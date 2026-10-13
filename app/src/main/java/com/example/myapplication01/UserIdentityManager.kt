package com.example.myapplication01

import android.content.Context
import android.provider.Settings
import java.util.UUID

object UserIdentityManager {

    private const val PREF_NAME = "user_identity_prefs"
    private const val KEY_USER_ID = "my_device_id"

    /**
     * App 啟動時取得或生成唯一的「登錄人 ID」
     */
    fun getMyDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var userId = prefs.getString(KEY_USER_ID, null)

        if (userId.isNullOrEmpty()) {
            // 優先使用 ANDROID_ID，若取得失敗則生成 UUID
            userId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                ?: UUID.randomUUID().toString()
            prefs.edit().putString(KEY_USER_ID, userId).apply()
        }
        return userId
    }

    /**
     * 判斷卡片是否為「自己發布」
     */
    fun isMyCard(context: Context, publisherDeviceId: String): Boolean {
        return getMyDeviceId(context) == publisherDeviceId
    }
}
