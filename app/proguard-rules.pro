#
# Preserve generic signatures and annotations for Kotlin Coroutines and Reflection
#-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
#-dontwarn kotlinx.coroutines.**

# Retrofit & OkHttp (Preserve network DTO fields used in JSON serialization)
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
    @kotlinx.serialization.SerialName <fields>;
}
-dontwarn okhttp3.**
-dontwarn retrofit2.**

# Hilt Dependency Injection
#-keep class dagger.hilt.** { *; }
#-keep class com.google.dagger.** { *; }
#-dontwarn dagger.hilt.**

#
#Kotlinx Serialization (Preserves runtime reflection for @Serializable models and generated serializers)
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keep class * implements kotlinx.serialization.KSerializer { *; }
-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Coil 3 Image Loader (Updated package namespace for Coil v3.x)
#-keep class coil3.** { *; }
#-dontwarn coil3.**

#
# Room Database (Preserves generated DAOs and Database abstractions)
#-keep class * extends androidx.room.RoomDatabase
#-dontwarn androidx.room.**

# AWS Amplify Cognito
-keep class com.amplifyframework.** { *; }
-keep class com.amazonaws.** { *; }
-dontwarn com.amplifyframework.**
# Preserve raw resource IDs loaded via reflection
-keepclassmembers class com.example.yummiziapp.R$raw {
    public static final int amplifyconfiguration;
    public static final int awsconfiguration;
}

# MVVM Data Layer (Protect Network Request/Response Models from Obfuscation)  # TODO (#904): Change this approach for "@Keep"
-keepclassmembers class com.example.yummiziapp.core.data.local.** { *; }
-keepclassmembers class com.example.yummiziapp.core.data.network.** { *; }
# Keep any class or member explicitly annotated with @Keep
-keep @androidx.annotation.Keep class * { *; }
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
    @androidx.annotation.Keep <methods>;
}
