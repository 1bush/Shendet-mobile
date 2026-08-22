# General Android
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Moshi
-keep class com.example.shendet.data.model.** { *; }
-keep class com.squareup.moshi.** { *; }

# OKHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Compose
-keep class androidx.compose.material.icons.** { *; }

# Navigation
-keep class kotlinx.serialization.json.** { *; }
-keep @kotlinx.serialization.Serializable class * { *; }

# Models
-keepclassmembers class com.example.shendet.data.model.** { *; }
