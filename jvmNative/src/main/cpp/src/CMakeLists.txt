add_library(clipbird SHARED)

if(WIN32)
  set_target_properties(clipbird PROPERTIES PREFIX "lib")
endif()

target_include_directories(
  clipbird
  PUBLIC  ${CMAKE_SOURCE_DIR}/include
  PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}
)

target_sources(
  clipbird
  PRIVATE
  advertiser/advertiser.cxx
  advertiser/ble/BleAdvertiserFactory.cpp
  advertiser/ble/ble_advertiser_factory.cxx
  discoverer/discoverer.cxx
  io/bluetooth/BluetoothManagerFactory.cpp
  io/bluetooth/bluetooth_manager_factory.cxx
  io/bluetooth/bluetooth_manager.cxx
  io/bluetooth/bluetooth_channel.cxx
  io/bluetooth/bluetooth_server.cxx
  error/Error.cpp
  error/error.cxx
  power/PowerHandlerFactory.cpp
  power/power_handler.cxx
)

if(UNIX)
  find_package(PkgConfig REQUIRED)
  pkg_check_modules(BLUEZ REQUIRED bluez)

  set(SDBUSCPP_BUILD_CODEGEN  OFF CACHE BOOL "" FORCE)
  set(SDBUSCPP_BUILD_DOCS     OFF CACHE BOOL "" FORCE)
  set(SDBUSCPP_BUILD_TESTS    OFF CACHE BOOL "" FORCE)
  set(SDBUSCPP_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
  set(SDBUSCPP_INSTALL        OFF CACHE BOOL "" FORCE)
  set(BUILD_SHARED_LIBS       OFF CACHE BOOL "" FORCE)

  FetchContent_Declare(
    sdbuscpp
    GIT_REPOSITORY https://github.com/Kistler-Group/sdbus-cpp.git
    GIT_TAG        v2.3.1
    GIT_SHALLOW    TRUE
  )

  FetchContent_MakeAvailable(sdbuscpp)

  target_sources(
    clipbird
    PRIVATE
    log/Log.cpp
    log/backend/LogCallbackBackend.cpp
    log/log.cxx
    advertiser/ble/linux/LinuxBleAdvertisementData.cpp
    advertiser/ble/linux/LinuxBleAdvertiser.cpp
    discoverer/ble/BleDiscovererFactory.cpp
    discoverer/ble/linux/LinuxBleDiscoverer.cpp
    discoverer/ble/ble_discoverer_factory.cxx
    io/bluetooth/linux/LinuxBluetoothManager.cpp
    io/bluetooth/linux/LinuxRfcommChannel.cpp
    io/bluetooth/linux/LinuxRfcommConnector.cpp
    io/bluetooth/linux/LinuxSdpResolver.cpp
    io/bluetooth/linux/LinuxRfcommServer.cpp
    io/bluetooth/linux/LinuxRfcommServerProfile.cpp
    power/linux/LinuxPowerHandler.cpp
  )

  target_include_directories(clipbird PRIVATE ${BLUEZ_INCLUDE_DIRS})
  target_compile_options(clipbird PRIVATE ${BLUEZ_CFLAGS_OTHER})
  target_link_libraries(
    clipbird
    PRIVATE
    ${BLUEZ_LIBRARIES}
    SDBusCpp::sdbus-c++
    Boost::log
    Boost::log_setup
    Boost::thread
    Boost::uuid
  )

  target_link_directories(clipbird PRIVATE ${BLUEZ_LIBRARY_DIRS})

endif()
