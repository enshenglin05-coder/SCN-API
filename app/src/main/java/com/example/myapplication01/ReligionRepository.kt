package com.example.myapplication01

object ReligionRepository {
    fun getCardsByLanguage(langCode: String): List<ReligionCard> {
        return when (langCode) {
            "en" -> listOf(
                ReligionCard("en_1", R.string.religion_en_christian_title, R.string.religion_en_christian_desc, ReligionCategory.CHRISTIAN),
                ReligionCard("en_2", R.string.religion_en_catholic_title, R.string.religion_en_catholic_desc, ReligionCategory.CATHOLIC_GOLD),
                ReligionCard("en_3", R.string.religion_en_judaism_title, R.string.religion_en_judaism_desc, ReligionCategory.JUDAISM),
                ReligionCard("en_4", R.string.religion_en_islam_title, R.string.religion_en_islam_desc, ReligionCategory.ISLAM),
                ReligionCard("en_5", R.string.religion_en_general_title, R.string.religion_en_general_desc, ReligionCategory.GENERAL)
            )
            "ja" -> listOf(
                ReligionCard("ja_1", R.string.religion_ja_shinto_title, R.string.religion_ja_shinto_desc, ReligionCategory.SHINTO),
                ReligionCard("ja_2", R.string.religion_ja_buddhist_title, R.string.religion_ja_buddhist_desc, ReligionCategory.BUDDHIST_AMBER),
                ReligionCard("ja_3", R.string.religion_ja_christian_title, R.string.religion_ja_christian_desc, ReligionCategory.CHRISTIAN),
                ReligionCard("ja_4", R.string.religion_ja_catholic_title, R.string.religion_ja_catholic_desc, ReligionCategory.CATHOLIC_BLUE),
                ReligionCard("ja_5", R.string.religion_ja_general_title, R.string.religion_ja_general_desc, ReligionCategory.GENERAL)
            )
            "ko" -> listOf(
                ReligionCard("ko_1", R.string.religion_ko_protestant_title, R.string.religion_ko_protestant_desc, ReligionCategory.CHRISTIAN),
                ReligionCard("ko_2", R.string.religion_ko_catholic_title, R.string.religion_ko_catholic_desc, ReligionCategory.CATHOLIC_BLUE),
                ReligionCard("ko_3", R.string.religion_ko_buddhist_title, R.string.religion_ko_buddhist_desc, ReligionCategory.BUDDHIST_GOLD),
                ReligionCard("ko_4", R.string.religion_ko_general_title, R.string.religion_ko_general_desc, ReligionCategory.GENERAL)
            )
            "vi" -> listOf(
                ReligionCard("vi_1", R.string.religion_vi_buddhist_title, R.string.religion_vi_buddhist_desc, ReligionCategory.BUDDHIST_GOLD),
                ReligionCard("vi_2", R.string.religion_vi_catholic_title, R.string.religion_vi_catholic_desc, ReligionCategory.CHRISTIAN),
                ReligionCard("vi_3", R.string.religion_vi_caodai_title, R.string.religion_vi_caodai_desc, ReligionCategory.COMPASSION),
                ReligionCard("vi_4", R.string.religion_vi_general_title, R.string.religion_vi_general_desc, ReligionCategory.GENERAL)
            )
            else -> listOf( // zh-TW
                ReligionCard("tw_1", R.string.religion_tw_tudigong_title, R.string.religion_tw_tudigong_desc, ReligionCategory.ELDER),
                ReligionCard("tw_2", R.string.religion_tw_mazu_title, R.string.religion_tw_mazu_desc, ReligionCategory.COMPASSION),
                ReligionCard("tw_3", R.string.religion_tw_guangong_title, R.string.religion_tw_guangong_desc, ReligionCategory.WARRIOR),
                ReligionCard("tw_4", R.string.religion_tw_christian_title, R.string.religion_tw_christian_desc, ReligionCategory.CHRISTIAN),
                ReligionCard("tw_5", R.string.religion_tw_buddhist_title, R.string.religion_tw_buddhist_desc, ReligionCategory.BUDDHIST_AMBER),
                ReligionCard("tw_6", R.string.religion_tw_general_title, R.string.religion_tw_general_desc, ReligionCategory.GENERAL)
            )
        }
    }
}
