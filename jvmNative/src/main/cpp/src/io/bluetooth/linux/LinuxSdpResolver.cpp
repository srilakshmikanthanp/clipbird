#include "LinuxSdpResolver.hpp"
#include "io/IOException.hpp"

#include <bluetooth/sdp.h>
#include <bluetooth/sdp_lib.h>

#include <memory>

namespace {

struct SdpProtocolListDeleter {
  void operator()(sdp_list_t* l) const noexcept {
    for (auto* e = l; e; e = e->next) {
      sdp_list_free(static_cast<sdp_list_t*>(e->data), nullptr);
    }
    sdp_list_free(l, nullptr);
  }
};

struct SdpSessionDeleter {
  void operator()(sdp_session_t* s) const noexcept {
    sdp_close(s);
  }
};

struct SdpListDeleter {
  void operator()(sdp_list_t* l) const noexcept {
    sdp_list_free(l, nullptr);
  }
};

struct SdpRecordListDeleter {
  void operator()(sdp_list_t* l) const noexcept {
    sdp_list_free(l, reinterpret_cast<sdp_free_func_t>(sdp_record_free));
  }
};

using SdpProtocolList = std::unique_ptr<sdp_list_t, SdpProtocolListDeleter>;
using SdpSession      = std::unique_ptr<sdp_session_t, SdpSessionDeleter>;
using SdpList         = std::unique_ptr<sdp_list_t, SdpListDeleter>;
using SdpRecordList   = std::unique_ptr<sdp_list_t, SdpRecordListDeleter>;

}  // namespace

namespace clipbird::io::bluetooth {

std::optional<uint8_t> LinuxSdpResolver::rfcommChannel(const bdaddr_t& remote, const boost::uuids::uuid& serviceUuid) {
  bdaddr_t local = {{0, 0, 0, 0, 0, 0}};
  SdpSession session{sdp_connect(&local, &remote, SDP_RETRY_IF_BUSY)};
  if (!session) throw io::IOException("Failed to connect to SDP server");

  uint32_t attributeRange = 0x0000FFFF;
  SdpList attributes{sdp_list_append(nullptr, &attributeRange)};

  uuid_t uuid{};
  sdp_uuid128_create(&uuid, serviceUuid.data);
  SdpList queries{sdp_list_append(nullptr, &uuid)};

  sdp_list_t* rawResponse = nullptr;

  if (sdp_service_search_attr_req(session.get(), queries.get(), SDP_ATTR_REQ_RANGE, attributes.get(), &rawResponse) != 0) {
    throw io::IOException("SDP service search failed");
  }

  SdpRecordList response{rawResponse};

  for (auto* entry = response.get(); entry; entry = entry->next) {
    sdp_record_t* record = static_cast<sdp_record_t*>(entry->data);
    sdp_list_t* rawProto = nullptr;
    if (sdp_get_access_protos(record, &rawProto) != 0) continue;

    SdpProtocolList protocols{rawProto};
    int port = sdp_get_proto_port(protocols.get(), RFCOMM_UUID);

    if (port > 0 && port <= UINT8_MAX) {
      return static_cast<uint8_t>(port);
    }
  }

  return std::nullopt;
}

}  // namespace clipbird::io::bluetooth
