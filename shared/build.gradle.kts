import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidMultiplatformLibrary)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.kotlinSerialization)
}

kotlin {
  // Enable expect/actual classes (currently in Beta)
  compilerOptions {
    freeCompilerArgs.add("-Xexpect-actual-classes")
  }

  jvm()

  androidLibrary {
    namespace = "com.srilakshmikanthanp.clipbird.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()

    compilerOptions {
      jvmTarget = JvmTarget.JVM_11
    }

    androidResources {
      enable = true
    }

    withHostTest {
      isIncludeAndroidResources = true
    }
  }

  sourceSets {
    androidMain.dependencies {
      implementation(libs.compose.uiToolingPreview)
      implementation(libs.kable.default.permissions)
    }

    commonMain.dependencies {
      implementation(libs.compose.runtime)
      implementation(libs.compose.foundation)
      implementation(libs.compose.material3)
      implementation(libs.compose.ui)
      implementation(libs.compose.components.resources)
      implementation(libs.compose.uiToolingPreview)
      implementation(libs.androidx.lifecycle.viewmodelCompose)
      implementation(libs.androidx.lifecycle.runtimeCompose)
      implementation(libs.kable.core)
      implementation(libs.kotlinx.serialization.core)
      implementation(libs.kotlinx.serialization.protobuf)
    }

    commonTest.dependencies {
      implementation(libs.kotlin.test)
    }
  }
}

dependencies {
  androidRuntimeClasspath(libs.compose.uiTooling)
}
