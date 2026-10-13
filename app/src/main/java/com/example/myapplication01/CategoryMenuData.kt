package com.example.myapplication01

/**
 * UI2 入口選單與卡片標題共用字典數據
 * 確保入口點文字與渲染文字 100% 同步
 */
object CategoryMenuData {
    val MENU_ITEMS = mapOf(
        // 交通出行
        "MOTOR_RUNNER" to "機車跑腿\n急件順風",
        "TRUCK_FREIGHT" to "貨車搬運\n載貨接單",
        "TAXI_CARE" to "計程叫車\n長照接送",
        "MOTOR_REPAIR" to "機車維修\n保養洗車",

        // 住 / 行
        "RENTAL_INFO" to "租屋資訊",
        "DECORATION" to "裝潢修繕",
        "REAL_ESTATE" to "房產買賣",
        "PLUMBING_LOCK" to "水電鎖印",

        // 餐飲私廚
        "LOCAL_VENDOR" to "在地攤商\n個人私廚",

        // 公益關懷 (Section 11)
        "11-1" to "庇護手作\n愛心義賣",
        "11-2" to "物資募集\n舊物捐贈",
        "11-3" to "志工招募\n義工服務",
        "11-4" to "愛心餐食\n待用餐點",
        "11-5" to "浪浪救助\n中途送養",
        "11-6" to "社區關懷\n長者陪伴",
        "11-7" to "義剪義診\n資源援助",
        "11-8" to "環境保護\n淨灘義工"
    )

    // 對應清理後的顯示名稱，以防外部傳入的是原始名稱
    val NAME_MAPPING = mapOf(
        "機車跑腿急件順風" to "MOTOR_RUNNER",
        "貨車搬運載貨接單" to "TRUCK_FREIGHT",
        "計程叫車長照接送" to "TAXI_CARE",
        "機車維修保養洗車" to "MOTOR_REPAIR",
        "在地攤商個人私廚" to "LOCAL_VENDOR",
        "租屋資訊" to "RENTAL_INFO",
        "裝潢修繕" to "DECORATION",
        "房產買賣" to "REAL_ESTATE",
        "水電鎖印" to "PLUMBING_LOCK",
        "庇護手作愛心義賣" to "11-1",
        "物資募集舊物捐贈" to "11-2",
        "志工招募義工服務" to "11-3",
        "愛心餐食待用餐點" to "11-4",
        "浪浪救助中途送養" to "11-5",
        "社區關懷長者陪伴" to "11-6",
        "義剪義診資源援助" to "11-7",
        "環境保護淨灘義工" to "11-8"
    )
}
