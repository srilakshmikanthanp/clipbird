import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
}

kotlin {
  jvmToolchain(25)
}

dependencies {
  implementation(projects.shared)
  implementation(libs.koin.core)
  implementation(compose.desktop.currentOs)
  implementation(libs.kotlinx.coroutinesSwing)
  implementation(libs.compose.uiToolingPreview)
}

compose.desktop {
  application {
    mainClass = "com.srilakshmikanthanp.clipbird.MainKt"
    jvmArgs += listOf("--enable-native-access=ALL-UNNAMED")
    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "com.srilakshmikanthanp.clipbird"
      packageVersion = "1.0.0"
    }
  }
}