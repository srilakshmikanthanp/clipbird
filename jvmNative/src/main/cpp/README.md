# Clipbird Native

This directory contains the C++ source code for the native implementation part of the clipbird.

The library is written in C++ but exposes only a C API. The JVM side calls that C API through
bindings that jextract generates from `include/clipbird.h`.

## File extensions

Each file extension marks which side of the C/C++ boundary a file belongs to.

| Extension | Location   | Language | Naming       | Purpose                                                                                                       |
|-----------|------------|----------|--------------|---------------------------------------------------------------------------------------------------------------|
| `.h`      | `include/` | C        | `snake_case` | Public C API read by jextract: opaque handle types, error code enums and functions. Must stay plain C.        |
| `.hxx`    | `src/`     | C++      | `snake_case` | Bridge header: defines the C handle structs, which own the C++ objects behind them.                           |
| `.cxx`    | `src/`     | C++      | `snake_case` | Bridge source: implements the C API in `extern "C"` and converts C++ exceptions to errors.                    |
| `.hpp`    | `src/`     | C++      | `PascalCase` | Internal C++ headers in `com::srilakshmikanthanp::clipbird`: interfaces, factories, platform classes.         |
| `.cpp`    | `src/`     | C++      | `PascalCase` | Internal C++ sources. Like `.hpp`, may include a `.h` for shared C types but never a `.hxx`.                   |

## Module layout

Every module follows the same structure. `fs` is the smallest example:

| File                                          | Role                                                |
|-----------------------------------------------|-----------------------------------------------------|
| `include/fs/directory_info.h`                 | C API                                               |
| `src/fs/directory_info.hxx`                   | Bridge header                                       |
| `src/fs/directory_info.cxx`                   | Bridge implementation (`extern "C"`)                |
| `src/fs/DirectoryInfo.hpp`                    | C++ interface                                       |
| `src/fs/DirectoryInfoFactory.hpp` / `.cpp`    | Picks the implementation for the current platform   |
| `src/fs/linux/LinuxDirectoryInfo.hpp` / `.cpp`| Linux implementation                                |

Platform implementations live in a subdirectory named after the platform (`linux/`, later
`macos/` or `windows/`). The factory selects one with `#if defined(...)` and fails the build with
`#error` on platforms that are not implemented yet.

## Adding a module

1. Declare the C API in `include/<module>/<name>.h` and include it from `include/clipbird.h`.
2. Add the C++ interface, factory and platform implementation as `.hpp`/`.cpp` under `src/<module>/`.
3. Add `<name>.hxx`/`<name>.cxx` to bridge the C API to the C++ interface.
4. List the sources in `src/CMakeLists.txt`: platform-independent files in the common list, and
   platform files in the matching platform block.
5. Wrap the generated binding in a Kotlin `*Handle` object under `jvmNative/src/main/kotlin`.

## Errors

C functions report failure through their return value (`NULL` or `false`). They store an error
code and message that `clipbird_error_last_error_code()` and `clipbird_error_last_error_message()`
return. Bridge code catches every C++ exception and converts it with `error::setLastError`.
