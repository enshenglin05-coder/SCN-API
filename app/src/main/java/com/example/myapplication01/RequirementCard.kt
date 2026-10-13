package com.example.myapplication01

import com.google.gson.annotations.SerializedName

/**
 * 百川 1.1 統一外殼模板 (Shell Envelope)
 * 核心編號矩陣 (11x8) 與模組化邏輯
 */
data class RequirementCard(
    @SerializedName("card_id", alternate = ["cardId"]) val card_id: String = "",
    @SerializedName("publisher_device_id", alternate = ["publisherDeviceId"]) val publisher_device_id: String = "",
    @SerializedName("created_at", alternate = ["createdAt"]) val created_at: Long = System.currentTimeMillis(),
    
    // 按讚機制 (1.0 擴展)
    @SerializedName("like_count") val like_count: Int = 0,
    @SerializedName("dislike_count") val dislike_count: Int = 0,
    
    // 11x8 矩陣編碼 (如 "1-1" 到 "11-8")
    @SerializedName("category_code", alternate = ["drawer_code", "drawerCode"]) val category_code: String = "1-1",
    @SerializedName("drawer_type", alternate = ["type"]) val drawer_type: String = "1A", // 1A: 需求, 1B: 供給
    
    @SerializedName("description") val description: String = "",
    @SerializedName("media_url") val media_url: String? = null,
    
    // 4 選 1 聯絡工具模組化支援
    @SerializedName("contact_method", alternate = ["contact_type", "contactType"]) val contact_method: ContactMethod = ContactMethod.LINE,
    @SerializedName("contact_value", alternate = ["contact_id", "contactId"]) val contact_value: String = "",
    @SerializedName("phone_number") val phone_number: String? = null,
    
    // 經緯度定位與距離計算核心
    @SerializedName("gps_lat", alternate = ["lat"]) val gps_lat: Double = 0.0,
    @SerializedName("gps_lng", alternate = ["lng"]) val gps_lng: Double = 0.0,
    
    @SerializedName("theme_color") val theme_color: String = "#FFFFFF",
    
    // 風控與 12 小時過期邏輯
    @SerializedName("moderation_status") val moderation_status: String = "NORMAL",
    @SerializedName("reporter_ids") val reporter_ids: Set<String> = emptySet(),
    @SerializedName("reports_by_reason") val reports_by_reason: Map<String, Set<String>> = emptyMap(),
    @SerializedName("accepted_by_device_id") val accepted_by_device_id: String? = null,
    
    // UI 渲染標籤 (由 app 端動態生成或從 repository 取得)
    @SerializedName("category_tag", alternate = ["category_name", "categoryName"]) val category_tag: String = "",
    
    // TTL 與測試模式支援
    @SerializedName("ttl_hours") val ttl_hours: Int = 12,
    @SerializedName("is_test_only") val is_test_only: Boolean = false,
    
    @Transient val isSelfOwner: Boolean = false
) {
    enum class ContactMethod {
        LINE, TELEGRAM, WECHAT, PHONE
    }

    /**
     * 動態 TTL 效期檢查 (預設 12 小時)
     */
    fun isExpired(): Boolean {
        val ttlMillis = ttl_hours * 60 * 60 * 1000L
        return System.currentTimeMillis() - created_at > ttlMillis
    }

    /**
     * 是否為預留擴充池 (Category 11)
     */
    fun isReserved(): Boolean = category_code.startsWith("11-")
}
