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
     * 編號矩陣驗證 (1-1 ~ 11-8)
     * 支援 11 個大項目 × 8 個小項目，確保全系統外殼模板的一致性
     */
    fun validateCategoryCode(code: String): Boolean {
        val pattern = Regex("^([1-9]|10|11)-[1-8]$")
        return pattern.matches(code)
    }

    /**
     * 格式化數據標籤 (內部使用，抹除 1A/1B 痕跡)
     */
    fun formatInternalTag(type: String, categoryName: String): String {
        // 返回純淨的標籤供 Data Model 綁定，但 RequirementAdapter 會進一步剝離顯示
        return "$type $categoryName"
    }
}
