# Baichuan 1.0 Release ProGuard Rules

# 1. GSON / Networking Models
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.example.myapplication01.RequirementCard { *; }
-keep class com.example.myapplication01.CategoryItem { *; }
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.TypeAdapter

# 2. OkHttp / Okio
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# 3. UI Components (ViewPager2 / RainbowBorderWrapper)
-keep class androidx.viewpager2.widget.ViewPager2 { *; }
-keep class com.example.myapplication01.LightAppSuiteBoxKt { *; }

# 4. Compose / Kotlin Coroutines
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory { *; }
-keep class retrofit2.KotlinExtensions* { *; }
-dontwarn kotlinx.coroutines.**

# 5. BuildConfig
-keep class com.example.myapplication01.BuildConfig { *; }
