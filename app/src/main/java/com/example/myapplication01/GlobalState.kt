package com.example.myapplication01

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object GlobalState {
    var globalLatitude: Double? = null
    var globalLongitude: Double? = null
    var isLocated: Boolean = false
    
    // 全域祝福詞與語系狀態
    var selectedBlessingText: String? = null
    var selectedDeityId: String? = null // 新增：記錄選定的神明 ID 以利多國語系連動
    var selectedLanguageCode: String = "zh-TW"

    // 流彩氛圍燈狀態 (使用 Compose State 以確保即時重繪)
    var isRainbowEnabled by mutableStateOf(false)

    // 全螢幕閃爍觸發器 (存儲點擊時的時間戳與顏色)
    var flashTrigger by mutableStateOf(0L)
    var flashColor by mutableStateOf(androidx.compose.ui.graphics.Color.White)
    
    // 當前選定的信仰專屬顏色 (Hex 格式)
    var selectedThemeColorHex by mutableStateOf("#00E5FF") // 主色 (Primary)
    var selectedThemeAccentColorHex by mutableStateOf("#00FFC2") // 強調色 (Accent)
    var selectedThemeTextColorHex by mutableStateOf("#FFFFFF") // 文字色 (Text)

    // 內部極密調控邏輯常數 (A比B / B比A 雙向對齊)
    const val TYPE_A_DEMAND = "1A"
    const val TYPE_B_SUPPLY = "1B"
}

