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
    testImplementation(libs.ktor.client.mock)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Async Images
    implementation(libs.coil3.coil.compose)
    implementation(libs.coil.network.okhttp)

    //mapbox
    implementation(libs.android)
    implementation(libs.maps.compose)
    implementation(libs.autofill)
    implementation(libs.discover)
    implementation(libs.place.autocomplete)
    implementation(libs.offline)
    implementation(libs.mapbox.search.android)
    implementation(libs.mapbox.search.android.ui)

    // navigation
    implementation(libs.androidx.navigation.compose)
    // meterial3
    implementation(libs.material3)

    // turf
    implementation(libs.mapbox.sdk.geojson)
    implementation(libs.mapbox.sdk.services)
    implementation(libs.mapbox.sdk.turf)
    implementation(libs.mapbox.sdk.core)

    // BottomNavigation
    implementation(libs.androidx.material)

    // testing
    testImplementation(libs.kotlinx.coroutines.test) // Use the latest version
    testImplementation(libs.mockk)

    // Mocking (Mockito)
    testImplementation (libs.mockito.core)
    testImplementation (libs.mockito.kotlin)
    testImplementation(kotlin("test"))


    // Coroutine testing Turbine
    testImplementation("app.cash.turbine:turbine:1.2.0")
    testImplementation ("androidx.arch.core:core-testing:2.2.0")

    //graf
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("androidx.compose.ui:ui:1.5.0")
    implementation ("androidx.compose.material:material:1.5.0")


}