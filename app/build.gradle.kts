plugins {
    id("com.android.application")
}

android {
    namespace = "com.android.tiki_taka"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.tiki_taka"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation ("androidx.appcompat:appcompat-resources:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Retrofit 라이브러리
    implementation ("com.squareup.retrofit2:retrofit:2.6.4")
    // Gson 변환기 라이브러리
    implementation ("com.squareup.retrofit2:converter-gson:2.6.4")
    // Scalars 변환기 라이브러리
    implementation ("com.squareup.retrofit2:converter-scalars:2.6.4")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    // 새로운: AndroidX 라이브러리
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    // 글라이드
    implementation ("com.github.bumptech.glide:glide:4.12.0") //Glide 라이브러리의 핵심 기능을 포함
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0") //커스텀 Glide 모듈 지원
    // 유튜브 동영상 재생 라이브러리
    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    // 사진 확대 후 크롭 라이브러리
    implementation ("com.github.yalantis:ucrop:2.2.8-native")
    // 바텀 시트 다이얼로그 라이브러리
    implementation ("com.google.android.material:material:1.11.0")

}