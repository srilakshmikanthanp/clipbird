import dev.nucleusframework.desktop.application.dsl.TargetFormat

plugins {
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.nucleusframework)
  alias(libs.plugins.aboutlibraries)
}

kotlin {
  jvmToolchain(25)
}

dependencies {
  implementation(projects.shared)
  implementation(libs.koin.core)
  implementation(compose.desktop.currentOs)
  implementation(libs.material3.v190)
  implementation(libs.kotlinx.coroutinesSwing)
  implementation(libs.compose.uiToolingPreview)
  implementation(libs.composenativetray)
  implementation(libs.nucleus.darkmode.detector)
  implementation(libs.nucleus.application)
  implementation(libs.nucleus.decorated.window.tao)
}

nucleus.application {
  mainClass = "com.srilakshmikanthanp.clipbird.MainKt"
  jvmArgs += listOf("--enable-native-access=ALL-UNNAMED")
  nativeDistributions {
    targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
    packageName = "com.srilakshmikanthanp.clipbird"
    packageVersion = providers.gradleProperty("app.version").get()
    linux { iconFile.set(project.file("src/main/resources/logo.png")) }
  }
}
