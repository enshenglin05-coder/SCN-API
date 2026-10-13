package com.example.myapplication01

data class CategoryItem(
    val id: String,
    val displayName: String,
    val description: String = ""
)

object CategoryRepository {
    
    val foodCategoryList = createList("1", listOf("在地攤商 / 個人私廚", "在地小吃 / 便當快餐", "火鍋燒烤 / 牛排鐵板", "熱炒合菜 / 中式餐廳", "異國料理 / 日韓泰法", "早午輕食 / 咖啡甜點", "手搖飲料 / 冰品甜湯", "宵夜炸物 / 酒吧滷味"))
    val clothingCategoryList = createList("2", listOf("潮流服飾", "鞋類精品", "飾品配件", "運動戶外", "內衣睡衣", "童裝孕婦", "洗滌二手", "綜合其它"))
    val housingCategoryList = createList("3", listOf("租屋資訊", "房產買賣", "裝潢修繕", "水電鎖印", "清潔家政", "家具家電", "搬家儲藏", "綜合其它"))
    val transportCategoryList = createList("4", listOf("機車跑腿 / 急件順風", "計程叫車 / 長照接送", "貨車搬運 / 載貨接單", "機車維修 / 保養洗車", "汽車修護 / 美容輪胎", "拖吊救援 / 代駕服務", "租車服務 / 共享出行", "綜合其它 / 出行站點"))
    val educationCategoryList = createList("5", listOf("幼兒托育 / 保母家教", "補習輔導 / 升學檢定", "才藝培訓 / 音樂美術", "運動健身 / 私人教練", "語言學習 / 留學檢定", "職業技能 / 證照培訓", "駕訓考照 / 汽機車班", "綜合其它 / 學習資訊"))
    val entertainmentCategoryList = createList("6", listOf("KTV 視聽 / 酒吧夜生活", "按摩 SPA / 美容美體", "影城劇院 / 展覽演出", "網咖電競 / 桌遊密室", "親子樂園 / 戶外休閒", "運動場館 / 保齡球館", "美甲美睫 / 美髮理容", "綜合其它 / 休閒票券"))
    val medicalCategoryList = createList("7", listOf("藥局藥品 / 保健送藥", "診所門診 / 掛號諮詢", "看護照顧 / 居家照護", "醫美整形 / 皮膚管理", "健檢中心 / 醫事檢驗", "復健物理 / 中醫針灸", "輔具租售 / 醫療器材", "綜合其它 / 健康諮詢"))
    val secondhandCategoryList = createList("8", listOf("二手手機 / 數碼 3C", "汽機買賣 / 二輪四輪", "家電傢俱 / 居家閒置", "精品服飾 / 包款古著", "母嬰用品 / 玩具童車", "遊戲電玩 / 動漫周邊", "戶外裝備 / 運動器材", "綜合閒置 / 在地面交"))
    val petCategoryList = createList("9", listOf("寵物洗澡 / 美容造型", "寵物寄養 / 日託安親", "獸醫門診 / 急診疫苗", "寵物接送 / 專車跑腿", "到府餵食 / 代遛寵物", "寵物用品 / 主糧零食", "走失協尋 / 送養領養", "綜合其它 / 寵物生命"))
    val serviceCategoryList = createList("10", listOf("臨時短工 / 活動人力", "家事清潔 / 消毒除蟲", "專業修繕 / 裝潢水電", "代辦跑腿 / 現場排隊", "活動企劃 / 攝影主持", "專業諮詢 / 代寫翻譯", "緊急求助 / 在地互助", "綜合服務 / 其它幫忙"))
    
    // 第 11 個大項目：公益關懷 (Charity Care)
    val reservedCategoryList = createList("11", listOf(
        "庇護手作 / 愛心義賣", 
        "物資募集 / 舊物捐贈", 
        "志工招募 / 義工服務", 
        "愛心餐食 / 待用餐點", 
        "浪浪救助 / 中途送養", 
        "社區關懷 / 長者陪伴", 
        "義剪義診 / 資源援助", 
        "環境保護 / 淨灘義工"
    ))

    private fun createList(mainId: String, names: List<String>): List<CategoryItem> {
        return names.mapIndexed { index, name ->
            CategoryItem("$mainId-${index + 1}", name)
        }
    }

    fun getSubItems(categoryIndex: String): List<CategoryItem> {
        return when (categoryIndex) {
            "1" -> foodCategoryList
            "2" -> clothingCategoryList
            "3" -> housingCategoryList
            "4" -> transportCategoryList
            "5" -> educationCategoryList
            "6" -> entertainmentCategoryList
            "7" -> medicalCategoryList
            "8" -> secondhandCategoryList
            "9" -> petCategoryList
            "10" -> serviceCategoryList
            "11" -> reservedCategoryList
            else -> emptyList()
        }
    }

    fun getAllCategoryIds(): List<String> {
        return (1..11).flatMap { main ->
            (1..8).map { sub -> "$main-$sub" }
        }
    }

    /**
     * 獲取小項目標題文字 (drawer_title)
     * 用於動態 UI 文字對齊
     */
    fun getCategoryTitle(categoryCode: String): String {
        val parts = categoryCode.split("-")
        if (parts.size != 2) return "未知項目"
        val main = parts[0]
        val subIndex = parts[1].toIntOrNull()?.minus(1) ?: return "未知項目"
        
        val list = getSubItems(main)
        return if (subIndex in list.indices) list[subIndex].displayName else "預留項目"
    }
}
