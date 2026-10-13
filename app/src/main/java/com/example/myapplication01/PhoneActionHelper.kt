package com.example.myapplication01

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.net.URLDecoder

/**
 * PhoneActionHelper：電話動作輔助工具類
 * 專責處理電話號碼提取與觸發 Android 原生電話撥號器 (Intent.ACTION_DIAL)
 */
object PhoneActionHelper {

    private fun parseQueryParam(url: String, paramName: String): String? {
        val searchPattern = "$paramName="
        val idx = url.indexOf(searchPattern, ignoreCase = true)
        if (idx != -1) {
            val sub = url.substring(idx + searchPattern.length)
            val end = sub.indexOfAny(charArrayOf('&', '#', ' '))
            return if (end != -1) sub.substring(0, end) else sub
        }
        return null
    }

    /**
     * 判斷給定的 URL / 字串是否代表電話撥號或帶有電話搜尋特徵
     */
    fun isTelUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val trimmed = url.trim()

        // 1. 直接通訊協定
        if (trimmed.startsWith("tel:", ignoreCase = true) ||
            trimmed.startsWith("tel://", ignoreCase = true) ||
            trimmed.startsWith("intent:", ignoreCase = true) ||
            trimmed.contains("scheme=tel", ignoreCase = true)
        ) {
            return true
        }

        // 2. 解碼 URL (catch %3A, %2F 等 URL 編碼)
        val decoded = try {
            URLDecoder.decode(trimmed, "UTF-8")
        } catch (_: Exception) {
            trimmed
        }

        if (trimmed.contains("tel:", ignoreCase = true) ||
            trimmed.contains("tel%3a", ignoreCase = true) ||
            trimmed.contains("tel%3A", ignoreCase = true) ||
            decoded.contains("tel:", ignoreCase = true)
        ) {
            return true
        }

        // 3. 搜尋 URL (包含 google.com/search 或 search? 且帶有 tel: 或純電話數字)
        if (trimmed.contains("search", ignoreCase = true) || trimmed.contains("q=", ignoreCase = true)) {
            val qParam = parseQueryParam(trimmed, "q") ?: parseQueryParam(trimmed, "query")
            val uri = try { Uri.parse(trimmed) } catch (_: Exception) { null }
            val q = qParam ?: uri?.getQueryParameter("q") ?: uri?.getQueryParameter("query")
            if (!q.isNullOrBlank()) {
                val decodedQ = try { URLDecoder.decode(q, "UTF-8") } catch (_: Exception) { q }
                if (decodedQ.contains("tel:", ignoreCase = true) || decodedQ.startsWith("tel", ignoreCase = true)) {
                    return true
                }
                val cleanQ = cleanPhoneNumber(decodedQ)
                if (cleanQ.length in 7..15) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * 從各式 URL (包含 tel:09xx, Google 搜尋 q=tel:09xx, url-encoded tel%3A09xx) 中精確提煉純號碼
     */
    fun extractPhoneNumberFromUrl(url: String?): String {
        if (url.isNullOrBlank()) return ""
        val trimmed = url.trim()
        val decoded = try {
            URLDecoder.decode(trimmed, "UTF-8")
        } catch (_: Exception) {
            trimmed
        }

        // 1. 若解碼後包含 tel:
        val index = decoded.indexOf("tel:", ignoreCase = true)
        if (index != -1) {
            val telPart = decoded.substring(index + 4)
            val end = telPart.indexOfAny(charArrayOf('&', '?', '#', '/', ' '))
            val rawNum = if (end != -1) telPart.substring(0, end) else telPart
            val cleaned = cleanPhoneNumber(rawNum)
            if (cleaned.isNotEmpty()) return cleaned
        }

        // 2. 若為搜尋 URL 帶 q 參數
        val qParam = parseQueryParam(trimmed, "q") ?: parseQueryParam(trimmed, "query")
        val uri = try { Uri.parse(trimmed) } catch (_: Exception) { null }
        val q = qParam ?: uri?.getQueryParameter("q") ?: uri?.getQueryParameter("query")
        if (!q.isNullOrBlank()) {
            val decodedQ = try { URLDecoder.decode(q, "UTF-8") } catch (_: Exception) { q }
            val cleanQ = cleanPhoneNumber(decodedQ)
            if (cleanQ.isNotEmpty()) return cleanQ
        }

        // 3. 兜底策略
        return cleanPhoneNumber(decoded)
    }

    /**
     * 清理並提取純電話號碼 (處理 intent://, tel://, tel: 及各種斜線與非數字雜訊)
     */
    fun cleanPhoneNumber(phoneStr: String): String {
        var str = phoneStr.trim()
        if (str.startsWith("intent://", ignoreCase = true) || str.startsWith("intent:", ignoreCase = true)) {
            val endIdx = str.indexOf('#')
            str = if (endIdx != -1) {
                str.substring(0, endIdx)
            } else {
                str
            }
            val schemeIdx = str.indexOf("scheme=tel", ignoreCase = true)
            if (schemeIdx != -1) {
                str = str.substring(0, schemeIdx)
            }
        }

        // 逐一清除通訊協定頭與斜線
        str = str.removePrefix("intent://").removePrefix("INTENT://")
            .removePrefix("intent:").removePrefix("INTENT:")
            .removePrefix("tel://").removePrefix("TEL://")
            .removePrefix("tel:").removePrefix("TEL:")
            .removePrefix("//")

        // 留下純數字與國際號碼字元 '+'
        return str.replace(Regex("[^0-9+]"), "")
    }

    /**
     * 從各式資料形態 (String / Map / JsonObject / JsonElement) 中提取純電話號碼
     */
    fun extractPhoneNumber(phoneAny: Any?): String? {
        if (phoneAny == null) return null
        val rawNumber = when (phoneAny) {
            is String -> phoneAny
            is Map<*, *> -> {
                val num = phoneAny["number"] ?: phoneAny["val"] ?: phoneAny["phone"]
                num as? String
            }
            is JsonObject -> {
                val num = phoneAny.get("number")?.asString
                    ?: phoneAny.get("val")?.asString
                    ?: phoneAny.get("phone")?.asString
                num
            }
            is JsonElement -> {
                if (phoneAny.isJsonPrimitive && phoneAny.asJsonPrimitive.isString) {
                    phoneAny.asString
                } else if (phoneAny.isJsonObject) {
                    extractPhoneNumber(phoneAny.asJsonObject)
                } else {
                    null
                }
            }
            else -> phoneAny.toString()
        } ?: return null

        val cleaned = cleanPhoneNumber(rawNumber)
        return cleaned.ifEmpty { null }
    }

    /**
     * 全域安全 Intent 啟動攔截器：強制檢查所有 Intent/URL，只要包含電話特徵或特定號碼，一律轉發給 openDialer！
     */
    fun safeStartActivity(context: Context, intent: Intent): Boolean {
        val action = intent.action
        val data = intent.dataString ?: intent.data?.toString()
        val extraText = intent.getStringExtra(Intent.EXTRA_TEXT)
        val combined = "$action $data $extraText"

        if (combined.contains("tel:", ignoreCase = true) ||
            combined.contains("search?q=tel:", ignoreCase = true) ||
            combined.contains("google.com/search", ignoreCase = true) ||
            isTelUrl(combined)
        ) {
            val pureNumbers = extractPhoneNumberFromUrl(combined).ifEmpty { combined.replace(Regex("[^0-9+]"), "") }
            if (pureNumbers.isNotEmpty()) {
                openDialer(context, pureNumbers)
                return true
            }
        }
        return false
    }

    /**
     * 為指定的 WebView 綁定 tel: 與 intent: 撥號攔截器 (AppWebViewClient 與 WebChromeClient)
     * 確保點擊電話連結或在新視窗開啟時直接發射原生 Intent.ACTION_DIAL 喚醒系統電話撥號器
     */
    fun attachToWebView(webView: android.webkit.WebView) {
        val ctx = webView.context
        val appClient = AppWebViewClient(ctx)

        @Suppress("SetJavaScriptEnabled")
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportMultipleWindows(false)
        webView.settings.javaScriptCanOpenWindowsAutomatically = true

        val appInterface = WebAppInterface(ctx)
        webView.addJavascriptInterface(appInterface, "AndroidBridge")
        webView.addJavascriptInterface(appInterface, "Android")

        webView.webViewClient = appClient
        webView.webChromeClient = object : android.webkit.WebChromeClient() {
            override fun onCreateWindow(
                view: android.webkit.WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: android.os.Message?
            ): Boolean {
                return false
            }

            override fun onProgressChanged(view: android.webkit.WebView?, newProgress: Int) {
                val currentUrl = view?.url
                if (currentUrl != null && (
                    currentUrl.contains("tel:", ignoreCase = true) ||
                    currentUrl.contains("search?q=tel:", ignoreCase = true) ||
                    isTelUrl(currentUrl)
                )) {
                    view.stopLoading()
                    openDialer(view.context, currentUrl)
                }
                super.onProgressChanged(view, newProgress)
            }
        }
    }

    /**
     * 開啟外部網址進入點：強制最上方死攔截所有包含 tel: 或搜尋格式 tel 的 URI，直接調用卡片標準 openDialer 邏輯！
     */
    fun openExternalUrl(context: Context, url: String) {
        val trimmed = url.trim()
        val dummyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(trimmed))
        if (safeStartActivity(context, dummyIntent)) {
            return
        }

        openUrl(context, trimmed)
    }

    /**
     * 全域開啟 URL 進入點：直接調用卡片標準 openDialer 邏輯！
     */
    fun openUrl(context: Context, url: String) {
        val trimmed = url.trim()
        val dummyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(trimmed))
        if (safeStartActivity(context, dummyIntent)) {
            return
        }
        // 原本開啟網頁的邏輯...
        try {
            val webView = android.webkit.WebView(context).apply {
                attachToWebView(this)
                loadUrl(trimmed)
            }

            androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("外部網頁預覽")
                .setView(webView)
                .setPositiveButton(context.getString(R.string.confirm), null)
                .show()
        } catch (e: Exception) {
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(trimmed)))
            } catch (_: Exception) {
                Toast.makeText(context, context.getString(R.string.error_open_link), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 開啟 Android 系統原生電話撥號器 (Intent.ACTION_DIAL)
     * 徹底去除斜線與雜訊，使用 Uri.parse("tel:$cleanNumber") 確保啟動 Android 原生電話撥號畫面
     */
    fun openDialer(context: Context, phoneAny: Any?) {
        val rawStr = phoneAny as? String
        val cleanNumber = if (rawStr != null && isTelUrl(rawStr)) {
            extractPhoneNumberFromUrl(rawStr)
        } else {
            extractPhoneNumber(phoneAny)
        }

        if (cleanNumber.isNullOrEmpty()) return

        try {
            val dialUri = Uri.parse("tel:$cleanNumber")
            val intent = Intent(Intent.ACTION_DIAL, dialUri).apply {
                if (context !is Activity) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                ContactUtils.copyToClipboard(context, cleanNumber)
                Toast.makeText(context, "無法開啟撥號程式，已複製電話：$cleanNumber", Toast.LENGTH_SHORT).show()
            } catch (_: Exception) {
            }
        }
    }

    /**
     * 開啟 Android 系統原生電話撥號器 (字串專屬多載)
     */
    fun openDialer(context: Context, phoneNumber: String?) {
        openDialer(context, phoneNumber as Any?)
    }

    /**
     * 喚起原生撥號盤（純本地簡短別名）
     */
    fun dial(context: Context, phoneNumber: String?) {
        openDialer(context, phoneNumber)
    }
}
