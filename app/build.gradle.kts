import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

android {
    namespace = "com.example.myapplication01"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.baichuan"
        testApplicationId = "com.example.baichuan.test"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.0-RELEASE"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // 從 local.properties 讀取 Token
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        val githubToken = localProperties.getProperty("GITHUB_TOKEN") ?: "ghp_Y3Rb2ZHTrMcVDujrkjzv6SjhYicGU3107XgZ"
        
        buildConfigField("String", "GITHUB_TOKEN", "\"ghp_Y3Rb2ZHTrMcVDujrkjzv6SjhYicGU3107XgZ\"")
        buildConfigField("String", "GITHUB_API_URL", "\"https://api.github.com/repos/enshenglin05-coder/baichuan-v2/contents/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }



    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.play.services.location)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    
    // Networking
    implementation(libs.okhttp)
    implementation(libs.gson)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}