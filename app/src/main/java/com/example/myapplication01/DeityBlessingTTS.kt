package com.example.myapplication01

import android.content.Context
import java.util.Locale

/**
 * DeityBlessingTTS：專屬神明祝福語 TTS 服務模組
 * 負責連動 UI 選定主題與多國語系發音
 */
object DeityBlessingTTS {

    private var ttsManager: TtsManager? = null

    fun init(context: Context) {
        if (ttsManager == null) {
            ttsManager = TtsManager(context)
        }
    }

    fun updateLanguage() {
        val langCode = GlobalState.selectedLanguageCode
        val locale = when (langCode) {
            "zh-TW" -> Locale.TRADITIONAL_CHINESE
            "en" -> Locale.US
            "ja" -> Locale.JAPAN
            "ko" -> Locale.KOREA
            "vi" -> Locale("vi", "VN")
            else -> Locale.TRADITIONAL_CHINESE
        }
        ttsManager?.setLanguage(locale)
    }

    /**
     * 播放當前選定神明的祝福詞
     */
    fun speakCurrentBlessing(context: Context) {
        updateLanguage()
        
        // 1. 取得當前語系的所有卡片
        val cards = ReligionRepository.getCardsByLanguage(GlobalState.selectedLanguageCode)
        
        // 2. 尋找對應 ID 的卡片以取得正確的譯文 Resource ID
        val selectedId = GlobalState.selectedDeityId
        val card = cards.find { it.id == selectedId }
        
        // 3. 取得文字內容 (優先使用 ID 對應的翻譯，若無則回退至全域存儲或預設值)
        val blessingText = if (card != null) {
            context.getString(card.descResId)
        } else {
            GlobalState.selectedBlessingText ?: context.getString(R.string.msg_publish_success_default)
        }
        
        // 4. 執行異步非阻塞播報
        ttsManager?.speak(blessingText, utteranceId = "DEITY_BLESSING")
    }

    /**
     * 播放任意文字 (非阻塞)
     */
    fun speak(text: String) {
        updateLanguage()
        ttsManager?.speak(text, utteranceId = "GENERIC_FEEDBACK")
    }

    fun shutdown() {
        ttsManager?.shutdown()
        ttsManager = null
    }
}
