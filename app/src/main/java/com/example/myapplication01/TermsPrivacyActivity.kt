package com.example.myapplication01

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class TermsPrivacyActivity : BaseActivity() {

    private lateinit var cbAgree: CheckBox
    private lateinit var tvTermsLinks: TextView
    private lateinit var tvWelcomeTitle: TextView
    private lateinit var btnContinue: MaterialButton
    private lateinit var btnExitApp: MaterialButton
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            navigateToUI1()
        } else {
            Toast.makeText(this, "需要定位權限以提供外送服務資訊", Toast.LENGTH_LONG).show()
            // 即使拒絕權限，通常流程也會進入主頁，但功能會受限
            navigateToUI1()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 檢查是否已同意過
        val sharedPrefs = getSharedPreferences("APP_ONBOARDING", Context.MODE_PRIVATE)
        if (sharedPrefs.getBoolean("IS_FIRST_LAUNCH_AGREED", false)) {
            navigateToUI1()
            return
        }

        setContentView(R.layout.activity_terms_privacy)

        cbAgree = findViewById(R.id.cbAgree)
        tvTermsLinks = findViewById(R.id.tvTermsLinks)
        tvWelcomeTitle = findViewById(R.id.tvWelcomeTitle)
        btnContinue = findViewById(R.id.btnContinue)
        btnExitApp = findViewById(R.id.btnExitApp)

        // 設定動態標題
        tvWelcomeTitle.text = getString(R.string.welcome_prefix) + getString(R.string.app_name)

        setupTermsLinks()

        cbAgree.setOnCheckedChangeListener { _, isChecked ->
            btnContinue.isEnabled = isChecked
        }

        btnContinue.setOnClickListener {
            // 1. 顯示 UGC 內容規範彈窗 (UGC 免責)
            ConsentDialogManager.showTermsAndPrivacyDialog(this) {
                // 紀錄同意狀態
                sharedPrefs.edit().putBoolean("IS_FIRST_LAUNCH_AGREED", true).apply()
                
                // 2. 顯示位置權限告知 (Google Play 合規)
                checkLocationPermissionWithDisclosure()
            }
        }

        btnExitApp.setOnClickListener {
            finishAffinity() // 退出 App
        }
    }

    private fun setupTermsLinks() {
        val fullText = getString(R.string.terms_checkbox_text)
        val termsLabel = getString(R.string.terms_service_label)
        val privacyLabel = getString(R.string.privacy_policy_label)

        val spannable = SpannableString(fullText)

        // 服務條款點擊
        val termsStart = fullText.indexOf(termsLabel)
        if (termsStart != -1) {
            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    showTermsDialog()
                }
                override fun updateDrawState(ds: TextPaint) {
                    ds.color = Color.parseColor("#4285F4")
                    ds.isUnderlineText = true
                }
            }, termsStart, termsStart + termsLabel.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // 隱私權政策點擊
        val privacyStart = fullText.indexOf(privacyLabel)
        if (privacyStart != -1) {
            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    showTermsDialog()
                }
                override fun updateDrawState(ds: TextPaint) {
                    ds.color = Color.parseColor("#4285F4")
                    ds.isUnderlineText = true
                }
            }, privacyStart, privacyStart + privacyLabel.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        tvTermsLinks.text = spannable
        tvTermsLinks.movementMethod = LinkMovementMethod.getInstance()
        tvTermsLinks.highlightColor = Color.TRANSPARENT
    }

    private fun showTermsDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_terms_title))
            .setMessage(getString(R.string.terms_content_full))
            .setPositiveButton(getString(R.string.confirm), null)
            .show()
    }

    private fun checkLocationPermissionWithDisclosure() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            navigateToUI1()
        } else {
            // 明確告知位置用途
            ConsentDialogManager.showLocationDisclosureDialog(this, {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }, {
                // 使用者拒絕說明，仍跳轉但無權限
                navigateToUI1()
            })
        }
    }

    private fun navigateToUI1() {
        val intent = Intent(this, UI1Activity::class.java)
        startActivity(intent)
        finish()
    }
}
