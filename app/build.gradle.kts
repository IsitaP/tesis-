import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

val placesApiKey = localProperties.getProperty("PLACES_API_KEY") ?: ""

// Sin esta key Google Maps se queda en blanco y Places no inicializa
if (placesApiKey.isBlank()) {
    logger.warn("⚠ Falta PLACES_API_KEY en local.properties: el mapa de Rutas saldrá en blanco")
}

android {
    namespace = "com.rebottle.app"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.rebottle.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["PLACES_API_KEY"] = placesApiKey
        buildConfigField(
            "String",
            "PLACES_API_KEY",
            "\"$placesApiKey\""
        )
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
        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
    implementation("com.google.firebase:firebase-storage")
    implementation(libs.googleid)
    implementation(libs.maps.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.3")
    implementation(libs.places)
    implementation("com.google.android.gms:play-services-location:21.4.0")
    implementation("com.google.firebase:firebase-functions")
    implementation("com.google.firebase:firebase-messaging")
}