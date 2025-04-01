plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin") // Hilt 플러그인 적용
    id("kotlin-kapt")
}

android {
    namespace = "com.junseo.newsreminder"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
        compose = true
    }

    defaultConfig {
        applicationId = "com.junseo.newsreminder"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            buildConfigField ("boolean", "LOG", "false")
            buildConfigField ("boolean", "LOGFILE", "false")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            // 로그설정을 비활성화 하려면 false로 변경
            buildConfigField ("boolean", "LOG", "true")
            buildConfigField ("boolean", "LOGFILE", "true")
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.5"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.compose.compiler:compiler:1.5.15")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // JSON 변환용 (Gson)

    // log4j
    implementation("log4j:log4j:1.2.17")
    implementation("de.mindpipe.android:android-logging-log4j:1.0.3")

    // Compose ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // HTML 파싱
    implementation("org.jsoup:jsoup:1.16.1")

    // iamge loader
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Hilt 의존성 추가
    implementation("com.google.dagger:hilt-android:2.44") // Hilt Android 의존성
    kapt("com.google.dagger:hilt-android-compiler:2.44") // Hilt Android 컴파일러 의존성
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

}