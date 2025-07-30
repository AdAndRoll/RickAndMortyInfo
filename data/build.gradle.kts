// build.gradle.kts (module: data)
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // Добавьте плагин Hilt для KSP (Kotlin Symbol Processing)
    alias(libs.plugins.hilt.android)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        // Убедитесь, что версии Java соответствуют Hilt и Room требованиям
        sourceCompatibility = JavaVersion.VERSION_1_8 // Java 8 требуется для Hilt и Room
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8" // JvmTarget 1.8 также требуется
    }
}

dependencies {

    // Existing dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Project Module Dependencies
    // Data Layer depends on Domain Layer
    implementation(project(":domain"))

    // Retrofit for network requests
    implementation(libs.retrofit)
    implementation(libs.converter.gson) // For JSON conversion

    // Room for local database caching
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx) // Kotlin extensions for Room
    ksp(libs.androidx.room.compiler) // Annotation processor for Room

    // Paging 3 for pagination
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.common.ktx) // Common utilities for Paging 3

    // Hilt for Dependency Injection
    implementation(libs.hilt.android) // Используйте псевдоним
    ksp(libs.hilt.compiler) // Hilt annotation processor

    // Logging Interceptor (Good for debugging network requests) - Optional but highly recommended
    implementation(libs.logging.interceptor)
}