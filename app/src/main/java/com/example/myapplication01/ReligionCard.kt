package com.example.myapplication01

enum class ReligionCategory {
    ELDER,          // 福氣/長者型 (Gold: #FFD700)
    COMPASSION,     // 慈悲/女性型 (Rose Gold: #ECC5C0)
    WARRIOR,        // 剛正/武將型 (Sky Blue: #4FC3F7)
    WISDOM,         // 智慧/文昌型 (Violet: #E040FB)
    CHRISTIAN,      // 基督教/純白 (White: #FFFFFF)
    BUDDHIST_AMBER, // 佛教/琥珀橙 (Amber: #FFB74D)
    BUDDHIST_GOLD,  // 佛教/金黃 (Gold: #FFD700)
    SHINTO,         // 神道/橙色 (Shinto: #FF7043)
    JUDAISM,        // 猶太教/藍色 (Judaism: #29B6F6)
    ISLAM,          // 伊斯蘭教/綠色 (Islam: #81C784)
    CATHOLIC_BLUE,  // 天主教/淺藍 (Catholic Blue: #81D4FA)
    CATHOLIC_GOLD,  // 天主教/金黃 (Catholic Gold: #FFE082)
    GENERAL         // 中性/通用 (Macaron Green: #A8E6CF)
}

data class ReligionCard(
    val id: String,
    val nameResId: Int,
    val descResId: Int,
    val category: ReligionCategory = ReligionCategory.GENERAL
)
