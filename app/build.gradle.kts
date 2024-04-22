plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
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
    implementation ("com.squareup.retrofit2:converter-gson:2.10.0")
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
    // FCM 기본설정
    // Firebase Android BoM을 사용하면 앱에서 항상 호환되는 Firebase Android 라이브러리 버전을 사용합니다.
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    //FCM 사용 환경을 최적화하려면 프로젝트에서 Google 애널리틱스 사용 설정을 적극 권장합니다. Google 애널리틱스는 FCM의 메시지 전송 보고에 반드시 필요합니다.
    implementation("com.google.firebase:firebase-analytics")
    //FCM 을 수신할 수 있는 라이브러리
    implementation ("com.google.firebase:firebase-messaging")
   //Exoplayer 라이브러리
    implementation ("com.google.android.exoplayer:exoplayer-core:2.19.1");
    implementation ("com.google.android.exoplayer:exoplayer-ui:2.19.1");
    implementation ("com.google.android.exoplayer:exoplayer-hls:2.19.1");
    // WorkManager 라이브러리
    dependencies {
        val work_version = "2.9.0"
        // (Java only)
        implementation("androidx.work:work-runtime:$work_version")
    }

    }