package com.example.myapplication01

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * TtsManager：TTS 語音引擎管理類別
 * 實作 TextToSpeech.OnInitListener，使用 Application Context 避免 Memory Leak 與引擎異常斷線 (Disconnected from TTS engine)
 */
class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.TAIWAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TtsManager", "語言不支援或缺少資料")
                isReady = false
            } else {
                isReady = true
                Log.d("TtsManager", "TTS 引擎初始化成功！")
            }
        } else {
            isReady = false
            Log.e("TtsManager", "TTS 初始化失敗, status=$status")
        }
    }

    /**
     * 切換 TTS 發音語系
     */
    fun setLanguage(locale: Locale): Boolean {
        if (!isReady || tts == null) {
            Log.w("TtsManager", "TTS 尚未準備就緒，無法設定語系")
            return false
        }
        val result = tts?.setLanguage(locale)
        val supported = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        if (!supported) {
            Log.e("TtsManager", "語言 $locale 不支援或缺少資料")
        }
        return supported
    }

    /**
     * 播報指定語音文字
     */
    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH, utteranceId: String = "TTS_ID") {
        if (isReady && tts != null) {
            try {
                tts?.speak(text, queueMode, null, utteranceId)
            } catch (e: Exception) {
                Log.e("TtsManager", "播報語音時發生異常: ${e.message}", e)
            }
        } else {
            Log.w("TtsManager", "TTS 尚未準備就緒，無法播報: $text")
        }
    }

    /**
     * 停止當前播報
     */
    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("TtsManager", "停止 TTS 播報失敗: ${e.message}", e)
        }
    }

    /**
     * 離開時或 Activity onDestroy 時正確釋放資源，防止 Disconnected 警告
     */
    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("TtsManager", "釋放 TTS 資源失敗: ${e.message}", e)
        } finally {
            tts = null
            isReady = false
            Log.d("TtsManager", "TTS 資源已完全釋放")
        }
    }

    fun isReady(): Boolean = isReady
}
