import android.annotation.SuppressLint
plugins {
    id("com.android.library")
}

android {
    namespace = "com.usage.access.helper"
    compileSdkVersion = "android-31"
    buildToolsVersion = "30.0.2"

    defaultConfig {
        minSdk = 22
        @SuppressLint("ExpiredTargetSdkVersion")
        targetSdk = 30
        version = 1
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation( "androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
}