plugins {
    id("com.android.application")
}

android {
    namespace = "com.win7e.yuan.stock"
    compileSdk = 36

    // The signingConfigs block is now only for local builds.
    // The CI process will handle signing separately.
    signingConfigs {
        create("release") {
            storeFile = file("apksign.keystore")
            storePassword = project.property("RELEASE_STORE_PASSWORD") as String
            keyAlias = project.property("RELEASE_KEY_ALIAS") as String
            keyPassword = project.property("RELEASE_KEY_PASSWORD") as String
        }
    }

    defaultConfig {
        applicationId = "com.win7e.yuan.stock"
        minSdk = 21
        targetSdk = 36
        versionCode = 2
        versionName = "1.21"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            // We remove the signingConfig from here for CI builds
            // This will produce an unsigned APK by default
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // GridLayout
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    // Retrofit and Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3") // Added for logging

    // ZXing
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
}
