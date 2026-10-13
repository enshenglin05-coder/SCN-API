package com.example.myapplication01.repository.core

/**
 * 百川 1.0 全局不可變地理過濾器
 * 封裝標準化 Bounding Box 公式，確保 72 格全抽屜邏輯統一
 */
object GeoBoundingBoxFilter {

    data class BoundingBox(
        val minLat: Double,
        val maxLat: Double,
        val minLon: Double,
        val maxLon: Double
    )

    /**
     * 高階封裝函式：根據中心座標與距離計算 4 個座標界線
     */
    fun calculateBoundingBox(lat: Double, lon: Double, y2DistanceKm: Double): BoundingBox {
        // 標準經緯度偏移公式
        val deltaLat = y2DistanceKm * 0.009
        val deltaLon = y2DistanceKm * 0.010
        
        return BoundingBox(
            minLat = lat - deltaLat,
            maxLat = lat + deltaLat,
            minLon = lon - deltaLon,
            maxLon = lon + deltaLon
        )
    }
}
