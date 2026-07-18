import clipbird.cpp.cmake.buildCmake
import clipbird.cpp.cmake.configureCmake
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Sync
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
  `java-library`
  alias(libs.plugins.kotlinJvm)
  id("de.infolektuell.jextract") version "1.1.0"
}

repositories {
  mavenCentral()
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(25)
  }
}

kotlin {
  jvmToolchain(25)
}

val nativeSourceDirectory = layout.projectDirectory.dir("src/main/cpp")
val nativeBuildDirectory = nativeSourceDirectory.dir("build")
val nativeResourcesDirectory = layout.buildDirectory.dir("generated/resources/main")

tasks.register<Exec>("configureNative") {
  val buildType = providers.gradleProperty("nativeBuildType").getOrElse("Debug")
  description = "Configure the clipbird native library"
  workingDir(nativeSourceDirectory.asFile)
  configureCmake(
    sourceDir = nativeSourceDirectory.asFile,
    buildDir = nativeBuildDirectory.asFile,
    buildType = buildType
  )
}

tasks.register<Exec>("buildNative") {
  description = "Build the clipbird native library"
  dependsOn("configureNative")
  workingDir(nativeSourceDirectory.asFile)
  buildCmake(
    sourceDir = nativeSourceDirectory.asFile,
    buildDir = nativeBuildDirectory.asFile
  )
}

tasks.register<Sync>("packageNative") {
  description = "Package the native library as a jar resource"
  dependsOn("buildNative")

  from(nativeBuildDirectory.dir("src")) {
    include("*.dll")
    include("*.so")
    include("*.dylib")
    into("native")
  }

  into(nativeResourcesDirectory)
}

tasks.named<Delete>("clean") {
  delete(nativeBuildDirectory)
}

tasks.named("processResources") {
  dependsOn("packageNative")
}

sourceSets {
  named("main") {
    resources.srcDir(nativeResourcesDirectory)
  }
}

jextract.libraries {
  val clipbird by registering {
    header = layout.projectDirectory.file("src/main/cpp/include/clipbird.h")
    headerClassName = "Clipbird"
    targetPackage = "com.srilakshmikanthanp.clipbird.ffi.bindings"
    includes.add(layout.projectDirectory.dir("src/main/cpp/include"))
  }

  sourceSets.named("main") {
    jextract.libraries.addLater(clipbird)
  }
}
