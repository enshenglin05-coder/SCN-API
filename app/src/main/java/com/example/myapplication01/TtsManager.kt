package com.example.myapplication01

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.TAIWAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TtsManager", "語言不支援或缺少資料")
            } else {
                isReady = true
                Log.d("TtsManager", "TTS 引擎初始化成功！")
            }
        } else {
            Log.e("TtsManager", "TTS 初始化失敗")
        }
    }

    // 播報高頻外送通知與廣告冠名
    fun speak(text: String) {
        if (isReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_ID")
        } else {
            Log.w("TtsManager", "TTS 尚未準備就緒，無法播報: $text")
        }
    }

    // 離開時正確釋放資源，避免 Disconnected 警告
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        isReady = false
    }
}
