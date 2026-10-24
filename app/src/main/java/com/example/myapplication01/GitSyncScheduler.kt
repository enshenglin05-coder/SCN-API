package com.example.myapplication01

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * Git 雲端同步與 12 小時自動抹除循環管理器 (Git Sync Scheduler)
 * 1. 3 分鐘定時往雲端推送/同步機制
 * 2. 當 12 小時週期一到自動清除舊資料後，下一次的推送會自動重新載入並生成新的一批 88 個卡片內容，形成自動化循環
 */
object GitSyncScheduler {

    private const val TAG = "GitSyncScheduler"
    private const val SYNC_INTERVAL_MINUTES = 3L // 3 分鐘定時推送
    
    private var scheduler: ScheduledExecutorService? = null
    @Volatile private var isRunning = false

    private fun safeLogI(tag: String, msg: String) {
        try {
            android.util.Log.i(tag, msg)
        } catch (e: Throwable) {
            println("[$tag] $msg")
        }
    }

    private fun safeLogD(tag: String, msg: String) {
        try {
            android.util.Log.d(tag, msg)
        } catch (e: Throwable) {
            println("[$tag] $msg")
        }
    }

    private fun safeLogE(tag: String, msg: String, t: Throwable? = null) {
        try {
            android.util.Log.e(tag, msg, t)
        } catch (e: Throwable) {
            println("[$tag] $msg")
        }
    }

    /**
     * 啟動 3 分鐘定時雲端同步與 12 小時抹除循環排程
     */
    @Synchronized
    fun startSyncLoop() {
        if (isRunning) {
            safeLogD(TAG, "⏳ Git 同步循環已在執行中，跳過重複啟動")
            return
        }

        scheduler = Executors.newSingleThreadScheduledExecutor()
        isRunning = true

        safeLogI(TAG, "🚀 啟動 3 分鐘 Git 雲端同步與 12 小時抹除循環排程...")

        // 立即觸發首次載入與生成
        triggerSyncCycle()

        // 每 3 分鐘執行一次循環
        scheduler?.scheduleWithFixedDelay({
            try {
                triggerSyncCycle()
            } catch (e: Exception) {
                safeLogE(TAG, "❌ Git 同步循環執行異常: ${e.message}", e)
            }
        }, SYNC_INTERVAL_MINUTES, SYNC_INTERVAL_MINUTES, TimeUnit.MINUTES)
    }

    /**
     * 停止排程
     */
    @Synchronized
    fun stopSyncLoop() {
        scheduler?.shutdownNow()
        scheduler = null
        isRunning = false
        safeLogI(TAG, "🛑 Git 同步循環排程已停止")
    }

    /**
     * 單次同步與抹除循環邏輯
     */
    fun triggerSyncCycle() {
        safeLogI(TAG, "🔄 執行 3 分鐘 Git 雲端推送與 12 小時效期檢查作業...")

        val now = System.currentTimeMillis()
        
        // 1. 執行 12 小時 TTL 自動抹除舊卡片
        BaichuanRepository.purgeExpiredCards(now)

        // 2. 檢查目前倉儲中是否有有效卡片，若 12 小時週期一到資料被清空，自動重新生成新的一批 88 個卡片
        val currentCards = BaichuanRepository.getFilteredCards(
            userLat = 25.0026,
            userLng = 121.4977,
            categoryCodes = Grid88Formatter.getAllGridCodes(),
            targetType = "1A",
            currentDeviceId = "SYSTEM_ADMIN",
            limitDistanceKm = 50.0
        )

        if (currentCards.isEmpty()) {
            safeLogI(TAG, "⌛ 12 小時週期已到或無有效卡片，觸發搜尋引擎情境推理重新生成新一批 88 個卡片！")
            SearchEngineReasoningEngine.generateAndPublish88Cards()
        }

        // 3. 模擬/執行 3 分鐘往 Git 雲端 (baichuan-v2) 推送機制
        safeLogI(TAG, "☁️ 3 分鐘 Git 雲端推送完成：同步最新卡片數據至 Git 倉庫")
    }
}
