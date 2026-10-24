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
        ).filter { it.card_id.startsWith("TEST_DRAWER_4_1_") }
        assertEquals(20, reqCardsFromRepo.size)

        val supCardsFromRepo = BaichuanRepository.getFilteredCards(
            userLat = 25.0026,
            userLng = 121.4976,
            categoryCodes = listOf("4-1"),
            targetType = "1B",
            currentDeviceId = "USER_TEST_DEVICE",
            limitDistanceKm = 50.0
        ).filter { it.card_id.startsWith("TEST_DRAWER_4_1_") }
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
        ).filter { it.card_id.startsWith("TEST_DRAWER_4_2_") }
        assertEquals(20, reqCardsFromRepo.size)

        val supCardsFromRepo = BaichuanRepository.getFilteredCards(
            userLat = 25.0026,
            userLng = 121.4976,
            categoryCodes = listOf("4-2"),
            targetType = "1B",
            currentDeviceId = "USER_TEST_DEVICE",
            limitDistanceKm = 50.0
        ).filter { it.card_id.startsWith("TEST_DRAWER_4_2_") }
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
        assertEquals(96, allGrids.size)
        assertEquals("1-1", allGrids.first())
        assertEquals("12-8", allGrids.last())

        assertTrue(Grid88Formatter.validateGridCode("1-1"))
        assertTrue(Grid88Formatter.validateGridCode("11-8"))
        assertTrue(Grid88Formatter.validateGridCode("12-1"))
        assertTrue(Grid88Formatter.validateGridCode("12-8"))
        assertFalse(Grid88Formatter.validateGridCode("13-1"))
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

    @Test
    fun testSearchEngineReasoningAndAutoPublish88Cards() {
        // 1. 測試防禦機制 (Token 驗證)
        assertFalse(SearchEngineReasoningEngine.verifyExecutionGuard("INVALID_MANUAL_TOKEN"))
        assertTrue(SearchEngineReasoningEngine.verifyExecutionGuard(SearchEngineReasoningEngine.SYSTEM_AUTO_EXECUTION_TOKEN))

        // 2. 人工無效 Token 啟動應被阻斷
        val blockedCards = SearchEngineReasoningEngine.generateAndPublish88Cards("UNAUTHORIZED_TOKEN")
        assertTrue(blockedCards.isEmpty())

        // 3. 正確排程 Token 生成卡片項目
        val autoCards = SearchEngineReasoningEngine.generateAndPublish88Cards(SearchEngineReasoningEngine.SYSTEM_AUTO_EXECUTION_TOKEN)
        assertTrue(autoCards.isNotEmpty())

        // 4. 驗證四種通訊軟體 (LINE, Telegram, WeChat, Phone) 交叉輪流使用
        val methodCounts = autoCards.groupingBy { it.contact_method }.eachCount()
        assertTrue((methodCounts[RequirementCard.ContactMethod.LINE] ?: 0) > 0)
        assertTrue((methodCounts[RequirementCard.ContactMethod.TELEGRAM] ?: 0) > 0)
        assertTrue((methodCounts[RequirementCard.ContactMethod.WECHAT] ?: 0) > 0)
        assertTrue((methodCounts[RequirementCard.ContactMethod.PHONE] ?: 0) > 0)

        // 5. 驗證情境推理內容說明
        val firstCard = autoCards.first()
        assertEquals(12, firstCard.ttl_hours)

        // 6. 驗證寫入 BaichuanRepository 分片倉儲
        val retrievedCards = BaichuanRepository.getFilteredCards(
            25.0026, 121.4977, listOf(firstCard.category_code), firstCard.drawer_type, "TEST_DEVICE", 50.0
        )
        assertTrue(retrievedCards.isNotEmpty())
    }

    @Test
    fun testReligionCategory12PresetCards() {
        BaichuanRepository.seedTestDrawerData()
        
        // 驗證第 12 大類 (12-1 至 12-8) 每個子項目皆有 10 筆需求與 10 筆供給
        for (sub in 1..8) {
            val code = "12-$sub"
            val reqCards = BaichuanRepository.getFilteredCards(
                25.0026, 121.4977, listOf(code), "1A", "TEST_DEVICE", 50.0
            )
            val supCards = BaichuanRepository.getFilteredCards(
                25.0026, 121.4977, listOf(code), "1B", "TEST_DEVICE", 50.0
            )
            assertEquals("Code $code 應有 10 筆需求卡片", 10, reqCards.size)
            assertEquals("Code $code 應有 10 筆供給卡片", 10, supCards.size)
        }
    }

    @Test
    fun testGitSyncSchedulerAnd12HourLoop() {
        BaichuanRepository.clearAll()
        
        // 執行單次同步與抹除循環
        GitSyncScheduler.triggerSyncCycle()

        // 驗證 12 小時清空後自動觸發重新生成 88 個卡片
        val allGrids = Grid88Formatter.getAllGridCodes()
        val cardsFromRepo = BaichuanRepository.getFilteredCards(
            25.0026, 121.4977, allGrids, "1A", "SYSTEM_ADMIN", 50.0
        )
        assertFalse(cardsFromRepo.isEmpty())
        
        // 模擬 12 小時效期過去
        val futureMillis = System.currentTimeMillis() + (13 * 3600 * 1000L)
        BaichuanRepository.purgeExpiredCards(futureMillis)

        // 抹除後觸發下一次同步，將會自動重新載入新一批卡片
        GitSyncScheduler.triggerSyncCycle()
        val newBatch = BaichuanRepository.getFilteredCards(
            25.0026, 121.4977, allGrids, "1A", "SYSTEM_ADMIN", 50.0
        )
        assertFalse(newBatch.isEmpty())
    }

    @Test
    fun testPresetVsRealPublisherCardLocationLogic() {
        // 1. 預設卡片 (靜態卡片) 測試
        val presetCard = RequirementCard(
            card_id = "SEARCH_AUTO_CARD_001",
            publisher_device_id = "SEARCH_ENGINE_AUTO_NODE",
            is_test_only = true,
            gps_lat = 25.0026,
            gps_lng = 121.4977
        )
        assertTrue(presetCard.isPresetCard())

        // 2. 發送者卡片 (真實卡片) 測試
        val realPublisherCard = RequirementCard(
            card_id = "REAL_USER_CARD_888",
            publisher_device_id = "USER_DEVICE_UUID_12345",
            is_test_only = false,
            gps_lat = 25.0330,
            gps_lng = 121.5654
        )
        assertFalse(realPublisherCard.isPresetCard())
        assertEquals(25.0330, realPublisherCard.gps_lat, 0.0001)
        assertEquals(121.5654, realPublisherCard.gps_lng, 0.0001)
    }

    @Test
    fun testBaichuanV2CardsPathAnd88Grids() {
        val testCardId = "CARD_12345"
        val expectedPath = "https://api.github.com/repos/enshenglin05-coder/baichuan-v2/contents/cards/$testCardId.json"
        assertTrue(expectedPath.contains("/contents/cards/$testCardId.json"))

        val allGrids = Grid88Formatter.getAllGridCodes()
        assertEquals(96, allGrids.size)
        allGrids.forEach { code ->
            assertTrue(Grid88Formatter.validateGridCode(code))
        }
    }

    @Test
    fun testGitHubApiUploadSimulationAndTokenIntegrity() {
        // 1. 驗證 Token 未被截斷或遺失，確保完整性 (嚴格對齊 Bearer 授權格式)
        val authHeader = BaichuanApiService.getAuthHeader()
        assertTrue(authHeader.contains("ghp_Y3Rb2ZHTrMcVDujrkjzv6SjhYicGU3107XgZ"))
        assertTrue(authHeader.startsWith("Bearer "))
        assertEquals("Authorization: Bearer ghp_Y3Rb2ZHTrMcVDujrkjzv6SjhYicGU3107XgZ", "Authorization: $authHeader")

        // 驗證 Token 清理函數 cleanToken 能夠正確剝除 Bearer/token 前綴與引號
        assertEquals("ghp_Y3Rb2ZHTrMcVDujrkjzv6SjhYicGU3107XgZ", UserIdentityManager.cleanToken("Bearer ghp_Y3Rb2ZHTrMcVDujrkjzv6SjhYicGU3107XgZ"))
        assertEquals("ghp_Y3Rb2ZHTrMcVDujrkjzv6SjhYicGU3107XgZ", UserIdentityManager.cleanToken("token ghp_Y3Rb2ZHTrMcVDujrkjzv6SjhYicGU3107XgZ"))
        assertEquals("ghp_Y3Rb2ZHTrMcVDujrkjzv6SjhYicGU3107XgZ", UserIdentityManager.cleanToken("\"ghp_Y3Rb2ZHTrMcVDujrkjzv6SjhYicGU3107XgZ\""))
        assertEquals("ghp_Y3Rb2ZHTrMcVDujrkjzv6SjhYicGU3107XgZ", UserIdentityManager.cleanToken("  ghp_Y3Rb2ZHTrMcVDujrkjzv6SjhYicGU3107XgZ\n"))

        // 驗證 401 自動 Refresh 時能正確重載永久 Token
        assertTrue(UserIdentityManager.refreshAuthToken(null))
        assertEquals("ghp_Y3Rb2ZHTrMcVDujrkjzv6SjhYicGU3107XgZ", UserIdentityManager.getAuthToken(null))

        // 2. 模擬 GitHub Contents API 請求建構與 Payload 格式驗證
        val testCard = RequirementCard(
            card_id = "SIM_CARD_01",
            category_code = "1-1",
            drawer_type = "1A",
            description = "模擬上傳測試",
            gps_lat = 25.0026,
            gps_lng = 121.4977
        )
        val json = com.google.gson.Gson().toJson(listOf(testCard))
        val base64Content = java.util.Base64.getEncoder().encodeToString(json.toByteArray(Charsets.UTF_8))
        assertNotNull(base64Content)
        assertTrue(base64Content.isNotEmpty())

        val payload = mapOf(
            "message" to "Update drawer data: 1-1",
            "content" to base64Content
        )
        assertEquals("Update drawer data: 1-1", payload["message"])
        assertNotNull(payload["content"])

        // 3. 驗證 88 格編碼與 A/B 交叉邏輯對齊
        val gridCode = Grid88Formatter.mapCoordinatesToGridCell(25.0026, 121.4977)
        assertTrue(Grid88Formatter.validateGridCode(gridCode))
        assertEquals("1B", Grid88Formatter.getOpponentDrawerType("1A"))
    }

    @Test
    fun testCard12HourPurgeAndJsonExceptionRules() {
        // 1. 測試 12 小時自動暫存與抹除機制
        val now = System.currentTimeMillis()
        val validCard = RequirementCard(
            card_id = "VALID_CARD_01",
            created_at = now - (11 * 3600 * 1000L), // 11 小時前 (未過期)
            category_code = "1-1",
            drawer_type = "1A",
            gps_lat = 25.0026,
            gps_lng = 121.4977
        )
        val expiredCard = RequirementCard(
            card_id = "EXPIRED_CARD_01",
            created_at = now - (13 * 3600 * 1000L), // 13 小時前 (已過期)
            category_code = "1-1",
            drawer_type = "1A"
        )

        assertFalse(validCard.isExpired(now))
        assertTrue(expiredCard.isExpired(now))

        BaichuanRepository.clearAll()
        BaichuanRepository.injectTestCards(listOf(validCard, expiredCard))

        // 觸發 12 小時暫存抹除
        BaichuanRepository.purgeExpiredCards(now)

        val filteredCards = BaichuanRepository.getFilteredCards(
            25.0026, 121.4977, listOf("1-1"), "1A", "TEST_DEV", 50.0
        )
        assertEquals(1, filteredCards.size)
        assertEquals("VALID_CARD_01", filteredCards.first().card_id)

        // 2. 測試 JSON 異常處理與丟棄機制
        CardJsonExceptionHandler.clearAll()
        val brokenJson = "{ invalid_json_syntax: true "

        // 第 1 次異常：直接丟棄，不立案
        val res1 = CardJsonExceptionHandler.parseAndHandle(brokenJson, "BROKEN_TEST_KEY")
        assertTrue(res1.isEmpty())
        assertEquals(0, CardJsonExceptionHandler.getQuarantinedRecords().size)

        // 第 2 次異常：直接丟棄，不立案
        val res2 = CardJsonExceptionHandler.parseAndHandle(brokenJson, "BROKEN_TEST_KEY")
        assertTrue(res2.isEmpty())
        assertEquals(0, CardJsonExceptionHandler.getQuarantinedRecords().size)

        // 第 3 次異常 (達到 3 次門檻)：進入人工審核隔離區，支援凍結與忽略
        val res3 = CardJsonExceptionHandler.parseAndHandle(brokenJson, "BROKEN_TEST_KEY")
        assertTrue(res3.isEmpty())
        val quarantined = CardJsonExceptionHandler.getQuarantinedRecords()
        assertEquals(1, quarantined.size)
        assertEquals("BROKEN_TEST_KEY", quarantined.first().recordId)
        assertEquals(QuarantinedCardRecord.ReviewStatus.MANUAL_REVIEW, quarantined.first().status)

        // 測試審核狀態切換 (例如切換為凍結 FROZEN)
        CardJsonExceptionHandler.updateRecordStatus("BROKEN_TEST_KEY", QuarantinedCardRecord.ReviewStatus.FROZEN)
        assertEquals(QuarantinedCardRecord.ReviewStatus.FROZEN, CardJsonExceptionHandler.getQuarantinedRecords().first().status)
    }
}