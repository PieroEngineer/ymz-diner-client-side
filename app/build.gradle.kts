import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
//    alias(libs.plugins.secrets.gradle)
}

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        load(FileInputStream(localFile))
    }
}

android {
    namespace = "com.example.yummiziapp"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.yummiziapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Tells Room where to export its database schemas securely for migrations
        javaCompileOptions {
            annotationProcessorOptions {
                arguments(mapOf("room.schemaLocation" to "$projectDir/schemas"))
            }
        }

        buildConfigField(
            "String",
            "BASE_URL",
            "\"https://api.yummizi.com/\""
        )

        // Secret / per-dev — pulled from local.properties
        buildConfigField(
            "String",
            "ABLY_API_KEY",
            "\"${localProperties.getProperty("ABLY_API_KEY", "")}\""
        )
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        // 🌟 Upgraded to Java 17 for absolute compatibility with Kotlin 2.4.0 / AGP 9 graphs
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

}

//// 🛡️ Secrets Plugin Configuration
//secrets {
//    // Specify which properties file to read
//    propertiesFileName = "local.properties"
//
////    // Optional: Specify a fallback file for CI/CD environments (e.g. local.defaults.properties)
////    defaultPropertiesFileName = "local.defaults.properties"
//
//    // Ignore keys you don't want exposed in BuildConfig
//    ignoreList.add("sdk.dir")
//}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Compose navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room Runtime and Coroutine Extensions
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Networking & Serialization
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Inmutable lists
    implementation(libs.kotlinx.collections.immutable)

    // WebSockets (Ably)
    implementation(libs.ably.android)

    // AWS
    implementation(libs.amplify.core)
    implementation(libs.amplify.auth.cognito)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)
}

configurations.all {
    resolutionStrategy {
        // Enforces the correct 2.4.0 metadata parser across all app module compiler tasks (like KSP)
        force("org.jetbrains.kotlin:kotlin-metadata-jvm:2.4.0")
    }
}