import dev.nucleusframework.desktop.application.dsl.TargetFormat
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.nucleusframework)
  alias(libs.plugins.aboutlibraries)
}

val jdk25 = javaToolchains.launcherFor {
  languageVersion = JavaLanguageVersion.of(25)
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
  implementation(libs.autolaunch)
  implementation(libs.compose.material.icons.extended)
}

nucleus.application {
  mainClass = "com.srilakshmikanthanp.clipbird.MainKt"
  javaHome = jdk25.get().metadata.installationPath.asFile.absolutePath
  jvmArgs += listOf("--enable-native-access=ALL-UNNAMED")
  nativeDistributions {
    targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
    packageVersion = providers.gradleProperty("app.version").get()
    homepage = providers.gradleProperty("app.homepage").get()
    appName = providers.gradleProperty("app.name").get()
    packageName = providers.gradleProperty("app.packageName").get()
    description = providers.gradleProperty("app.description").get()
    vendor = providers.gradleProperty("app.maintainerName").get()
    linux {
      iconFile.set(project.file("src/main/resources/logo.png"))
      debMaintainer = providers.gradleProperty("app.maintainerEmail").get()
      appCategory = "Utility"
      menuGroup = "Utility"
      appName = providers.gradleProperty("app.name").get()
      packageName = providers.gradleProperty("app.packageName").get()
    }
  }
}
