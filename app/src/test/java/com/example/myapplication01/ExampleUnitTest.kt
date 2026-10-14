package com.example.myapplication01

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testDrawer1_1_ParsingAndLoading() {
        val testCards = DrawerBatchTester.getTestCardsForDrawer11()
        assertEquals(20, testCards.size)
        
        val requirements = testCards.filter { it.drawer_type == "1A" }
        val supplies = testCards.filter { it.drawer_type == "1B" }
        assertEquals(10, requirements.size)
        assertEquals(10, supplies.size)

        // Verify specific card properties
        val firstCard = testCards.first()
        assertEquals("TEST_DRAWER_1_1_001", firstCard.card_id)
        assertEquals("1-1", firstCard.category_code)
        assertEquals("1A", firstCard.drawer_type)
        assertEquals(RequirementCard.ContactMethod.PHONE, firstCard.contact_method)
        assertEquals("0912345678", firstCard.contact_value)
        assertEquals(25.002653, firstCard.gps_lat, 0.000001)
        assertEquals(121.497685, firstCard.gps_lng, 0.000001)

        // Verify Repository integration
        BaichuanRepository.seedTestDrawerData()
        val cardsFromRepo = BaichuanRepository.getFilteredCards(
            userLat = 25.0026,
            userLng = 121.4976,
            categoryCodes = listOf("1-1"),
            targetType = "1A",
            currentDeviceId = "USER_TEST_DEVICE",
            limitDistanceKm = 50.0
        )
        assertTrue(cardsFromRepo.isNotEmpty())
    }

    @Test
    fun testDrawer4_1_ParsingAndLoading() {
        val testCards = DrawerBatchTester.getTestCardsForDrawer41()
        assertEquals(40, testCards.size)
        
        val requirements = testCards.filter { it.drawer_type == "1A" }
        val supplies = testCards.filter { it.drawer_type == "1B" }
        assertEquals(20, requirements.size)
        assertEquals(20, supplies.size)

        // Verify specific card properties
        val firstReq = testCards.first { it.card_id == "TEST_DRAWER_4_1_REQ_001" }
        assertEquals("4-1", firstReq.category_code)
        assertEquals("機車跑腿 急件順風", firstReq.category_tag)
        assertEquals("1A", firstReq.drawer_type)
        assertEquals(RequirementCard.ContactMethod.PHONE, firstReq.contact_method)
        assertEquals("0912000101", firstReq.contact_value)
        assertEquals(25.002653, firstReq.gps_lat, 0.000001)

        val firstSup = testCards.first { it.card_id == "TEST_DRAWER_4_1_SUP_001" }
        assertEquals("4-1", firstSup.category_code)
        assertEquals("1B", firstSup.drawer_type)
        assertEquals(RequirementCard.ContactMethod.PHONE, firstSup.contact_method)

        // Verify Repository query for 4-1
        BaichuanRepository.seedTestDrawerData()
        val reqCardsFromRepo = BaichuanRepository.getFilteredCards(
            userLat = 25.0026,
            userLng = 121.4976,
            categoryCodes = listOf("4-1"),
            targetType = "1A",
            currentDeviceId = "USER_TEST_DEVICE",
            limitDistanceKm = 50.0
        )
        assertEquals(20, reqCardsFromRepo.size)

        val supCardsFromRepo = BaichuanRepository.getFilteredCards(
            userLat = 25.0026,
            userLng = 121.4976,
            categoryCodes = listOf("4-1"),
            targetType = "1B",
            currentDeviceId = "USER_TEST_DEVICE",
            limitDistanceKm = 50.0
        )
        assertEquals(20, supCardsFromRepo.size)
    }

    @Test
    fun testDrawer4_2_ParsingAndLoading() {
        val testCards = DrawerBatchTester.getTestCardsForDrawer42()
        assertEquals(40, testCards.size)
        
        val requirements = testCards.filter { it.drawer_type == "1A" }
        val supplies = testCards.filter { it.drawer_type == "1B" }
        assertEquals(20, requirements.size)
        assertEquals(20, supplies.size)

        val firstReq = testCards.first { it.card_id == "TEST_DRAWER_4_2_REQ_001" }
        assertEquals("4-2", firstReq.category_code)
        assertEquals("計程叫車 長照接送", firstReq.category_tag)
        assertEquals("1A", firstReq.drawer_type)
        assertEquals(RequirementCard.ContactMethod.PHONE, firstReq.contact_method)
        assertEquals("0912333001", firstReq.contact_value)

        BaichuanRepository.seedTestDrawerData()
        val reqCardsFromRepo = BaichuanRepository.getFilteredCards(
            userLat = 25.0026,
            userLng = 121.4976,
            categoryCodes = listOf("4-2"),
            targetType = "1A",
            currentDeviceId = "USER_TEST_DEVICE",
            limitDistanceKm = 50.0
        )
        assertEquals(20, reqCardsFromRepo.size)

        val supCardsFromRepo = BaichuanRepository.getFilteredCards(
            userLat = 25.0026,
            userLng = 121.4976,
            categoryCodes = listOf("4-2"),
            targetType = "1B",
            currentDeviceId = "USER_TEST_DEVICE",
            limitDistanceKm = 50.0
        )
        assertEquals(20, supCardsFromRepo.size)
    }

    @Test
    fun testLightTestCards() {
        BaichuanRepository.clearAll()
        val lightCards = DrawerBatchTester.loadLightTestCards()
        assertEquals(5, lightCards.size)

        val reqs = lightCards.filter { it.drawer_type == "1A" }
        val sups = lightCards.filter { it.drawer_type == "1B" }
        assertEquals(2, reqs.size)
        assertEquals(3, sups.size)

        assertEquals("REQ_001", reqs.first().card_id)
        assertEquals("SUP_001", sups.first().card_id)
    }

    @Test
    fun testGpsRelativeTestCards() {
        BaichuanRepository.clearAll()
        val relCards = DrawerBatchTester.loadGpsRelativeTestCards(25.002630, 121.497703)
        assertEquals(20, relCards.size)

        val reqs = relCards.filter { it.drawer_type == "1A" }
        val sups = relCards.filter { it.drawer_type == "1B" }
        assertEquals(10, reqs.size)
        assertEquals(10, sups.size)

        assertEquals("REQ_1_1_01", reqs.first().card_id)
        assertEquals("SUP_1_1_01", sups.first().card_id)
    }

    @Test
    fun testGpsRelativeDrawer4_1TestCards() {
        BaichuanRepository.clearAll()
        val relCards = DrawerBatchTester.loadGpsRelativeDrawer4_1TestCards(25.002614, 121.497660)
        assertEquals(20, relCards.size)

        val reqs = relCards.filter { it.drawer_type == "1A" }
        val sups = relCards.filter { it.drawer_type == "1B" }
        assertEquals(10, reqs.size)
        assertEquals(10, sups.size)

        assertEquals("REQ_4_1_01", reqs.first().card_id)
        assertEquals("SUP_4_1_01", sups.first().card_id)
    }

    @Test
    fun testGpsRelativeDrawer4_2TestCards() {
        BaichuanRepository.clearAll()
        val relCards = DrawerBatchTester.loadGpsRelativeDrawer4_2TestCards(25.002614, 121.497660)
        assertEquals(20, relCards.size)

        val reqs = relCards.filter { it.drawer_type == "1A" }
        val sups = relCards.filter { it.drawer_type == "1B" }
        assertEquals(10, reqs.size)
        assertEquals(10, sups.size)

        assertEquals("REQ_4_2_01", reqs.first().card_id)
        assertEquals("SUP_4_2_01", sups.first().card_id)
    }

    @Test
    fun testCrossDrawerTestCards() {
        BaichuanRepository.clearAll()
        val crossCards = DrawerBatchTester.loadCrossDrawerTestCards()
        assertEquals(10, crossCards.size)

        val cards41 = crossCards.filter { it.category_code == "4-1" }
        val cards42 = crossCards.filter { it.category_code == "4-2" }
        assertEquals(5, cards41.size)
        assertEquals(5, cards42.size)

        assertEquals("REQ_4_1_01", cards41.first().card_id)
        assertEquals("REQ_4_2_01", cards42.first().card_id)
    }

    @Test
    fun testRefreshCardsFromGitLocalParse() {
        val mockGitJson = """
            [
              {
                "cardId": "GIT_001",
                "drawerCode": "1-1",
                "categoryName": "在地攤商 個人私廚",
                "type": "REQUIREMENT",
                "title": "【Git需求】測試標題",
                "description": "測試內文",
                "contactType": "Phone",
                "contactId": "0912345678",
                "lat": 25.00263,
                "lng": 121.49770,
                "createdAt": "2026-09-22T23:15:00Z"
              }
            ]
        """.trimIndent()

        val parsedCards = DrawerBatchTester.parseDrawerJson(mockGitJson)
        assertEquals(1, parsedCards.size)
        
        val card = parsedCards.first()
        assertEquals("GIT_001", card.card_id)
        assertEquals("1-1", card.category_code)
        assertEquals("1A", card.drawer_type)
        assertEquals(RequirementCard.ContactMethod.PHONE, card.contact_method)
        assertEquals("0912345678", card.contact_value)

        BaichuanRepository.injectTestCards(parsedCards)
        val filtered = BaichuanRepository.getFilteredCards(
            25.0026, 121.4977, listOf("1-1"), "1A", "TEST_DEV", 50.0
        )
        assertEquals(1, filtered.size)
        assertEquals("GIT_001", filtered.first().card_id)
    }

    @Test
    fun testDrawer1_1CamelTestCards() {
        BaichuanRepository.clearAll()
        val camelCards = DrawerBatchTester.loadDrawer1_1CamelTestCards()
        assertEquals(20, camelCards.size)

        val reqs = camelCards.filter { it.drawer_type == "1A" }
        val sups = camelCards.filter { it.drawer_type == "1B" }
        assertEquals(10, reqs.size)
        assertEquals(10, sups.size)

        assertEquals("REQ_1_1_01", reqs.first().card_id)
        assertEquals("SUP_1_1_01", sups.first().card_id)
    }

    @Test
    fun testDrawer4_1ErrandCamelTestCards() {
        BaichuanRepository.clearAll()
        val errandCards = DrawerBatchTester.loadDrawer4_1ErrandCamelTestCards()
        assertEquals(20, errandCards.size)

        val reqs = errandCards.filter { it.drawer_type == "1A" }
        val sups = errandCards.filter { it.drawer_type == "1B" }
        assertEquals(10, reqs.size)
        assertEquals(10, sups.size)

        assertEquals("REQ_4_1_01", reqs.first().card_id)
        assertEquals("SUP_4_1_01", sups.first().card_id)
    }

    @Test
    fun testDrawer4_2TaxiCamelTestCards() {
        BaichuanRepository.clearAll()
        val taxiCards = DrawerBatchTester.loadDrawer4_2TaxiCamelTestCards()
        assertEquals(20, taxiCards.size)

        val reqs = taxiCards.filter { it.drawer_type == "1A" }
        val sups = taxiCards.filter { it.drawer_type == "1B" }
        assertEquals(10, reqs.size)
        assertEquals(10, sups.size)

        assertEquals("REQ_4_2_01", reqs.first().card_id)
        assertEquals("SUP_4_2_01", sups.first().card_id)
    }

    @Test
    fun testPhoneActionHelper_ExtractionAndJsonParsing() {
        // 1. 測試字串提昇與修剪空白
        assertEquals("0979739077", PhoneActionHelper.extractPhoneNumber(" 0979739077 "))
        assertNull(PhoneActionHelper.extractPhoneNumber("   "))
        assertNull(PhoneActionHelper.extractPhoneNumber(null))

        // 2. 測試 Map 結構提昇 (phone.number / phone.val)
        val mapData = mapOf("number" to " 0979739077 ", "action" to "CALL")
        assertEquals("0979739077", PhoneActionHelper.extractPhoneNumber(mapData))

        // 3. 測試 JsonObject 提昇
        val gsonObj = com.google.gson.JsonObject().apply {
            addProperty("number", "0979739077")
            addProperty("action", "CALL")
        }
        assertEquals("0979739077", PhoneActionHelper.extractPhoneNumber(gsonObj))

        // 4. 測試包含 contact.phone 結構之 JSON 卡片解析
        val jsonWithContactPhone = """
            [
              {
                "cardId": "PHONE_CARD_01",
                "drawerCode": "1-1",
                "title": "電話測試卡片",
                "description": "測試電話撥號功能",
                "contact": {
                  "method": "LINE",
                  "val": "line_user_id",
                  "phone": "0979739077"
                }
              },
              {
                "cardId": "PHONE_CARD_02",
                "drawerCode": "1-1",
                "title": "電話物件測試卡片",
                "description": "測試電話物件結構",
                "contact": {
                  "method": "LINE",
                  "val": "line_user_id_2",
                  "phone": {
                    "number": "0912345678",
                    "action": "CALL"
                  }
                }
              }
            ]
        """.trimIndent()

        val cards = DrawerBatchTester.parseDrawerJson(jsonWithContactPhone)
        assertEquals(2, cards.size)
        assertEquals("PHONE_CARD_01", cards[0].card_id)
        assertEquals("0979739077", cards[0].phone_number)
        assertEquals("PHONE_CARD_02", cards[1].card_id)
        assertEquals("0912345678", cards[1].phone_number)
    }

    @Test
    fun testPhoneActionHelper_isTelUrlAndUrlExtraction() {
        // 1. 標準 tel: 協定
        assertTrue(PhoneActionHelper.isTelUrl("tel:0979739077"))
        assertTrue(PhoneActionHelper.isTelUrl("TEL:0979739077"))
        assertTrue(PhoneActionHelper.isTelUrl("tel://0979739077"))
        assertEquals("0979739077", PhoneActionHelper.extractPhoneNumberFromUrl("tel:0979739077"))

        // 2. URL Encoded tel%3A 協定
        assertTrue(PhoneActionHelper.isTelUrl("https://www.google.com/search?q=tel%3A0979739077"))
        assertEquals("0979739077", PhoneActionHelper.extractPhoneNumberFromUrl("https://www.google.com/search?q=tel%3A0979739077"))

        // 3. Google 搜尋帶 tel:
        assertTrue(PhoneActionHelper.isTelUrl("https://www.google.com/search?q=tel:0979739077"))
        assertEquals("0979739077", PhoneActionHelper.extractPhoneNumberFromUrl("https://www.google.com/search?q=tel:0979739077"))

        // 4. Google 搜尋純電話號碼
        assertTrue(PhoneActionHelper.isTelUrl("https://www.google.com.tw/search?q=0979739077"))
        assertEquals("0979739077", PhoneActionHelper.extractPhoneNumberFromUrl("https://www.google.com.tw/search?q=0979739077"))

        // 5. Android Intent Scheme
        assertTrue(PhoneActionHelper.isTelUrl("intent://0979739077#Intent;scheme=tel;action=android.intent.action.DIAL;end"))
        assertEquals("0979739077", PhoneActionHelper.extractPhoneNumberFromUrl("intent://0979739077#Intent;scheme=tel;action=android.intent.action.DIAL;end"))

        // 6. 一般網頁 link，應回傳 false
        assertFalse(PhoneActionHelper.isTelUrl("https://www.google.com/"))
        assertFalse(PhoneActionHelper.isTelUrl("https://line.me/ti/p/~line_id"))
    }

    @Test
    fun testAddOrUpdateCard_CallbackAndPartitioning() {
        BaichuanRepository.clearAll()
        var successCalled = false
        val testCard = RequirementCard(
            card_id = "TEST_CALLBACK_01",
            publisher_device_id = "DEV_001",
            category_code = "1-1",
            drawer_type = "1A",
            description = "測試閉環驗證",
            contact_method = RequirementCard.ContactMethod.LINE,
            contact_value = "line_id_test",
            gps_lat = 25.00263,
            gps_lng = 121.49770,
            is_test_only = true
        )

        BaichuanRepository.addOrUpdateCard(
            testCard,
            onSuccess = { successCalled = true },
            onError = { fail("Should not trigger error for test card") }
        )

        assertTrue(successCalled)
        val filtered = BaichuanRepository.getFilteredCards(
            25.00263, 121.49770, listOf("1-1"), "1A", "DEV_OTHER", 50.0
        )
        assertEquals(1, filtered.size)
        assertEquals("TEST_CALLBACK_01", filtered.first().card_id)
    }

    @Test
    fun testGrid88FormatterAndABCrossLogic() {
        val allGrids = Grid88Formatter.getAllGridCodes()
        assertEquals(88, allGrids.size)
        assertEquals("1-1", allGrids.first())
        assertEquals("11-8", allGrids.last())

        assertTrue(Grid88Formatter.validateGridCode("1-1"))
        assertTrue(Grid88Formatter.validateGridCode("11-8"))
        assertFalse(Grid88Formatter.validateGridCode("12-1"))
        assertFalse(Grid88Formatter.validateGridCode("1-9"))

        val verticalOrder = Grid88Formatter.getTwoColumnVerticalOrder(1)
        assertEquals(8, verticalOrder.size)
        assertEquals("1-1", verticalOrder[0])
        assertEquals("1-8", verticalOrder[7])

        val mappedCell = Grid88Formatter.mapCoordinatesToGridCell(25.0026, 121.4977)
        assertTrue(Grid88Formatter.validateGridCode(mappedCell))

        // A/B cross logic test
        val demandOpponent = Grid88Formatter.getOpponentDrawerType("1A")
        assertEquals("1B", demandOpponent)
        val supplyOpponent = Grid88Formatter.getOpponentDrawerType("1B")
        assertEquals("1A", supplyOpponent)
    }
}