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
  src/advertiser/advertiser.cxx
  src/advertiser/ble/BleAdvertiserFactory.cpp
  src/advertiser/ble/ble_advertiser_factory.cxx
  src/io/bluetooth/rfcomm/BluetoothManagerFactory.cpp
  src/io/bluetooth/rfcomm/BluetoothManagerFactoryFfi.cpp
  src/io/bluetooth/rfcomm/BluetoothManagerFfi.cpp
  src/error/Error.cpp
  src/error/error.cxx
  src/io/ChannelFfi.cpp
  src/io/ServerFfi.cpp
)

if(UNIX)
  find_package(PkgConfig REQUIRED)
  find_package(Boost REQUIRED COMPONENTS log log_setup)
  pkg_check_modules(BLUEZ REQUIRED bluez)

  if(TARGET Boost::headers)
    set(CLIPBIRD_BOOST_TARGET Boost::headers)
  else()
    set(CLIPBIRD_BOOST_TARGET Boost::boost)
  endif()

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
    src/log/Log.cpp
    src/log/LogFfi.cpp
    src/advertiser/ble/linux/LinuxBleAdvertisementData.cpp
    src/advertiser/ble/linux/LinuxBleAdvertiser.cpp
    src/io/bluetooth/rfcomm/linux/LinuxBluetoothManager.cpp
    src/io/bluetooth/rfcomm/linux/LinuxRfcommChannel.cpp
    src/io/bluetooth/rfcomm/linux/LinuxRfcommClientProfile.cpp
    src/io/bluetooth/rfcomm/linux/LinuxRfcommConnector.cpp
    src/io/bluetooth/rfcomm/linux/LinuxRfcommServer.cpp
    src/io/bluetooth/rfcomm/linux/LinuxRfcommServerProfile.cpp
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

  target_compile_definitions(
    clipbird
    PRIVATE
    BOOST_LOG_DYN_LINK
  )

  target_link_libraries(
    clipbird
    PRIVATE
    ${BLUEZ_LIBRARIES}
    SDBusCpp::sdbus-c++
    ${CLIPBIRD_BOOST_TARGET}
    Boost::log
    Boost::log_setup
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
    src/advertiser/ble/windows/WindowsBleAdvertiser.cpp
    src/io/bluetooth/rfcomm/windows/WindowsBluetoothManager.cpp
    src/io/bluetooth/rfcomm/windows/WindowsRfcommChannel.cpp
    src/io/bluetooth/rfcomm/windows/WindowsRfcommServer.cpp
  )
endif()
