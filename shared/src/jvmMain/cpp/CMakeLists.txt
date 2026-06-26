cmake_minimum_required(VERSION 3.20)

project(clipbird VERSION 1.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

include(FetchContent)

add_library(
  clipbird
  SHARED
)

target_include_directories(
  clipbird
  PUBLIC
  ${CMAKE_CURRENT_SOURCE_DIR}/include
  PRIVATE
  ${CMAKE_CURRENT_SOURCE_DIR}/src
)

target_sources(
  clipbird
  PRIVATE
  src/advertiser/AdvertiserFfi.cpp
  src/bluetooth/ble/BleAdvertiserFactory.cpp
  src/bluetooth/ble/BleAdvertiserFactoryFfi.cpp
  src/bluetooth/rfcomm/BluetoothManagerFactory.cpp
  src/bluetooth/rfcomm/BluetoothManagerFactoryFfi.cpp
  src/bluetooth/rfcomm/BluetoothManagerFfi.cpp
  src/error/Error.cpp
  src/error/ErrorFfi.cpp
  src/io/ChannelFfi.cpp
  src/io/ServerFfi.cpp
)

if(UNIX)
  find_package(PkgConfig REQUIRED)
  pkg_check_modules(BLUEZ REQUIRED bluez)

  set(SDBUSCPP_BUILD_CODEGEN OFF CACHE BOOL "" FORCE)
  set(SDBUSCPP_BUILD_DOCS OFF CACHE BOOL "" FORCE)
  set(SDBUSCPP_BUILD_TESTS OFF CACHE BOOL "" FORCE)
  set(SDBUSCPP_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
  set(SDBUSCPP_INSTALL OFF CACHE BOOL "" FORCE)
  set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)

  FetchContent_Declare(
    sdbuscpp
    GIT_REPOSITORY https://github.com/Kistler-Group/sdbus-cpp.git
    GIT_TAG v2.3.1
    GIT_SHALLOW TRUE
  )

  FetchContent_MakeAvailable(sdbuscpp)

  target_sources(
    clipbird
    PRIVATE
    src/bluetooth/ble/linux/LinuxBleAdvertisementData.cpp
    src/bluetooth/ble/linux/LinuxBleAdvertiser.cpp
    src/bluetooth/rfcomm/linux/LinuxBluetoothManager.cpp
    src/bluetooth/rfcomm/linux/LinuxRfcommChannel.cpp
    src/bluetooth/rfcomm/linux/LinuxRfcommServer.cpp
  )

  target_include_directories(
    clipbird
    PRIVATE
    ${BLUEZ_INCLUDE_DIRS}
  )

  target_compile_options(
    clipbird
    PRIVATE
    ${BLUEZ_CFLAGS_OTHER}
  )

  target_link_libraries(
    clipbird
    PRIVATE
    ${BLUEZ_LIBRARIES}
    SDBusCpp::sdbus-c++
  )

  target_link_directories(
    clipbird
    PRIVATE
    ${BLUEZ_LIBRARY_DIRS}
  )
elseif(WIN32)
  target_sources(
    clipbird
    PRIVATE
    src/bluetooth/ble/windows/WindowsBleAdvertiser.cpp
    src/bluetooth/rfcomm/windows/WindowsBluetoothManager.cpp
    src/bluetooth/rfcomm/windows/WindowsRfcommChannel.cpp
    src/bluetooth/rfcomm/windows/WindowsRfcommServer.cpp
  )
endif()
