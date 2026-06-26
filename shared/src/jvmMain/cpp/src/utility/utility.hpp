#include <type_traits>

namespace clipbird::utility {
template <typename E>
constexpr auto toUnderlying(E e) noexcept {
  return static_cast<std::underlying_type_t<E>>(e);
}
}
