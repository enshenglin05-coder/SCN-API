package com.example.myapplication01

import android.graphics.Color

object ThemeUtils {
    /**
     * 根據信仰類別取得主題色組合 (Primary, Accent, Text)
     */
    fun getColorsForCategory(category: ReligionCategory): Triple<String, String, String> {
        return when (category) {
            ReligionCategory.ELDER -> Triple("#FFD700", "#FFCC00", "#FFFFFF")
            ReligionCategory.COMPASSION -> Triple("#D8A7CA", "#C38EB4", "#FFFFFF")
            ReligionCategory.WARRIOR -> Triple("#4FC3F7", "#00E5FF", "#FFFFFF")
            ReligionCategory.WISDOM -> Triple("#E040FB", "#FF4081", "#FFFFFF")
            ReligionCategory.CHRISTIAN -> Triple("#FFFFFF", "#00E5FF", "#FFFFFF")
            ReligionCategory.BUDDHIST_AMBER -> Triple("#FFB74D", "#FF9800", "#FFFFFF")
            ReligionCategory.BUDDHIST_GOLD -> Triple("#FFD700", "#FFC107", "#FFFFFF")
            ReligionCategory.SHINTO -> Triple("#FF7043", "#FF5722", "#FFFFFF")
            ReligionCategory.JUDAISM -> Triple("#29B6F6", "#03A9F4", "#FFFFFF")
            ReligionCategory.ISLAM -> Triple("#81C784", "#4CAF50", "#FFFFFF")
            ReligionCategory.CATHOLIC_BLUE -> Triple("#81D4FA", "#2196F3", "#FFFFFF")
            ReligionCategory.CATHOLIC_GOLD -> Triple("#FFE082", "#FFD54F", "#FFFFFF")
            ReligionCategory.GENERAL -> Triple("#A8E6CF", "#C8E6C9", "#FFFFFF")
        }
    }
    
    fun parseColor(hex: String): Int {
        return try {
            Color.parseColor(hex)
        } catch (e: Exception) {
            Color.WHITE
        }
    }
}
