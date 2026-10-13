package com.example.myapplication01

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object MapUtils {
    /**
     * 開啟 Google 地圖並在指定座標放置大頭針
     */
    fun openGoogleMaps(context: Context, lat: Double, lng: Double, label: String) {
        try {
            val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "未安裝 Google 地圖，改用瀏覽器開啟", Toast.LENGTH_SHORT).show()
            val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
            context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
        }
    }
}
