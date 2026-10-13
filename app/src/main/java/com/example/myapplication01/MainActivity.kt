package com.example.myapplication01

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 檢查 Onboarding 狀態
        val sharedPrefs = getSharedPreferences("APP_ONBOARDING", Context.MODE_PRIVATE)
        val hasAgreed = sharedPrefs.getBoolean("IS_FIRST_LAUNCH_AGREED", false)

        if (!hasAgreed) {
            // 前往服務條款頁面
            val intent = Intent(this, TermsPrivacyActivity::class.java)
            startActivity(intent)
        } else {
            // 跳轉到 UI1 宗教卡片選擇介面
            val intent = Intent(this, UI1Activity::class.java)
            startActivity(intent)
        }
        finish()
    }
}
