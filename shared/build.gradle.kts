import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidMultiplatformLibrary)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.ksp)
}

kotlin {
  jvmToolchain(25)

  compilerOptions {
    freeCompilerArgs.add("-Xexpect-actual-classes")
  }

  jvm {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_25
    }
  }

  androidLibrary {
    namespace = "com.srilakshmikanthanp.clipbird.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    minSdk = libs.versions.android.minSdk.get().toInt()

    compilerOptions {
      jvmTarget = JvmTarget.JVM_25
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
      implementation(libs.androidx.room.sqlite.wrapper)
      implementation(libs.koin.android)
      implementation(libs.androidx.activity.compose)
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
      implementation(libs.navigation.compose)
      implementation(libs.compose.material.icons.extended)
      implementation(libs.kable.core)
      implementation(libs.kotlinx.serialization.core)
      implementation(libs.kotlinx.serialization.protobuf)
      implementation(libs.androidx.room.runtime)
      implementation(libs.androidx.sqlite.bundled)
      implementation(libs.koin.core)
      implementation(libs.koin.compose)
      implementation(libs.koin.compose.viewmodel)
      api(libs.koin.annotations)
      implementation(libs.kermit)
    }

    jvmMain.dependencies {
      implementation(projects.jvmNative)
    }

    commonTest.dependencies {
      implementation(libs.kotlin.test)
    }
  }
}

dependencies {
  androidRuntimeClasspath(libs.compose.uiTooling)
  add("kspAndroid", libs.androidx.room.compiler)
  add("kspJvm", libs.androidx.room.compiler)
  add("kspAndroid", libs.koin.ksp.compiler)
  add("kspJvm", libs.koin.ksp.compiler)
}

ksp {
  arg("KOIN_USE_COMPOSE_VIEWMODEL", "true")
}
