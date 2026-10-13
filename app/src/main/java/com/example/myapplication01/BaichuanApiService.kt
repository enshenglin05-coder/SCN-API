package com.example.myapplication01

import com.example.myapplication01.BuildConfig
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 百川 1.0 API 傳輸中心 (GitHub 極簡架構)
 * 負責 GitHub 數據同步、金鑰管理與資料對齊
 */
object BaichuanApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    
    // GitHub API 標頭配置
    private const val HEADER_ACCEPT = "Accept"
    private const val VALUE_ACCEPT = "application/vnd.github+json"
    private const val HEADER_USER_AGENT = "User-Agent"
    private const val VALUE_USER_AGENT = "BaichuanApp"
    private const val HEADER_AUTH = "Authorization"
    
    private val GITHUB_URL = BuildConfig.GITHUB_API_URL
    // GitHub Contents API: 用於寫入分片檔案
    private val GITHUB_CONTENTS_BASE_URL = GITHUB_URL.replace("/issues", "/contents/data/")
    private const val RAW_DATA_BASE_URL = "https://raw.githubusercontent.com/enshenglin05-coder/SCN-API/main/data/"
    
    private val AUTH_VALUE: String by lazy { 
        val token = BuildConfig.GITHUB_TOKEN
        if (token.isBlank()) {
            android.util.Log.w("BaichuanAPI", "⚠️ GITHUB_TOKEN 未設定或為空！請在 local.properties 中設定 GITHUB_TOKEN=your_token，否則 GitHub API 寫入會失敗 (401/403)")
        }
        "Bearer $token" 
    }

    /**
     * 發布卡片數據 (Targeted File Write)
     * 邏輯：先嘗試獲取該類別的 JSON 檔案，若存在則追加，若不存在 (404) 則新建
     */
    fun publishCard(card: RequirementCard, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val categoryCode = card.category_code
        val fileName = "$categoryCode.json"
        val contentsUrl = "$GITHUB_CONTENTS_BASE_URL$fileName"

        // 1. 先獲取文件資訊 (需取得 SHA 才能更新檔案)
        val auth = AUTH_VALUE
        val getRequest = Request.Builder()
            .url(contentsUrl)
            .addHeader(HEADER_ACCEPT, VALUE_ACCEPT)
            .addHeader(HEADER_USER_AGENT, VALUE_USER_AGENT)
            .addHeader(HEADER_AUTH, auth)
            .get()
            .build()

        client.newCall(getRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                android.util.Log.e("BaichuanAPI", "Publish/Get 失敗: ${e.message}")
                onError(e.message ?: "Network Connection Error")
            }

            override fun onResponse(call: Call, response: Response) {
                val code = response.code
                val bodyString = response.body?.string() ?: ""
                response.close()

                var sha: String? = null
                val cards = mutableListOf<RequirementCard>()

                if (response.isSuccessful) {
                    // 文件存在，解析內容與 SHA
                    try {
                        val fileInfo = gson.fromJson(bodyString, Map::class.java)
                        sha = fileInfo["sha"] as? String
                        val base64Content = fileInfo["content"] as? String ?: ""
                        val decoded = android.util.Base64.decode(base64Content.replace("\n", ""), android.util.Base64.DEFAULT)
                        val json = String(decoded)
                        val existingCards = gson.fromJson(json, Array<RequirementCard>::class.java)
                        cards.addAll(existingCards)
                    } catch (e: Exception) {
                        android.util.Log.e("BaichuanAPI", "解析現有檔案失敗: ${e.message}")
                    }
                } else if (code == 404) {
                    // 檔案尚不存在，正常建立新檔案
                    sha = null
                } else {
                    android.util.Log.e("BaichuanAPI", "Publish/Get 錯誤 HTTP $code: $bodyString")
                    onError("HTTP $code: $bodyString")
                    return
                }

                // 2. 追加新卡片並去重 (同一發布者在同一類別僅保留一筆)
                cards.removeAll { it.publisher_device_id == card.publisher_device_id && it.drawer_type == card.drawer_type }
                cards.add(card)

                // 3. 寫回 GitHub
                writeToGitHub(contentsUrl, cards, sha, onSuccess, onError)
            }
        })
    }

    private fun writeToGitHub(url: String, cards: List<RequirementCard>, sha: String?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val json = gson.toJson(cards)
        val contentBase64 = android.util.Base64.encodeToString(json.toByteArray(), android.util.Base64.NO_WRAP)
        
        val payload = mutableMapOf(
            "message" to "Update drawer data: ${System.currentTimeMillis()}",
            "content" to contentBase64
        )
        sha?.let { payload["sha"] = it }

        val body = gson.toJson(payload).toRequestBody("application/json".toMediaType())
        val auth = AUTH_VALUE

        val request = Request.Builder()
            .url(url)
            .addHeader(HEADER_ACCEPT, VALUE_ACCEPT)
            .addHeader(HEADER_USER_AGENT, VALUE_USER_AGENT)
            .addHeader(HEADER_AUTH, auth)
            .put(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                android.util.Log.e("BaichuanAPI", "Write to GitHub 失敗: ${e.message}")
                onError(e.message ?: "Write Error")
            }
            override fun onResponse(call: Call, response: Response) {
                val code = response.code
                val errorBody = response.body?.string() ?: ""
                response.close()
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    android.util.Log.e("BaichuanAPI", "Write to GitHub 錯誤 HTTP $code: $errorBody")
                    onError("HTTP $code: $errorBody")
                }
            }
        })
    }

    /**
     * 精準請求單一抽屜檔案 (Drawer-based API Fetch)
     * URL: .../data/{category_code}.json
     */
    fun fetchCardsByCategory(
        categoryCode: String,
        onResult: (List<RequirementCard>) -> Unit
    ) {
        val url = "$RAW_DATA_BASE_URL$categoryCode.json"

        val request = Request.Builder()
            .url(url)
            .addHeader(HEADER_USER_AGENT, VALUE_USER_AGENT)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                android.util.Log.e("BaichuanAPI", "Targeted Fetch 失敗 [$categoryCode]: ${e.message}")
                onResult(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string() ?: ""
                val code = response.code
                if (response.isSuccessful) {
                    try {
                        val cards = gson.fromJson(bodyString, Array<RequirementCard>::class.java).toList()
                        onResult(cards)
                    } catch (e: Exception) {
                        android.util.Log.e("BaichuanAPI", "Targeted Parse 錯誤 [$categoryCode]: ${e.message}")
                        onResult(emptyList())
                    }
                } else {
                    if (code != 404) {
                        android.util.Log.w("BaichuanAPI", "Targeted Fetch 響應錯誤 [$categoryCode]: $code")
                    }
                    onResult(emptyList())
                }
                response.close()
            }
        })
    }

    /**
     * 交叉抽屜並行請求 (Multi-Drawer Cross Fetch)
     * 同時抓取多個類別檔案並進行結果合併
     */
    fun fetchCardsByCategories(
        categoryCodes: List<String>,
        onResult: (List<RequirementCard>) -> Unit
    ) {
        if (categoryCodes.isEmpty()) {
            onResult(emptyList())
            return
        }

        val allFetchedCards = java.util.Collections.synchronizedList(mutableListOf<RequirementCard>())
        val totalCount = categoryCodes.size
        var completedCount = 0

        categoryCodes.forEach { code ->
            fetchCardsByCategory(code) { cards ->
                allFetchedCards.addAll(cards)
                synchronized(allFetchedCards) {
                    completedCount++
                    if (completedCount == totalCount) {
                        onResult(allFetchedCards.toList())
                    }
                }
            }
        }
    }

    /**
     * 從任意 Git Raw JSON URL 動態下載卡片清單 (Git Raw Fetcher)
     */
    fun refreshCardsFromGit(
        gitRawUrl: String,
        onSuccess: (List<RequirementCard>) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = Request.Builder()
            .url(gitRawUrl)
            .addHeader(HEADER_USER_AGENT, VALUE_USER_AGENT)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                android.util.Log.e("GitError", "連線 Git 失敗: ${e.message}")
                onError(e.message ?: "Network Error")
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string()
                if (!bodyString.isNullOrEmpty()) {
                    try {
                        val cardList = DrawerBatchTester.parseDrawerJson(bodyString)
                        BaichuanRepository.injectTestCards(cardList)
                        onSuccess(cardList)
                    } catch (e: Exception) {
                        android.util.Log.e("GitError", "解析 Git 卡片失敗: ${e.message}")
                        onError(e.message ?: "Parse Error")
                    }
                } else {
                    onError("Empty response body")
                }
                response.close()
            }
        })
    }

    /**
     * 檢舉卡片紀錄
     */
    fun reportCard(cardId: String, reason: String, reporterId: String) {
        val reportData = mapOf(
            "title" to "【REPORT】$cardId",
            "body" to "Reason: $reason\nReporter: $reporterId\nTS: ${System.currentTimeMillis()}"
        )
        
        val auth = AUTH_VALUE

        val request = Request.Builder()
            .url(GITHUB_URL)
            .addHeader(HEADER_ACCEPT, VALUE_ACCEPT)
            .addHeader(HEADER_USER_AGENT, VALUE_USER_AGENT)
            .addHeader(HEADER_AUTH, auth)
            .post(gson.toJson(reportData).toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                android.util.Log.e("BaichuanAPI", "Report Card 失敗: ${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    android.util.Log.w("BaichuanAPI", "Report Card 響應錯誤 HTTP ${response.code}")
                }
                response.close()
            }
        })
    }

    /**
     * 全網下架刪除訊號 (Targeted File Delete/Update)
     */
    fun deleteCard(card: RequirementCard, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val categoryCode = card.category_code
        val fileName = "$categoryCode.json"
        val contentsUrl = "$GITHUB_CONTENTS_BASE_URL$fileName"
        
        val auth = AUTH_VALUE
        val getRequest = Request.Builder()
            .url(contentsUrl)
            .addHeader(HEADER_ACCEPT, VALUE_ACCEPT)
            .addHeader(HEADER_USER_AGENT, VALUE_USER_AGENT)
            .addHeader(HEADER_AUTH, auth)
            .get()
            .build()

        client.newCall(getRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                android.util.Log.e("BaichuanAPI", "Delete/Get 失敗: ${e.message}")
                onError(e.message ?: "Network Error")
            }
            override fun onResponse(call: Call, response: Response) {
                val bodyString = response.body?.string() ?: ""
                val code = response.code
                val isSuccessful = response.isSuccessful
                response.close()

                if (isSuccessful) {
                    try {
                        val fileInfo = gson.fromJson(bodyString, Map::class.java)
                        val sha = fileInfo["sha"] as? String
                        val base64Content = fileInfo["content"] as? String ?: ""
                        val decoded = android.util.Base64.decode(base64Content.replace("\n", ""), android.util.Base64.DEFAULT)
                        val json = String(decoded)
                        val existingCards = gson.fromJson(json, Array<RequirementCard>::class.java).toMutableList()
                        
                        val removed = existingCards.removeAll { it.card_id == card.card_id }
                        if (removed) {
                            writeToGitHub(contentsUrl, existingCards, sha, onSuccess, onError)
                        } else {
                            onSuccess()
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("BaichuanAPI", "Delete Parse 錯誤: ${e.message}")
                        onError("Parse Error: ${e.message}")
                    }
                } else if (code == 404) {
                    onSuccess()
                } else {
                    android.util.Log.e("BaichuanAPI", "Delete/Get 錯誤 HTTP $code: $bodyString")
                    onError("HTTP $code: $bodyString")
                }
            }
        })
    }
}
