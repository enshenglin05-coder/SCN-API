package com.example.myapplication01

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object MapUtils {

    /**
     * 統一 Google Map 導航核心入口 (區分發送者真實卡片與預設卡片)
     * - 預設卡片 (靜態卡片)：強制顯示/回傳「無法讀取該卡片位置資訊」
     * - 發送者卡片 (真實卡片)：精準帶入發送者設定的動態經緯度，並正常開啟 Google Map 導航
     */
    fun openGoogleMapsForCard(context: Context, card: RequirementCard) {
        if (card.isPresetCard()) {
            Toast.makeText(context, "無法讀取該卡片位置資訊", Toast.LENGTH_SHORT).show()
        } else {
            openRealGoogleMaps(context, card.gps_lat, card.gps_lng, card.category_tag)
        }
    }

    /**
     * 精準帶入發送者動態經緯度，並調起 Google 地圖導航 (強制使用 https:// 協議)
     */
    fun openRealGoogleMaps(context: Context, lat: Double, lng: Double, label: String) {
        try {
            val geoUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
            val mapIntent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "無法開啟地圖導航", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 保底相容舊介面 (當沒有 RequirementCard 實例時)
     */
    fun openGoogleMaps(context: Context, lat: Double, lng: Double, label: String) {
        Toast.makeText(context, "無法讀取該卡片位置資訊", Toast.LENGTH_SHORT).show()
    }
}
