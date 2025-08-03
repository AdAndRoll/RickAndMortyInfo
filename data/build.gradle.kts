// build.gradle.kts (module: data)
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)

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
    implementation(project(":domain")) {
        exclude(group = "androidx.paging", module = "paging-common-jvm")
    } // Зависимость от модуля domain

    // Retrofit for network requests
    implementation(libs.retrofit)
    implementation(libs.converter.gson) // For JSON conversion (вам также нужно converter.moshi, если используете Moshi)
    // У вас в app-модуле: implementation(libs.converter.moshi)
    // Убедитесь, что используете один и тот же конвертер: Moshi или Gson
    // Если используете Moshi, то:
    // implementation(libs.converter.moshi)
    // implementation(libs.moshi.kotlin)


    // Room for local database caching
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.room.ktx) // Kotlin extensions for Room
    ksp(libs.androidx.room.compiler) // Annotation processor for Room

    // Paging 3 for pagination
    implementation(libs.androidx.paging.runtime.ktx)


    // Hilt for Dependency Injection
    implementation(libs.hilt.android) // Используйте псевдоним
    ksp(libs.hilt.compiler) // Hilt annotation processor

    // Logging Interceptor (Good for debugging network requests) - Optional but highly recommended
    implementation(libs.logging.interceptor)


    // Тестовые зависимости, добавленные для RemoteMediator и репозитория
    testImplementation("org.junit.jupiter:junit-jupiter:5.13.4")
    testImplementation("io.mockk:mockk:1.14.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("androidx.paging:paging-common-ktx:3.3.6")
    testImplementation("androidx.paging:paging-testing:3.3.6")
    testImplementation("androidx.room:room-testing:2.7.2")
    testImplementation("androidx.test:core:1.7.0")
    testImplementation(kotlin("test"))
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<Test> {
    useJUnitPlatform() // Включаем JUnit 5
}