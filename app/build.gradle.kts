import android.annotation.SuppressLint

plugins {
    id ("com.android.application")
}

android {
    namespace = "com.usage.access.app"
    compileSdk = 32

    defaultConfig {
        applicationId = "com.usage.access.app"
        minSdk = 22
        @SuppressLint("ExpiredTargetSdkVersion")
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner ="androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation ("androidx.appcompat:appcompat:1.5.1")
    implementation ("com.google.android.material:material:1.7.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.navigation:navigation-fragment:2.5.3")
    implementation ("androidx.navigation:navigation-ui:2.5.3")
    implementation (project(path = ":helper"))
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.4")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.0")
}