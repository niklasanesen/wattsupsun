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
        minSdk = 24
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
    implementation("com.mapbox.search:place-autocomplete:2.8.0-rc.1")
    implementation("com.mapbox.search:offline:2.8.0-rc.1")
    implementation("com.mapbox.search:mapbox-search-android:2.8.0-rc.1")
    implementation("com.mapbox.search:mapbox-search-android-ui:2.8.0-rc.1")
    implementation("com.google.android.gms:play-services-location:20.0.0")
    implementation("com.mapbox.mapboxsdk:mapbox-android-core:5.0.0")
}