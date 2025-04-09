plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "no.uio.ifi.in2000.team_43"
    compileSdk = 35

    defaultConfig {
        applicationId = "no.uio.ifi.in2000.team_43"
        minSdk = 26
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.logging)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Async Images
    implementation(libs.coil3.coil.compose)
    implementation(libs.coil.network.okhttp)

    //mapbox
    implementation(libs.android)
    implementation(libs.maps.compose)
    implementation("com.mapbox.search:autofill:2.8.0-rc.1")
    implementation("com.mapbox.search:discover:2.8.0-rc.1")
    implementation(libs.place.autocomplete)
    implementation("com.mapbox.search:offline:2.8.0-rc.1")
    implementation(libs.mapbox.search.android)
    implementation(libs.mapbox.search.android.ui)

    // navigation
    implementation(libs.androidx.navigation.compose)
    // meterial3
    implementation("androidx.compose.material3:material3:1.3.1")

    // turf
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-geojson:7.3.2")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-services:7.3.2")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-turf:7.3.2")
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-core:7.3.2")

    // BottomNavigation
    implementation("androidx.compose.material:material:1.7.8")

    // testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3") // Use the latest version
    testImplementation("io.mockk:mockk:1.13.8")

    // Mocking (Mockito)
    testImplementation ("org.mockito:mockito-core:5.5.0")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:5.0.0")

}