plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "by.ssrlab.nasguide"
    compileSdk = 34

    bundle {
        language {
            @Suppress("UnstableApiUsage")
            enableSplit = false
        }
    }

    defaultConfig {
        applicationId = "by.ssrlab.nasguide"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        //First release (for tests and publishing)
        //NASB Guide 1.0.0
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

    dataBinding {
        enable = true
    }

    viewBinding {
        enable = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":ui"))
    implementation(project(":common-ui"))

    implementation(libs.koin.android)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)

    implementation(libs.coil)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(platform(libs.okhttp.bom))

    implementation(libs.kotlin.stdlib)
    implementation(libs.map)
    implementation(libs.map.navigation)
}