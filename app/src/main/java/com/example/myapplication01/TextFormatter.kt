package com.example.myapplication01

import android.widget.TextView

object TextFormatter {
    /**
     * 直接 1:1 渲染 UI2 入口的文字，不做任何多餘字串處理
     */
    fun renderCardTitle(textView: TextView, categoryKey: String) {
        textView.text = CategoryMenuData.MENU_ITEMS[categoryKey] ?: categoryKey
    }

    /**
     * 全局標題自動格式化 logic
     * 1. 先移除所有斜線與空格
     * 2. 優先查字典 Key，若無對應則嘗試名稱對應，最後 fallback 自動斷行
     */
    fun formatCardTitle(rawTitle: String): String {
        // 先移除所有斜線、空格與既有換行
        val cleanedInput = rawTitle.replace("/", "").replace(" ", "").replace("\n", "")
        
        // 1. 優先查 Key (如果是傳入內部 Key)
        CategoryMenuData.MENU_ITEMS[rawTitle]?.let { return it }
        
        // 2. 查名稱對應 (如果是傳入清理後的名稱)
        val keyFromName = CategoryMenuData.NAME_MAPPING[cleanedInput]
        if (keyFromName != null) {
            CategoryMenuData.MENU_ITEMS[keyFromName]?.let { return it }
        }
        
        // 3. Fallback 自動斷行 (禁止自動切割字數，但若查不到且超過 4 字仍需保底)
        return when {
            cleanedInput.length > 4 -> "${cleanedInput.substring(0, 4)}\n${cleanedInput.substring(4)}"
            else -> cleanedInput
        }
    }
}
