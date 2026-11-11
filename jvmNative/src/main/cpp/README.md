# Clipbird Native

This directory contains the C++ source code for the native implementation part of the clipbird.

The library is written in C++ but exposes only a C API. The JVM side calls that C API through
bindings that jextract generates from `include/clipbird.h`.

## File extensions

Each file extension marks which side of the C/C++ boundary a file belongs to.

| Extension | Location   | Language | Purpose                                                                                     |
|-----------|------------|----------|---------------------------------------------------------------------------------------------|
| `.h`      | `include/` | C        | Public C API: opaque handle types, error code enums and function declarations.              |
| `.hxx`    | `src/`     | C++      | Bridge header: defines the C handle structs, which own the C++ objects behind them.         |
| `.cxx`    | `src/`     | C++      | Bridge source: implements the C API in `extern "C"` and converts C++ exceptions to errors.  |
| `.hpp`    | `src/`     | C++      | Internal C++ headers: interfaces, factories and platform classes.                           |
| `.cpp`    | `src/`     | C++      | Internal C++ sources.                                                                       |

- `.h` headers must stay valid C: no C++ types, no namespaces, and declarations wrapped in
  `extern "C"` when compiled as C++. They are what jextract reads.
- `.hxx`/`.cxx` files are where C handles and C++ objects meet. They use snake_case names that
  match the `.h` file they implement. A `.hxx` may also declare C++ helpers that other bridge
  files need to create a handle (see `io/bluetooth/bluetooth_channel.hxx`).
- `.hpp`/`.cpp` files are C++ in the `com::srilakshmikanthanp::clipbird` namespace. They use
  PascalCase names that match the class they declare. They may include a public `.h` to reuse a
  C type such as an error code enum or a callback typedef, but never a `.hxx`.

## Module layout

Every module follows the same structure. `fs` is the smallest example:

```
include/fs/directory_info.h            C API
src/fs/directory_info.hxx              bridge header
src/fs/directory_info.cxx              bridge implementation (extern "C")
src/fs/DirectoryInfo.hpp               C++ interface
src/fs/DirectoryInfoFactory.hpp/.cpp   picks the implementation for the current platform
src/fs/linux/LinuxDirectoryInfo.hpp/.cpp   Linux implementation
```

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
