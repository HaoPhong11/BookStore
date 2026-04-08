plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.bookstore"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.bookstore"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.foundation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    // Added ui-tooling for Preview support
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3")

    // 1. Navigation (Chuyển màn hình)
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // 2. ViewModel & Lifecycle (Mô hình MVVM)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // 3. Material Icons Extended (Icon có sẵn)
    implementation("androidx.compose.material:material-icons-extended:1.6.3")

    // 4. Coil (Load ảnh từ mạng)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // 5. Retrofit & Gson (Gọi API)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // 6. Coroutines (Chạy ngầm)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // 7. Hilt Navigation Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    // Két sắt DataStore (Hiện đại hơn SharedPreferences nhiều)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Lõi Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.55")
    kapt("com.google.dagger:hilt-compiler:2.55")
}