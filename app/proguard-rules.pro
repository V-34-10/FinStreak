# ─── Keep source file names & line numbers for crash reports ─────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ─── Application classes ──────────────────────────────────────────────────────
-keep class com.finance.finstreak.** { *; }

# ─── Room ─────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keepclassmembers class * extends androidx.room.RoomDatabase {
    abstract *;
}
-dontwarn androidx.room.**

# ─── Kotlin Serialization ─────────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.finance.finstreak.**$$serializer { *; }
-keepclassmembers class com.finance.finstreak.** {
    *** Companion;
}
-keepclasseswithmembers class com.finance.finstreak.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ─── Koin ─────────────────────────────────────────────────────────────────────
-keepnames class org.koin.** { *; }
-keep class org.koin.core.** { *; }
-keep class org.koin.android.** { *; }
-dontwarn org.koin.**

# ─── DataStore ────────────────────────────────────────────────────────────────
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}
-dontwarn androidx.datastore.**

# ─── Coroutines ───────────────────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ─── Jetpack Compose ──────────────────────────────────────────────────────────
-dontwarn androidx.compose.**
-keep class androidx.compose.runtime.** { *; }

# ─── Lifecycle / ViewModel ────────────────────────────────────────────────────
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keepclassmembers class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# ─── Navigation Compose ───────────────────────────────────────────────────────
-dontwarn androidx.navigation.**

# ─── Google Play In-App Review ────────────────────────────────────────────────
-keep class com.google.android.play.core.review.** { *; }
-dontwarn com.google.android.play.**

# ─── Android core ─────────────────────────────────────────────────────────────
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
