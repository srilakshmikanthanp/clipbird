package clipbird.cpp.cmake

import org.gradle.api.tasks.Exec
import java.io.File

fun Exec.configureCmake(sourceDir: File, buildDir: File, buildType: String = "Release") {
  workingDir(sourceDir)
  commandLine(
    "cmake",
    "-B",
    buildDir.absolutePath,
    "-DCMAKE_BUILD_TYPE=$buildType"
  )
}

fun Exec.buildCmake(sourceDir: File, buildDir: File) {
  workingDir(sourceDir)
  commandLine(
    "cmake",
    "--build",
    buildDir.absolutePath
  )
}
