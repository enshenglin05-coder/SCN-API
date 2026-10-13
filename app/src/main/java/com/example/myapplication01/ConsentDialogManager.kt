package com.example.myapplication01

import android.content.Context
import androidx.appcompat.app.AlertDialog

object ConsentDialogManager {

    /**
     * Google Play 要求的「位置權限明確告知彈窗」
     */
    fun showLocationDisclosureDialog(context: Context, onAgree: () -> Unit, onDecline: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("定位服務與隱私使用說明")
            .setMessage(
                "「附近剛需」需要存取您的裝置位置資訊（GPS/網路），" +
                "以便為您即時計算並顯示周邊服務與需求卡片的精準距離。\n\n" +
                "我們絕不追蹤您的個人軌跡，亦不將位置數據販售予第三方。"
            )
            .setPositiveButton("同意並繼續") { _, _ -> onAgree() }
            .setNegativeButton("暫時不允許") { _, _ -> onDecline() }
            .setCancelable(false)
            .show()
    }

    /**
     * 首次啟動 App 服務條款與 UGC 免責聲明
     */
    fun showTermsAndPrivacyDialog(context: Context, onAccepted: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("服務條款與 UGC 內容規範")
            .setMessage(
                "歡迎使用！登入前請閱讀並同意以下事項：\n\n" +
                "1. 本平台為使用者自發布資訊看板，禁止發布詐騙、色情、暴力或違法內容。\n" +
                "2. 發布者隨時擁有自身卡片的「一鍵下架/全網刪除」權利。\n" +
                "3. 遇到不當內容，使用者可透過「3 ID 檢舉」機制進行社群防禦與隱藏。\n" +
                "4. 系統採取零伺服器極簡架構，卡片紀錄將於 12 小時後自然銷毀。"
            )
            .setPositiveButton("我已閱讀並同意") { _, _ -> onAccepted() }
            .setCancelable(false)
            .show()
    }
}
