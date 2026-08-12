import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.ksp)
  alias(libs.plugins.aboutlibraries)
}

kotlin {
  compilerOptions {
    jvmTarget = JvmTarget.JVM_25
  }
}

dependencies {
  implementation(projects.shared)

  implementation(libs.koin.android)
  implementation(libs.koin.annotations)
  ksp(libs.koin.ksp.compiler)
  implementation(libs.androidx.activity.compose)
  implementation(libs.compose.foundation)
  implementation(libs.compose.material3)

  implementation(libs.androidx.lifecycle.viewmodelCompose)
  implementation(libs.androidx.lifecycle.runtimeCompose)
  implementation(libs.compose.uiToolingPreview)
  debugImplementation(libs.compose.uiTooling)
}

ksp {
  arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
}

android {
  namespace = "com.srilakshmikanthanp.clipbird"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "com.srilakshmikanthanp.clipbird"
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()
    versionCode = providers.gradleProperty("app.versionCode").get().toInt()
    versionName = providers.gradleProperty("app.version").get()
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
  }
}