import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidMultiplatformLibrary)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.buildkonfig)
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
      implementation(libs.androidx.core.ktx)
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
      implementation(libs.aboutlibraries.core)
      implementation(libs.aboutlibraries.compose.m3)
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

buildkonfig {
  packageName = "com.srilakshmikanthanp.clipbird"
  defaultConfigs {
    buildConfigField(INT, "CLIPBOARD_MAX_SIZE", providers.gradleProperty("clipboard.max.size").get())
    buildConfigField(STRING, "APP_NAME", providers.gradleProperty("app.name").get())
    buildConfigField(STRING, "APP_PACKAGE_NAME", providers.gradleProperty("app.packageName").get())
    buildConfigField(STRING, "APP_DESCRIPTION", providers.gradleProperty("app.description").get())
    buildConfigField(STRING, "APP_VERSION", providers.gradleProperty("app.version").get())
    buildConfigField(STRING, "APP_HOMEPAGE", providers.gradleProperty("app.homepage").get())
    buildConfigField(STRING, "APP_SOURCE_PAGE", providers.gradleProperty("app.sourcePage").get())
    buildConfigField(STRING, "APP_ISSUES_PAGE", providers.gradleProperty("app.issuesPage").get())
    buildConfigField(STRING, "APP_DONATE_PAGE", providers.gradleProperty("app.donatePage").get())
  }
}
