package com.example.myapplication01

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationFlowManager {

    var currentLocation: Pair<Double, Double>? = null

    /**
     * 圖三發布頁面：若尚未定位，自動執行首次冷啟動讀取
     */
    fun autoFetchLocationOnFirstPublish(context: Context, onLocationRetrieved: (Pair<Double, Double>) -> Unit) {
        val lat = currentLocation?.first
        val lng = currentLocation?.second
        
        if (lat == null || lng == null || lat == 0.0 || lng == 0.0) {
            // 讀取裝置路由經緯度 (GPS / Network)
            fetchDeviceRouteLocation(context) { location ->
                currentLocation = location
                onLocationRetrieved(location)
            }
        } else {
            onLocationRetrieved(currentLocation!!)
        }
    }

    /**
     * 刷新列表按鈕點擊事件：防呆檢查
     */
    fun onRefreshListClicked(context: Context, onRefreshSuccess: () -> Unit) {
        val lat = currentLocation?.first
        val lng = currentLocation?.second
        
        if (lat == null || lng == null || lat == 0.0 || lng == 0.0) {
            // 無經緯度座標，精準提示使用者
            Toast.makeText(context, "請點擊上方『我的位置』刷新列表", Toast.LENGTH_SHORT).show()
        } else {
            // 已有座標，順利執行列表刷新
            onRefreshSuccess()
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchDeviceRouteLocation(context: Context, callback: (Pair<Double, Double>) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val pos = Pair(location.latitude, location.longitude)
                    callback(pos)
                }
            }
    }
}
