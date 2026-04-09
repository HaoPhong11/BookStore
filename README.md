# 📚 BookStore – Ứng dụng mua sách Android

> Ứng dụng Android viết bằng **Kotlin + Jetpack Compose**, áp dụng các kỹ thuật lập trình hiện đại theo tài liệu PDF môn học.

---

## 🛠️ Công nghệ & Thư viện

| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| Kotlin | 2.x | Ngôn ngữ lập trình chính |
| Jetpack Compose | BOM 2024.02.00 | UI hiện đại declarative |
| Material 3 | - | Design system |
| Navigation Compose | 2.7.7 | Điều hướng màn hình |
| Hilt (Dagger) | 2.51.1 | Dependency Injection |
| Retrofit 2 | 2.9.0 | Gọi REST API |
| OkHttp Logging | 4.12.0 | Log request/response |
| Gson Converter | 2.9.0 | Parse JSON |
| Coroutines | 1.7.3 | Bất đồng bộ |
| Lifecycle ViewModel | 2.7.0 | MVVM architecture |
| Coil | 2.6.0 | Load ảnh từ mạng |
| DataStore Preferences | 1.0.0 | Lưu trữ cục bộ |

---

## 🗂️ Cấu trúc dự án

```
app/src/main/java/com/example/bookstore/
├── BookstoreApplication.kt          # @HiltAndroidApp
├── MainActivity.kt                  # Entry point, bọc BookStoreTheme
├── data/
│   ├── api/
│   │   ├── RetrofitClient.kt        # Singleton Retrofit (local / production)
│   │   └── GoogleBooksApiService.kt # Retrofit interface
│   ├── dto/                         # Data Transfer Objects (JSON mapping)
│   ├── local/                       # DataStore / local storage
│   ├── model/
│   │   ├── Book.kt
│   │   ├── CartItem.kt
│   │   ├── Category.kt              # data class + companion object (12 danh mục)
│   │   ├── Order.kt
│   │   └── User.kt
│   └── repo/
│       ├── BookRepository.kt        # getBooks() + getBooksResult()
│       ├── OrderRepository.kt
│       └── UserRepository.kt
├── di/                              # Hilt modules
├── ui/
│   ├── components/                  # Composable tái sử dụng
│   ├── screens/
│   │   ├── MainScreen.kt            # NavHost + BottomNavigation
│   │   ├── HomeScreen.kt
│   │   ├── CategoryScreen.kt        # Figma 4: LazyVerticalGrid, CategoryCard
│   │   ├── CategoryDetailScreen.kt  # Figma 5: AnimatedVisibility, BookCard
│   │   ├── CartScreen.kt
│   │   ├── CheckoutScreen.kt
│   │   ├── AccountScreen.kt
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── ProfileScreen.kt
│   │   ├── OrderHistoryScreen.kt
│   │   ├── ChangePasswordScreen.kt
│   │   └── SettingsScreen.kt
│   ├── state/
│   │   └── UiState.kt               # sealed class UiState<out T>
│   └── theme/
│       ├── Color.kt                 # object AppColors
│       └── Theme.kt                 # BookStoreTheme {}
├── utils/
│   └── Extensions.kt               # toVnd(), orDefault()
└── viewmodel/
    ├── AccountViewModel.kt
    ├── CartViewModel.kt
    ├── CategoryViewModel.kt         # @HiltViewModel + StateFlow + Coroutines
    ├── CheckoutViewModel.kt
    ├── HomeViewModel.kt
    └── LoginViewModel.kt
    └── AuthViewModel.kt             # Quản lý đăng nhập/đăng ký tập trung
```

---

## ✅ Bugs đã fix

| File | Vấn đề | Trạng thái |
|------|--------|------------|
| `UserRepository.kt` | Duplicate class (compile error) | ✅ Fixed |
| `Color.kt` | `class Color` shadow type Compose | ✅ Đổi thành `object AppColors` |
| `CategoryDetailScreen.kt` | Giá hiển thị "Liên hệ" vì Google Books API không trả về price | ✅ Fixed — `displayPrice()` Extension Function |
| `CategoryDetailScreen.kt` | Nút Sắp xếp không có UI (`/* TODO */`) | ✅ Fixed — `DropdownMenu` với 4 tuỳ chọn |
| `CategoryDetailScreen.kt` | Filter panel chỉ lặp lại sort chips, không lọc theo giá | ✅ Fixed — chips khoảng giá hoạt động thực sự |
| `MainScreen.kt` | Bottom navbar không hiện ở màn hình Chi tiết danh mục | ✅ Fixed — `showBottomBar` kiểm tra `category_detail/` |
| `AppBottomNavigation.kt` | Tab "Danh mục" không highlight khi đang ở `category_detail` | ✅ Fixed — `selected` bổ sung điều kiện `startsWith` |
| `CategoryDetailScreen.kt` | Chiều cao `BookCard` không đồng đều giữa 2 cột; `fillMaxHeight()` trong `LazyVerticalGrid` collapse text về 0; `height(285.dp)` thiếu 15dp khiến nút "Thêm vào giỏ" bị cắt | ✅ Fixed — `Card(height=310.dp)` + `fillMaxSize()` + `weight(1f)` + `SpaceBetween`; thêm số lượt đánh giá `(n)` theo Figma |
| `OrderHistoryScreen.kt` | Hàm `toVndOH()` trùng với `Extensions.kt` (code duplication) | ✅ Fixed — dùng `toVnd()` chung |
| `OrderHistoryScreen.kt` | `Divider` deprecated | ✅ Fixed → `HorizontalDivider` |
| 9 screen files | Mỗi file tự định nghĩa `private val PrimaryBlueXxx = Color(0xFF3E5EA5)` | ✅ Fixed — tập trung vào `AppColors.PrimaryBlue` |
| `CategoryDetailScreen.kt` + `CartScreen.kt` | Giỏ hàng hiển thị "171đ" (giá USD thô từ API) và "0đ" (API không có giá) | ✅ Fixed — `displayPrice()` chuyển từ `private` trong CategoryDetail sang `public` trong `Extensions.kt`; `addBook(book.copy(price = book.displayPrice()))` normalize giá VND trước khi lưu vào cart |
| `JwtResponse.kt` | `userId` luôn null sau login vì backend trả `"id"` nhưng DTO không có `@SerializedName("id")` → `loadProfile()` không bao giờ chạy → AccountScreen & CheckoutScreen trống | ✅ Fixed — `@SerializedName("id") val userId` |
| `ProfileScreen.kt` | Thiếu trường `Tỉnh/Thành phố` và `Quận/Huyện` so với CheckoutScreen | ✅ Fixed — thêm 2 `ProfileField` cho `editProvince`/`editDistrict`; đổi label "Địa chỉ" → "Địa chỉ chi tiết" |
| `CheckoutScreen.kt` | Form không pre-fill từ profile, user phải nhập lại toàn bộ mỗi lần | ✅ Fixed — `LaunchedEffect(profile)` pre-fill khi `userProfile` load xong từ API |
| `CheckoutScreen.kt` + `ProfileScreen.kt` | Dữ liệu không liên thông nhau | ✅ Fixed — `AccountViewModel` được hoist tại `MainScreen` và chia sẻ cho cả 2 screen |
| `CheckoutScreen.kt` | Sau đặt hàng thành công, thông tin giao hàng không được lưu lại | ✅ Fixed — `applyShippingInfo()` ghi ngược về profile + lưu lên backend |
| `AccountScreen.kt` | `Divider` deprecated, `Icons.Default.ArrowBack/Logout/HelpOutline` deprecated, unused import `border` | ✅ Fixed — `HorizontalDivider`, `AutoMirrored` icons |
| `LoginScreen.kt` / `AccountViewModel.kt` | Sau đăng nhập, `AccountViewModel` (Activity-scoped) không tự reload profile → ProfileScreen & CheckoutScreen trống | ✅ Fixed — `LoginScreen` nhận `accountViewModel`, gọi `loadProfile()` sau `loginSuccess`; `AccountScreen` gọi `loadProfile()` trong `LaunchedEffect(Unit)` |
| `ProfileScreen.kt` | Dialog "Lưu thành công" không hiển thị — `LaunchedEffect(Unit)` bên trong `if (updateSuccess)` reset flag về `false` ngay lập tức | ✅ Fixed — xóa `LaunchedEffect` xấu; chỉ reset `updateSuccess` trong confirmButton |
| `CheckoutScreen.kt` | Form không pre-fill khi đi Cart → Checkout (không qua AccountScreen, profile chưa load) | ✅ Fixed — thêm `LaunchedEffect(Unit) { accountViewModel.loadProfile() }`; dùng `isPrefilled` flag để chỉ fill 1 lần khi profile load xong |
| `LoginScreen.kt` / `RegisterScreen.kt` | Nút Facebook/Google chỉ hiện thông báo "đang phát triển" | ✅ Fixed — Tích hợp chức năng đăng nhập/đăng ký giả lập (mock) qua `AuthViewModel` |
| `RegisterScreen.kt` | Không xem được điều khoản dịch vụ và chính sách bảo mật | ✅ Fixed — Thêm `AlertDialog` hiển thị nội dung chi tiết khi nhấn vào các liên kết tương ứng |
| `OrderHistoryScreen.kt` | Crash `NullPointerException` khi mở "Đơn hàng của tôi" — backend trả field `orderItems` nhưng DTO expect `items` → Gson map thành null → `sumOf { it.quantity }` crash | ✅ Fixed — thêm `@JsonProperty("items")` + `FetchType.EAGER` ở backend; default `emptyList()` ở frontend |
| `Order.java` (backend) | `createdAt` serialize là số timestamp (long) thay vì ISO string → frontend parse sai ngày | ✅ Fixed — `@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")` |
| `CheckoutScreen.kt` | Sau đặt hàng MOMO không mở app thanh toán, chỉ hiện dialog rồi về home | ✅ Fixed — tích hợp luồng MoMo: tạo đơn → mở deeplink/browser → về OrderHistory |
| `MainScreen.kt` + `AppBottomNavigation.kt` | Blue extra space xuất hiện phía trên TopAppBar do double-consume insets | ✅ Fixed — `contentWindowInsets = WindowInsets(0)` + `calculateBottomPadding()` |
| `BookDetailScreen.kt` | Nút "Thêm vào giỏ" và "Mua ngay" không làm gì (`/* TODO */`) | ✅ Fixed — kết nối `cartViewModel`, thêm `quantity` state, `SnackbarHostState` |
| `AppBottomNavigation.kt` | Search → BookDetail → Cart → tap Category bị stuck trên Cart | ✅ Fixed — Nested Navigation Graphs: mỗi tab có back stack riêng |
| `AppBottomNavigation.kt` | Home → BookDetail → Cart → tap Home bị stuck trên Cart | ✅ Fixed — Nested Graphs + `saveState`/`restoreState` hoạt động đúng |
| `AppBottomNavigation.kt` | Tab Category/Account không highlight khi ở màn hình con | ✅ Fixed — `NavDestination.hierarchy` tự động detect graph |
| `SearchTopBar.kt` + `DetailTopBar.kt` | Placeholder text "Tìm kiếm sách, tác giả" bị cắt ở đáy | ✅ Fixed — `statusBarsPadding()` trước `padding()`, xóa `height(50.dp)` cứng |

---

## 🆕 Files mới tạo (6 files)

| File | Nội dung | Tham chiếu PDF |
|------|----------|----------------|
| `ui/components/AuthTextField.kt` | Component text field tái sử dụng cho Login/Register (label, leadingIcon, password toggle, isError) | §4.1.3 |
| `ui/components/SocialLoginButton.kt` | Nút đăng nhập Facebook/Google với hỗ trợ `enabled` state | §4.1.3 |
| `viewmodel/AuthViewModel.kt` | `sealed class AuthState` (Idle/Loading/LoginSuccess/Error) + `StateFlow<AuthState>` + `loginWithSocial` (mock) | §3.1.4 + §5.1.1 |
| `ui/state/UiState.kt` | `sealed class UiState<out T>` (Loading / Success / Error) | §3.1.4 Sealed Class + §3.3.2 |
| `data/model/Category.kt` | `data class Category` + `companion object` với list 12 danh mục | §2.3.2 Data Class |
| `ui/theme/Theme.kt` | `BookStoreTheme {}` bọc `MaterialTheme` | §4.1.3 Theming |
| `viewmodel/CategoryViewModel.kt` | `@HiltViewModel` + `StateFlow<UiState<List<Book>>>` + Coroutines | §3.3.2 + §3.3.4 + §Ch.6 |
| `ui/screens/CategoryDetailScreen.kt` | Figma 5: `AnimatedVisibility`, `LazyVerticalGrid`, `when(uiState)`, `BookCard`, `@Preview` | §4.1.4 + §3.3.2 |
| `data/dto/response/PaymentUrlResponse.kt` | DTO nhận từ backend: `{ orderId, payUrl, deeplink, paymentMethod }` | — |
| `service/MoMoService.java` (backend) | Gọi MoMo sandbox API, ký HMAC-SHA256, trả về payUrl + deeplink | — |
| `controller/PaymentController.java` (backend) | `GET /api/payment/momo-return` cập nhật order status → redirect deep link; `POST /api/payment/momo-ipn` | — |
| `dto/PaymentUrlResponse.java` (backend) | DTO response cho tạo đơn hàng | — |

---

## ✏️ Files đã sửa (8 files)

| File | Thay đổi |
|------|----------|
| `MainActivity.kt` | Bọc `BookStoreTheme {}` |
| `BookRepository.kt` | Thêm `getBooksResult()` trả về `Result<T>` |
| `CategoryScreen.kt` | Implement đầy đủ Figma 4: `LazyVerticalGrid`, `CategoryCard`, `getCategoryIcon`, `@Preview` |
| `MainScreen.kt` | Thêm route `category_detail/{index}` + `showBottomBar` hiện navbar ở `category_detail/{index}` |
| `CartScreen.kt` | Import `toVnd()` từ utils, `@Preview CartItemCard`, fix deprecated icons + `HorizontalDivider` |
| `CheckoutViewModel.kt` | Xóa form fields, cập nhật `placeOrder()` nhận params |
| `CheckoutScreen.kt` | 7 form fields dùng `rememberSaveable`, icon payment methods, import `toVnd()`; **thêm `accountViewModel` param, `LaunchedEffect` pre-fill từ profile, ghi ngược sau đặt hàng** |
| `AccountScreen.kt` | `AnimatedVisibility` + `fadeIn` + `slideInVertically` cho order status |
| `CategoryDetailScreen.kt` | `displayPrice()` ext fn; Sort `DropdownMenu` 4 option; Filter chips khoảng giá; `BookCard` luôn hiện giá+badge; Snackbar khi thêm giỏ; dùng `AppColors`; **fix card height** — `Card(height=310.dp)` + `fillMaxSize()` + `weight(1f)` + số lượt đánh giá |
| `AppBottomNavigation.kt` | Tab "Danh mục" highlight khi đang ở `category_detail/{index}` |
| `AppBottomNavigation.kt` | **Refactor sang Nested Graphs**: `selected` dùng `hierarchy` (tự động); navigate đến `graphRoute` với `saveState`/`restoreState`; không còn check route thủ công |
| `BottomNavItem.kt` | Thêm `graphRoute` property để tách route màn hình và route graph |
| `MainScreen.kt` | **Refactor sang Nested Navigation Graphs**: chia 4 sub-graph (`home_graph`, `category_graph`, `cart_graph`, `account_graph`); `book_detail` nằm trong `home_graph`; `login`/`register` là shared screens ngoài graph |
| `OrderHistoryScreen.kt` | Xóa `toVndOH()` → dùng `toVnd()`; `Divider` → `HorizontalDivider`; fix deprecated `ArrowBack` |
| `CategoryDetailScreen.kt` | `BookCard` dùng `Column(fillMaxHeight)` + `weight(1f)` + `SpaceBetween` để đồng đều chiều cao |
| 9 screen files | Xóa private color constant, dùng `AppColors.PrimaryBlue` / `AppColors.PriceColor` / `AppColors.StarYellow` |
| `LoginScreen.kt` | Tích hợp chức năng đăng nhập giả lập cho Facebook/Google |
| `RegisterScreen.kt` | Thêm hộp thoại (Dialog) hiển thị Điều khoản dịch vụ và Chính sách bảo mật; tích hợp đăng ký giả lập |
| `ui/theme/Color.kt` | Thêm top-level color vals: `PrimaryBlue`, `GrayText`, `FacebookBlue`, `GoogleButtonRed` |
| `UserRequest.kt` | Thêm `province: String` và `district: String` (gửi lên khi save profile) |
| `AccountViewModel.kt` | Thêm `editProvince`, `editDistrict`; cập nhật `loadProfile()`/`saveProfile()`; thêm `applyShippingInfo()` |
| `ProfileScreen.kt` | Thêm 2 trường `Tỉnh/Thành phố`, `Quận/Huyện`; đổi "Địa chỉ" → "Địa chỉ chi tiết"; nhận `viewModel` từ ngoài |
| `MainScreen.kt` | Hoist `AccountViewModel` tại `MainScreen`; truyền vào cả `ProfileScreen`, `CheckoutScreen`, `AccountScreen`, `LoginScreen` |
| `OrderHistoryScreen.kt` | Thêm `initialTab` param; PENDING card clickable → dialog Hủy/Thanh toán MoMo; handle deep link từ MoMo return |
| `CheckoutScreen.kt` | Tích hợp MOMO: sau tạo đơn mở `deeplink`/`payUrl` bằng `Intent`; navigate về `order_history` thay vì dialog |
| `AccountViewModel.kt` | Thêm `cancelOrder()`, `getRepayUrl()`, `repayResult`, `orderActionLoading`, `cancelSuccess` cho MOMO flow |
| `CheckoutViewModel.kt` | Expose `pendingPayUrl`, `pendingDeeplink`, `createdOrderId`; COD → `orderSuccess`, MOMO → URL state |
| `data/api/ApiService.kt` | `createOrder` trả `PaymentUrlResponse`; thêm `cancelOrder (PATCH)`, `repayOrder (GET)` |
| `data/repo/OrderRepository.kt` | `createOrder` trả `Result<PaymentUrlResponse>`; thêm `cancelOrder`, `getRepayUrl` |
| `AndroidManifest.xml` | `launchMode=singleTask`; thêm deep link intent-filter cho scheme `bookstore`, host `payment` |
| `entity/Order.java` (backend) | `@JsonProperty("items")` trên `orderItems`; `FetchType.EAGER`; `@JsonFormat` ISO date |
| `controller/OrderController.java` (backend) | POST trả `PaymentUrlResponse`; thêm `PATCH /{id}/cancel`; `GET /{id}/repay` |
| `security/SecurityConfig.java` (backend) | Thêm `PATCH` vào CORS allowed methods |

---

## 🗺️ Kiến trúc Navigation — Nested Navigation Graphs

Ứng dụng sử dụng **Nested Navigation Graphs** (Jetpack Navigation Compose) — mỗi tab là một sub-graph độc lập với back stack riêng.

```
NavHost(startDestination = "home_graph")
├── navigation(route = "home_graph")         ← HOME TAB
│   ├── composable("home")
│   ├── composable("search_screen")
│   ├── composable("search_results/{query}")
│   └── composable("book_detail/{bookId}")
│
├── navigation(route = "category_graph")     ← CATEGORY TAB
│   ├── composable("category")
│   └── composable("category_detail/{index}")
│
├── navigation(route = "cart_graph")         ← CART TAB
│   ├── composable("cart")
│   └── composable("checkout")
│
├── navigation(route = "account_graph")      ← ACCOUNT TAB
│   ├── composable("account")
│   ├── composable("profile")
│   ├── composable("settings")
│   ├── composable("change_password")
│   ├── composable("order_history")
│   └── composable("contact")
│
├── composable("login/{returnRoute}")        ← SHARED (ngoài mọi graph)
└── composable("register")                  ← SHARED (ngoài mọi graph)
```

### Tại sao dùng Nested Graphs?

| Vấn đề với Flat NavHost | Giải pháp với Nested Graphs |
|---|---|
| `saveState`/`restoreState` bị conflict khi back stack có non-tab routes (`search_screen`, `book_detail`, v.v.) | Mỗi graph có back stack riêng → `saveState`/`restoreState` hoạt động chính xác cho từng tab |
| Phải check thủ công `startsWith("category_detail/")` để highlight tab | `NavDestination.hierarchy` tự động detect tab dựa vào graph chứa màn hình hiện tại |
| `book_detail` và `search_screen` lẫn vào state của tab Home | `home_graph` chứa đúng các màn hình home-context — state lưu/restore rõ ràng |

### `BottomNavItem` — `route` vs `graphRoute`

```kotlin
sealed class BottomNavItem(
    val route: String,      // Route màn hình (dùng cho startDestination của graph)
    val graphRoute: String, // Route của graph (dùng để navigate khi tap tab)
    val title: String,
    val icon: ImageVector
) {
    object Home     : BottomNavItem("home",     "home_graph",     "Trang chủ", Icons.Outlined.Home)
    object Category : BottomNavItem("category", "category_graph", "Danh mục",  Icons.Outlined.GridView)
    object Cart     : BottomNavItem("cart",     "cart_graph",     "Giỏ hàng",  Icons.Outlined.ShoppingCart)
    object Account  : BottomNavItem("account",  "account_graph",  "Tài khoản", Icons.Outlined.Person)
}
```

### `AppBottomNavigation` — Hai cơ chế chính

**1. `selected` — dùng `hierarchy` thay vì so sánh route trực tiếp:**
```kotlin
// Trước (flat): chỉ highlight khi route khớp chính xác
selected = currentRoute == item.route

// Sau (nested graphs): highlight khi đang ở BẤT KỲ màn hình nào trong graph đó
selected = currentDestination?.hierarchy?.any { it.route == item.graphRoute } == true
```

**Lợi ích**: Tự động highlight đúng tab khi ở `category_detail`, `profile`, `settings`, v.v. — không cần `startsWith()` thủ công.

**2. `onClick` — `saveState`/`restoreState` hoạt động đúng:**
```kotlin
navController.navigate(item.graphRoute) {
    popUpTo(navController.graph.findStartDestination().id) {
        saveState = true  // Lưu back stack của tab đang rời
    }
    launchSingleTop = true
    restoreState    = true  // Restore back stack khi quay lại tab này
}
```

**Lợi ích**: Mỗi tab nhớ vị trí cuối cùng — ví dụ Category tab giữ nguyên `category_detail` khi user switch sang tab khác rồi quay lại.

### Luồng navigation giải quyết được

| Flow | Trước | Sau |
|---|---|---|
| Search → BookDetail → Cart → tap Category | ❌ Stuck trên Cart | ✅ Vào Category bình thường |
| Home → BookDetail → Cart → tap Home | ❌ Stuck trên Cart | ✅ Về Home bình thường |
| Ở `category_detail` → tab Category highlight? | ❌ Không highlight | ✅ Tự động highlight |
| Ở `profile`/`settings` → tab Account highlight? | ❌ Không highlight | ✅ Tự động highlight |
| Switch qua lại giữa Category ↔ Account | ❌ Mất trạng thái | ✅ Giữ nguyên scroll/state |

## ✅ Các cải tiến & Fix lỗi quan trọng

| Chức năng | Vấn đề / Yêu cầu | Trạng thái thực hiện |
|-----------|------------------|----------------------|
| **Social Auth** | Nút Facebook/Google chưa hoạt động | ✅ **Đã tích hợp giả lập (Mock)**: Xử lý loading 1.5s, tự động đăng nhập và lưu Token giả lập vào hệ thống để demo luồng người dùng hoàn chỉnh. |
| **Register UX** | Không xem được Điều khoản & Chính sách | ✅ **Fixed**: Thêm `AlertDialog` hiển thị nội dung chi tiết khi người dùng nhấn vào các liên kết màu xanh trong màn hình đăng ký. |
| **User Profile** | Dữ liệu không đồng bộ giữa các màn hình | ✅ **Fixed**: Hoist `AccountViewModel` tại `MainScreen` để chia sẻ dữ liệu profile giữa Checkout, Account và Profile screen. |
| **Checkout UI** | Phải nhập lại thông tin giao hàng nhiều lần | ✅ **Fixed**: Tự động lấy thông tin từ Profile để điền vào Form thanh toán; cập nhật ngược lại Profile sau khi đặt hàng thành công. |
| **Product UI** | Chiều cao Card sách không đều | ✅ **Fixed**: Chuẩn hóa chiều cao `BookCard` (310.dp) và dùng `weight(1f)` để căn chỉnh các thành phần Text/Button đồng nhất. |
| **Price Fix** | Lỗi hiển thị giá từ Google Books | ✅ **Fixed**: Chuyển đổi giá USD từ API sang VND hợp lý, xử lý các trường hợp sách không có giá (hiển thị "Liên hệ"). |

---

## 🔬 Mapping kỹ thuật → Module (Theo PDF học phần)

| Kỹ thuật PDF | Module | File |
|--------------|--------|------|
| `sealed class UiState<out T>` (§3.3.2) | Danh mục | `UiState.kt` + `CategoryViewModel` |
| `StateFlow` + `collectAsStateWithLifecycle` (§5.1.1) | Danh mục | `CategoryDetailScreen` |
| `AnimatedVisibility` (§4.1.4) | Danh mục + Tài khoản | `CategoryDetailScreen`, `AccountScreen` |
| Extension Function `toVnd()` (§3.1.2) | Giỏ hàng + Thanh toán + Lịch sử | `utils/Extensions.kt` |
| Extension Function `displayPrice()` (§3.1.2) | Danh mục | `CategoryDetailScreen` |
| Null Safety `?.`, `?:` (§3.1.1) | Danh mục | `BookRepository`, `CategoryViewModel` |
| `rememberSaveable` (§4.1.3) | Thanh toán | `CheckoutScreen` |
| `LaunchedEffect` (§4.1.3) | Thanh toán + Profile | `CheckoutScreen` — pre-fill form từ profile |
| ViewModel hoisting (§4.1.3) | Thanh toán + Profile | `MainScreen` hoist `AccountViewModel`, truyền xuống cả 2 screen |
| `@Preview` (§4.1.3) | Danh mục + Giỏ hàng | `CategoryScreen`, `CategoryDetailScreen`, `CartScreen` |
| `DropdownMenu` (§4.1.3 Material Components) | Danh mục | `CategoryDetailScreen` |
| `FilterChip` + `horizontalScroll` (§4.1.4) | Danh mục | `CategoryDetailScreen` |
| Coroutines `viewModelScope` (§3.3.4) | Danh mục | `CategoryViewModel` |
| Hilt `@HiltViewModel` (§Ch.6) | Danh mục | `CategoryViewModel` |
| `MaterialTheme` + Theming + `AppColors` (§4.1.3) | App-wide | `Theme.kt`, `Color.kt`, 9 screen files |
| `StateFlow` + `collectAsState` (§5.1.1) | Xác thực | `AuthViewModel` + `LoginScreen` |
| `AnimatedVisibility` (§4.1.4) | Tài khoản | `AccountScreen.kt` |
| `rememberSaveable` (§4.1.3) | Thanh toán | `CheckoutScreen.kt` |
| ViewModel hoisting (§4.1.3) | Toàn ứng dụng | `MainScreen.kt` quản lý `AccountViewModel` |
| `@Preview` (§4.1.3) | UI Components | `SocialLoginButton.kt`, `CartScreen.kt` |
| `HorizontalDivider` (M3) | Bố cục | Thay thế `Divider` cũ đã deprecated |
| **Nested Navigation Graphs** | Navigation | `MainScreen.kt` — 4 sub-graphs (home/category/cart/account) |
| **`NavDestination.hierarchy`** | Navigation | `AppBottomNavigation.kt` — auto-highlight tab theo graph |
| **`saveState` + `restoreState`** | Navigation | `AppBottomNavigation.kt` — mỗi tab giữ state riêng khi switch |
| **`BottomNavItem.graphRoute`** | Navigation | `BottomNavItem.kt` — tách route màn hình và route graph |
---

## 📱 Màn hình & Navigation Graph

### Tab Graphs (có Bottom Navigation)

| Graph | Route | Màn hình | Mô tả |
|-------|-------|----------|-------|
| `home_graph` | `home` | HomeScreen | Trang chủ |
| `home_graph` | `search_screen` | SearchScreen | Tìm kiếm |
| `home_graph` | `search_results/{query}` | BookSearchResultsScreen | Kết quả tìm kiếm |
| `home_graph` | `book_detail/{bookId}` | BookDetailScreen | Chi tiết sách |
| `category_graph` | `category` | CategoryScreen | Lưới 12 danh mục (Figma 4) |
| `category_graph` | `category_detail/{index}` | CategoryDetailScreen | Danh sách sách theo thể loại (Figma 5) |
| `cart_graph` | `cart` | CartScreen | Giỏ hàng |
| `cart_graph` | `checkout` | CheckoutScreen | Thanh toán (7 form fields + phương thức thanh toán) |
| `account_graph` | `account` | AccountScreen | Tài khoản |
| `account_graph` | `profile` | ProfileScreen | Thông tin tài khoản — 6 trường; liên thông với CheckoutScreen |
| `account_graph` | `order_history` | OrderHistoryScreen | Lịch sử đơn hàng (5 tab) |
| `account_graph` | `order_history?success={bool}` | OrderHistoryScreen | Deep link từ MoMo return |
| `account_graph` | `change_password` | ChangePasswordScreen | Đổi mật khẩu |
| `account_graph` | `contact` | ContactScreen | Liên hệ |

### Shared Screens (ngoài mọi graph — không thuộc tab nào)

| Route | Màn hình | Mô tả |
|-------|----------|-------|
| `login/{returnRoute}` | LoginScreen | Đăng nhập |
| `register` | RegisterScreen | Đăng ký |

---

## 💳 Tích hợp Thanh toán MoMo (Sandbox)

### Luồng hoạt động

```
User bấm "Đặt hàng & Thanh toán MoMo"
    ↓
Frontend → POST /api/orders (paymentMethod=MOMO)
    ↓
Backend tạo order (status=PENDING) → gọi MoMo API → nhận payUrl + deeplink
    ↓
Backend trả PaymentUrlResponse { orderId, payUrl, deeplink }
    ↓
Frontend thử mở app MoMo qua deeplink (momo://...)
    └─ Nếu MoMo chưa cài → fallback mở browser (payUrl)
    ↓
User thanh toán trên MoMo
    ↓
MoMo redirect browser → http://{server_ip}:8081/api/payment/momo-return?resultCode=0&...
    ↓
Backend verify → cập nhật order PENDING → PROCESSING
    ↓
Backend redirect → bookstore://payment/result?success=true&orderId=123
    ↓
Android nhận deep link → mở app → OrderHistoryScreen tab "Đang xử lý"
```

### Trường hợp không thanh toán (PENDING)

- Đơn hàng vẫn ở tab **"Chờ xác nhận"**
- Bấm vào card → dialog 2 lựa chọn:
  - **Hủy đơn** → gọi `PATCH /api/orders/{id}/cancel` → status = `CANCELLED`
  - **Thanh toán MoMo** → gọi `GET /api/orders/{id}/repay` → lấy payUrl mới → mở lại MoMo

### Cấu hình MoMo

Credentials sandbox đã được cấu hình trong `bookstore-backend/src/main/resources/application.properties`:

```properties
momo.partner-code=MOMOBKUN20180529
momo.access-key=klm05TvNBzhg7h7j
momo.secret-key=at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa
momo.endpoint=https://test-payment.momo.vn/v2/gateway/api/create

# ⚠ Đổi thành IP máy tính trong mạng WiFi (cùng mạng với điện thoại)
momo.redirect-base-url=http://192.168.1.x:8081
```

> **Lý do dùng `redirectUrl` thay vì `ipnUrl`:**  
> `ipnUrl` yêu cầu server phải có IP public (MoMo server gọi vào). Thay vào đó, dùng `redirectUrl` — browser trên điện thoại (cùng mạng WiFi) tự redirect về server local, server update status và redirect sang deep link. Không cần ngrok.

### Test với MoMo Sandbox

| Thông tin | Giá trị |
|-----------|---------|
| Số điện thoại | `0000000000` |
| OTP | `000000` |
| PIN | `000000` |
| resultCode thành công | `0` |

### Files liên quan

**Backend:**
| File | Vai trò |
|------|---------|
| `service/MoMoService.java` | Gọi MoMo API, ký HMAC-SHA256, trả về `payUrl` + `deeplink` |
| `controller/PaymentController.java` | Nhận redirect từ MoMo browser, update order status, redirect sang deep link |
| `controller/OrderController.java` | POST tạo đơn → `PaymentUrlResponse`; PATCH cancel; GET repay |
| `dto/PaymentUrlResponse.java` | DTO: `{ orderId, payUrl, deeplink, paymentMethod }` |

**Frontend:**
| File | Vai trò |
|------|---------|
| `data/dto/response/PaymentUrlResponse.kt` | DTO nhận từ backend |
| `data/api/ApiService.kt` | Endpoint `createOrder`, `cancelOrder`, `repayOrder` |
| `data/repo/OrderRepository.kt` | `createOrder`, `cancelOrder`, `getRepayUrl` |
| `viewmodel/CheckoutViewModel.kt` | Expose `pendingPayUrl`, `pendingDeeplink` sau khi tạo đơn MOMO |
| `viewmodel/AccountViewModel.kt` | `cancelOrder()`, `getRepayUrl()`, `repayResult` |
| `ui/screens/CheckoutScreen.kt` | COD → dialog; MOMO → mở Intent → navigate order_history |
| `ui/screens/OrderHistoryScreen.kt` | PENDING card clickable, dialog Hủy/Thanh toán lại |
| `AndroidManifest.xml` | Deep link: `bookstore://payment/result` → `MainActivity (singleTask)` |
| `MainScreen.kt` | Route `order_history?success={bool}` với `navDeepLink` |

---



```kotlin
// true  = chạy backend local (IntelliJ)
// false = chạy backend Railway (production)
private const val USE_LOCAL = true

private const val LOCAL_URL      = "http://192.168.1.4:8081/"
private const val PRODUCTION_URL = "https://bookstore-backend-production-4b7e.up.railway.app/"
```

*   **Emulator**: Dùng IP `10.0.2.2`.
*   **Thiết bị thật**: Dùng IP nội bộ của máy tính (ví dụ `192.168.1.x`) và cùng mạng WiFi.

> ⚠️ **IP LAN có thể thay đổi** khi đổi mạng WiFi hoặc khởi động lại router. Khi bị lỗi kết nối, kiểm tra lại IP máy tính bằng lệnh `ipconfig` (Windows) và cập nhật đồng thời **2 chỗ**:
> 1. `LOCAL_URL` trong `RetrofitClient.kt`
> 2. Thẻ `<domain>` tương ứng trong `app/src/main/res/xml/network_security_config.xml`

### network_security_config.xml — Whitelist IP cho HTTP cleartext

Android 9+ chặn HTTP (non-HTTPS) theo mặc định. Mỗi IP local cần được khai báo tường minh:

```xml
<!-- Emulator -->
<domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="false">10.0.2.2</domain>
</domain-config>

<!-- Thiết bị thật — IP LAN hiện tại của máy tính -->
<domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="false">192.168.1.4</domain>
</domain-config>
```

> Nếu gặp lỗi `CLEARTEXT communication to x.x.x.x not permitted by network security policy`, nghĩa là IP máy tính đã thay đổi và chưa được thêm vào file trên.

---

## 🔑 Google Books API Key

App dùng [Google Books API](https://developers.google.com/books) để tải danh sách sách theo thể loại.  
API Key đã được cấu hình trong **`AppModule.kt`** và **`GoogleBooksClient.kt`** thông qua OkHttp interceptor (tự động đính kèm `?key=...` vào mọi request).

> - Google Books API **hoàn toàn miễn phí** (không có gói trả phí).
> - Không có key → quota ~100 req/ngày → **HTTP 429** (Too Many Requests).
> - Có key → quota **10,000 req/ngày** (1,000 req/100s), đủ dùng cho development.

---

## 🚀 Hướng dẫn chạy

1. Clone dự án và mở bằng **Android Studio** (frontend) + **IntelliJ IDEA Community** (backend).
2. Đảm bảo MySQL đang chạy (cổng 3307).
3. Chạy backend từ IntelliJ — server sẽ khởi động tại `http://localhost:8081`.
4. Chỉnh `USE_LOCAL` trong `RetrofitClient.kt` và IP trong `LOCAL_URL` theo môi trường.
5. **Nếu dùng thanh toán MoMo**: Cập nhật `momo.redirect-base-url` trong `application.properties` thành IP LAN của máy tính (kết quả `ipconfig` → IPv4).
6. Chạy app trên Emulator hoặc điện thoại thật (minSdk 26 / Android 8.0+).
7. **Test MoMo sandbox**: SĐT `0000000000` · OTP `000000` · PIN `000000`.

---

## 📋 Yêu cầu hệ thống

- **minSdk**: 26 (Android 8.0)
- **targetSdk / compileSdk**: 36
- **JVM Target**: 11
- **Android Studio**: Hedgehog trở lên
- **Gradle**: 8.x
