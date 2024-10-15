plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.cpr3663.cpr_scouting_app"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.cpr3663.cpr_scouting_app"
        minSdk = 30
        targetSdk = 34
        versionCode = 9
<<<<<<< Updated upstream
        versionName = "1.2.0"
=======
        versionName = "1.1.2"
>>>>>>> Stashed changes
        setProperty("archivesBaseName", "CPR-Scout-$versionName")

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.preference)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}