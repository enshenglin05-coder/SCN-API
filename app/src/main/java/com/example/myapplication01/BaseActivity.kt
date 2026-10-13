package com.example.myapplication01

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    override fun startActivity(intent: Intent?) {
        if (intent != null && PhoneActionHelper.safeStartActivity(this, intent)) {
            return
        }
        super.startActivity(intent)
    }

    override fun startActivity(intent: Intent?, options: Bundle?) {
        if (intent != null && PhoneActionHelper.safeStartActivity(this, intent)) {
            return
        }
        super.startActivity(intent, options)
    }
}
