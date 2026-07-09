package clipbird.cpp.cmake

import org.gradle.api.tasks.Exec
import java.io.File

fun Exec.configureCmake(sourceDir: File, buildDir: File) {
  workingDir(sourceDir)
  commandLine(
    "cmake",
    "-B",
    buildDir.absolutePath
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
