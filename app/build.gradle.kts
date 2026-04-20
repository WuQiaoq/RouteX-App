plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("plugin.serialization") version "1.9.0"
}

// 1. Configuración personalizada que SÍ permite ser resuelta (extraer archivos)
val libgdxNatives by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

android {
    namespace = "com.example.routex_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.routex_app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        getByName("release") {
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

    sourceSets {
        getByName("main") {
            // Ruta estándar donde Android busca librerías nativas
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/STABLE"
            pickFirsts += "lib/*/libgdx.so"
        }
    }
}

dependencies {
    // Android Base
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Ktor & Utils
    implementation("com.google.android.material:material:1.12.0")
    implementation("io.ktor:ktor-client-android:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("io.ktor:ktor-client-logging:2.3.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("io.ktor:ktor-client-core:2.3.7")

    // --- LIBGDX ---
    val gdxVersion = "1.12.1"
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")

    libgdxNatives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    libgdxNatives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    libgdxNatives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    libgdxNatives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
}

tasks.register<Copy>("copyAndroidNatives") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    // Forzamos la resolución de la configuración justo cuando se necesita
    val filesToCopy = libgdxNatives.incoming.artifactView { }.files

    from(filesToCopy.map { zipTree(it) })
    into("src/main/jniLibs")

    // Buscamos los .so ignorando la estructura interna caprichosa de los JARs
    include("**/*.so")

    eachFile {
        // Clasificamos según el nombre de la carpeta en el JAR o el nombre del archivo
        val abi = when {
            path.contains("arm64-v8a") -> "arm64-v8a"
            path.contains("armeabi-v7a") -> "armeabi-v7a"
            path.contains("x86_64") -> "x86_64"
            path.contains("x86") -> "x86"
            else -> null
        }

        if (abi != null) {
            path = "$abi/$name"
        } else {
            exclude()
        }
    }
    includeEmptyDirs = false
}

// 3. Forzar ejecución antes de que Android intente unir las librerías
tasks.configureEach {
    if (name.contains("merge") && name.contains("JniLibFolders")) {
        dependsOn("copyAndroidNatives")
    }
}