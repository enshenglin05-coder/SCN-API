package com.example.myapplication01

/**
 * 第 12 大項：信仰祈福 (8 格子項目字典)
 * 鎖定 88 格獨立網格 4x4 / 2 排直向格式
 */
object Section12ReligionData {

    // 大項目標題
    const val SECTION_TITLE = "12. 信仰祈福"

    // 8 個按鈕項目 (Key -> UI2 按鈕顯示與卡片渲染標題)
    val ITEMS = mapOf(
        "12-1" to "土地公\n福德正神",  // 第 1 格
        "12-2" to "關聖帝君\n恩主公",    // 第 2 格
        "12-3" to "天上聖母\n媽祖",      // 第 3 格
        "12-4" to "基督教\n代禱祈禱",    // 第 4 格
        "12-5" to "佛教感應\n點燈祈福",  // 第 5 格
        "12-6" to "天主教\n祈禱祈福",    // 第 6 格
        "12-7" to "心靈平安\n普渡祈福",  // 第 7 格
        "12-8" to "綜合信仰\n靈修祈福"   // 第 8 格
    )
}
