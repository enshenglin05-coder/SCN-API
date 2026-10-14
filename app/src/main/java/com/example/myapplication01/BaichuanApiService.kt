package com.example.myapplication01

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.myapplication01.BuildConfig
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 百川 1.0 API 傳輸中心 (GitHub 極簡架構)
 * 負責 GitHub 數據同步、金鑰管理與資料對齊，並處理 401/403 驗證過期自動重新登入
 */
object BaichuanApiService {

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        UserIdentityManager.init(context)
    }

    // GitHub API 標頭配置
    private const val HEADER_ACCEPT = "Accept"
    private const val VALUE_ACCEPT = "application/vnd.github+json"
    private const val HEADER_USER_AGENT = "User-Agent"
    private const val VALUE_USER_AGENT = "BaichuanApp"
    private const val HEADER_AUTH = "Authorization"

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val urlString = originalRequest.url.toString()

            val requestBuilder = originalRequest.newBuilder()
                .header(HEADER_ACCEPT, VALUE_ACCEPT)
                .header(HEADER_USER_AGENT, VALUE_USER_AGENT)
                .header("X-GitHub-Api-Version", "2022-11-28")

            // 【深度盤查修復】只有向 api.github.com 發送寫入/管理請求時才帶入 Authorization Bearer Token
            // 絕對不能向 raw.githubusercontent.com (GitHub CDN 靜態站點) 帶入 Authorization，否則 GitHub CDN 會回傳 401 Unauthorized
            if (urlString.contains("api.github.com")) {
                val authHeader = getAuthHeader()
                if (authHeader.isNotBlank()) {
                    requestBuilder.header(HEADER_AUTH, authHeader)
                    android.util.Log.d("BaichuanAPI", "🔒 已為 API 請求注入授權標頭: $urlString")
                }
            } else {
                android.util.Log.d("BaichuanAPI", "🌐 略過授權標頭 (公開 CDN/Raw 請求): $urlString")
            }

            val builtRequest = requestBuilder.build()
            android.util.Log.d("BaichuanAPI", "🚀 發送 HTTP 請求 | Method: ${builtRequest.method} | URL: ${builtRequest.url}")
            android.util.Log.d("BaichuanAPI", "📋 請求 Headers: ${builtRequest.headers}")

            var response = chain.proceed(builtRequest)
            android.util.Log.d("BaichuanAPI", "📥 收到 HTTP 回應 | URL: ${response.request.url} | 狀態碼: HTTP ${response.code}")

            if (response.code == 401 || response.code == 403) {
                android.util.Log.e("BaichuanAPI", "⚠️ API 響應 HTTP ${response.code} (Unauthorized / Token 無效)，請求網址: ${response.request.url}")

                // 若為 api.github.com 且帶有 Token 失敗，嘗試重新整理並重試一次
                if (urlString.contains("api.github.com")) {
                    val refreshed = UserIdentityManager.refreshAuthToken(appContext)
                    if (refreshed) {
                        val newAuthHeader = getAuthHeader()
                        if (newAuthHeader.isNotBlank()) {
                            response.close()
                            val retryRequest = originalRequest.newBuilder()
                                .header(HEADER_AUTH, newAuthHeader)
                                .header(HEADER_ACCEPT, VALUE_ACCEPT)
                                .header(HEADER_USER_AGENT, VALUE_USER_AGENT)
                                .header("X-GitHub-Api-Version", "2022-11-28")
                                .build()
                            android.util.Log.d("BaichuanAPI", "🔄 正在使用新 Token 重試請求: ${retryRequest.url}")
                            response = chain.proceed(retryRequest)
                            android.util.Log.d("BaichuanAPI", "📥 重試後回應狀態碼: HTTP ${response.code}")
                        }
                    }
                }

                if (response.code == 401 || response.code == 403) {
                    handleUnauthorizedError()
                }
            }

            response
        }
        .build()

    private val gson = Gson()

    private val GITHUB_URL = BuildConfig.GITHUB_API_URL
    // GitHub Contents API: 用於寫入分片檔案
    private val GITHUB_CONTENTS_BASE_URL = GITHUB_URL.replace("/issues", "/contents/data/")
    private const val RAW_DATA_BASE_URL = "https://raw.githubusercontent.com/enshenglin05-coder/SCN-API/main/data/"

    private const val HARDCODED_TOKEN = "ghp_6MlbFMVQUcYGsI6BMkxkxvPpbm2EFp2gEJgq"

    /**
     * 動態獲取 Authorization Header (優先使用硬編碼 Token，確保絕對授權成功)
     */
    fun getAuthHeader(): String {
        val token = HARDCODED_TOKEN
        try {
            android.util.Log.d("BaichuanAPI", "使用硬編碼 Token: ${token.take(10)}...")
        } catch (_: Throwable) {
            // Ignored in unit test environment
        }
        return "Bearer $token"
    }

    /**
     * 當發生 401 / 403 錯誤時的錯誤處理邏輯，僅提示錯誤而不強制跳轉或重置條款
     */
    private fun handleUnauthorizedError() {
        android.util.Log.e("BaichuanAPI", "⛔ 身份驗證失敗 (401 Unauthorized / 403 Forbidden)，請檢查 Token 設定")
        Handler(Looper.getMainLooper()).post {
            appContext?.let { ctx ->
                Toast.makeText(ctx, "API 驗證失敗 (401 Unauthorized)，請檢查 Token 設定", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * 發布卡片數據 (Targeted File Write)
     * 寫入目標端點: https://api.github.com/repos/enshenglin05-coder/SCN-API/contents/data/<category_code>.json
     */
    fun publishCard(card: RequirementCard, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val categoryCode = card.category_code
        if (!Grid88Formatter.validateGridCode(categoryCode)) {
            android.util.Log.e("BaichuanAPI", "❌ 發布失敗：無效的 88 格編碼 $categoryCode")
            onError("Invalid 88-grid category code: $categoryCode")
            return
        }

        val fileName = "$categoryCode.json"
        val contentsUrl = "$GITHUB_CONTENTS_BASE_URL$fileName"
        android.util.Log.d("BaichuanAPI", "🚀 準備發布卡片至 SCN-API 88格資料路由: $contentsUrl (類別代碼: $categoryCode)")

        val getRequest = Request.Builder()
            .url(contentsUrl)
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
                    sha = null
                } else if (code == 401 || code == 403) {
                    onError("401 Unauthorized")
                    return
                } else {
                    android.util.Log.e("BaichuanAPI", "Publish/Get 錯誤 HTTP $code: $bodyString")
                    onError("HTTP $code: $bodyString")
                    return
                }

                cards.removeAll { it.publisher_device_id == card.publisher_device_id && it.drawer_type == card.drawer_type }
                cards.add(card)

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

        val request = Request.Builder()
            .url(url)
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
     */
    fun fetchCardsByCategory(
        categoryCode: String,
        onResult: (List<RequirementCard>) -> Unit
    ) {
        val url = "$RAW_DATA_BASE_URL$categoryCode.json"

        val request = Request.Builder()
            .url(url)
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
                    if (code != 404 && code != 401 && code != 403) {
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

        val request = Request.Builder()
            .url(GITHUB_URL)
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

        val getRequest = Request.Builder()
            .url(contentsUrl)
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
                } else if (code == 401 || code == 403) {
                    onError("401 Unauthorized")
                } else {
                    android.util.Log.e("BaichuanAPI", "Delete/Get 錯誤 HTTP $code: $bodyString")
                    onError("HTTP $code: $bodyString")
                }
            }
        })
    }
}
