plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies {
    // Корутинсы для Flow и suspend функций
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.paging.common.jvm)

    // Тесты (если нужны)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
}