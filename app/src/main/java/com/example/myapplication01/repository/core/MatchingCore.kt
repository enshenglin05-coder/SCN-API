package com.example.myapplication01.repository.core

/**
 * 百川 1.0 A/B 雙向記憶與 Schema 代碼封裝庫
 * 鎖定核心撮合邏輯與 72 格抽屜代碼規範
 */
object MatchingCore {

    // 底層 Payload 傳輸標籤 (嚴禁前端顯示)
    const val TYPE_A_DEMAND = "1A"
    const val TYPE_B_SUPPLY = "1B"

    /**
     * 交叉對齊邏輯判定 (A比B / B比A)
     * 當使用者為 A 時，其目標對手為 B，反之亦然
     */
    fun getTargetOpponentType(currentType: String): String {
        return if (currentType == TYPE_A_DEMAND) TYPE_B_SUPPLY else TYPE_A_DEMAND
    }

    /**
     * 88 格矩陣驗證 (1-1 ~ 11-8)
     * 嚴格遵循草圖直向排列規則：
     * 11 個大類，每個大類下 8 個小項（1-1~1-8, 2-1~2-8... 11-1~11-8）
     */
    fun validateCategoryCode(code: String): Boolean {
        val pattern = Regex("^([1-9]|10|11)-[1-8]$")
        return pattern.matches(code)
    }

    /**
     * 88 格格位索引轉換：將 11x8 結構轉換為內部獨立索引值
     * 滿足「兩排直向排列」需求：大類編號為 x，小項編號為 y (x=1..11, y=1..8)
     */
    fun getGridIndex(code: String): Int {
        val parts = code.split("-")
        if (parts.size != 2) return -1
        val x = parts[0].toIntOrNull() ?: 1
        val y = parts[1].toIntOrNull() ?: 1
        // 計算直向偏移：(x-1) * 8 + y
        return (x - 1) * 8 + y
    }

    /**
     * 格式化數據標籤 (內部使用，抹除 1A/1B 痕跡)
     */
    fun formatInternalTag(type: String, categoryName: String): String {
        // 返回純淨的標籤供 Data Model 綁定，但 RequirementAdapter 會進一步剝離顯示
        return "$type $categoryName"
    }
}
