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

---

## 🆕 Files mới tạo (6 files)

| File | Nội dung | Tham chiếu PDF |
|------|----------|----------------|
| `ui/components/AuthTextField.kt` | Component text field tái sử dụng cho Login/Register (label, leadingIcon, password toggle, isError) | §4.1.3 |
| `ui/components/SocialLoginButton.kt` | Nút đăng nhập Facebook/Google | §4.1.3 |
| `viewmodel/AuthViewModel.kt` | `sealed class AuthState` (Idle/Loading/LoginSuccess/Error) + `StateFlow<AuthState>` + `login(LoginRequest)` | §3.1.4 + §5.1.1 |
| `ui/state/UiState.kt` | `sealed class UiState<out T>` (Loading / Success / Error) | §3.1.4 Sealed Class + §3.3.2 |
| `data/model/Category.kt` | `data class Category` + `companion object` với list 12 danh mục | §2.3.2 Data Class |
| `ui/theme/Theme.kt` | `BookStoreTheme {}` bọc `MaterialTheme` | §4.1.3 Theming |
| `viewmodel/CategoryViewModel.kt` | `@HiltViewModel` + `StateFlow<UiState<List<Book>>>` + Coroutines | §3.3.2 + §3.3.4 + §Ch.6 |
| `ui/screens/CategoryDetailScreen.kt` | Figma 5: `AnimatedVisibility`, `LazyVerticalGrid`, `when(uiState)`, `BookCard`, `@Preview` | §4.1.4 + §3.3.2 |

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
| `OrderHistoryScreen.kt` | Xóa `toVndOH()` → dùng `toVnd()`; `Divider` → `HorizontalDivider`; fix deprecated `ArrowBack` |
| `CategoryDetailScreen.kt` | `BookCard` dùng `Column(fillMaxHeight)` + `weight(1f)` + `SpaceBetween` để đồng đều chiều cao |
| 9 screen files | Xóa private color constant, dùng `AppColors.PrimaryBlue` / `AppColors.PriceColor` / `AppColors.StarYellow` |
| `LoginScreen.kt` | **Merge với nhánh nghia**: thay toàn bộ UI bằng `LoginScreenContent` của Nghĩa (email validation, `AuthTextField`, `SocialLoginButton`, `CenterAlignedTopAppBar`); giữ `LoginScreen` wrapper với `NavController` + `accountViewModel` của mình; dùng `AuthViewModel`/`AuthState` thay `LoginViewModel` |
| `ui/theme/Color.kt` | Thêm top-level color vals: `PrimaryBlue`, `GrayText`, `FacebookBlue`, `GoogleButtonRed` (để `import com.example.bookstore.ui.theme.*` trong module login của Nghĩa hoạt động) |
| `UserRequest.kt` | Thêm `province: String` và `district: String` (gửi lên khi save profile) |
| `AccountViewModel.kt` | Thêm `editProvince`, `editDistrict`; cập nhật `loadProfile()`/`saveProfile()`; thêm `applyShippingInfo()` |
| `ProfileScreen.kt` | Thêm 2 trường `Tỉnh/Thành phố`, `Quận/Huyện`; đổi "Địa chỉ" → "Địa chỉ chi tiết"; nhận `viewModel` từ ngoài |
| `MainScreen.kt` | Hoist `AccountViewModel` tại `MainScreen`; truyền vào cả `ProfileScreen`, `CheckoutScreen`, `AccountScreen`, `LoginScreen` |

---

## ⚠️ Lưu ý kỹ thuật — AppBottomNavigation

### Vấn đề: Tab highlight khi ở màn hình con

Mặc định `selected = currentRoute == item.route` chỉ khớp **chính xác** route.  
Khi navigate sang màn hình con (sub-screen), route thay đổi → tab bị **bỏ highlight**, user mất orientation.

**Ví dụ:** User đang ở tab Danh mục → bấm vào 1 danh mục → route đổi thành `category_detail/3`  
→ Không tab nào được highlight ❌

**Fix hiện tại** (đã áp dụng cho Category):
```kotlin
selected = currentRoute == item.route ||
           (item.route == BottomNavItem.Category.route &&
            currentRoute?.startsWith("category_detail/") == true)
```

### Hướng giải quyết khi mở rộng

> Bất kỳ khi nào thêm **sub-screen mới** cho một tab, cần bổ sung điều kiện tương ứng:

| Tab | Sub-screen tương lai | Điều kiện cần thêm |
|-----|---------------------|-------------------|
| Home | `product_detail/{id}` | `currentRoute?.startsWith("product_detail/") == true` |
| Cart | `order_confirm` | `currentRoute == "order_confirm"` |
| Account | `edit_profile`, `change_password`, `order_history`, `settings` | `currentRoute in listOf("edit_profile", "change_password", ...)` |

**Refactor gợi ý** khi số lượng sub-screen nhiều lên:
```kotlin
// Định nghĩa map sub-routes trong BottomNavItem thay vì hardcode trong AppBottomNavigation
sealed class BottomNavItem(val route: String, val subRoutes: List<String> = emptyList(), ...) {
    object Category : BottomNavItem("category", subRoutes = listOf("category_detail"))
    object Account  : BottomNavItem("account",  subRoutes = listOf("profile", "order_history", "settings", "change_password"))
}

// Sau đó trong AppBottomNavigation:
selected = currentRoute == item.route ||
           item.subRoutes.any { currentRoute?.startsWith(it) == true }
```

---

## 🔬 Mapping kỹ thuật → Module

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

---

## 📱 Màn hình

| Route | Màn hình | Mô tả |
|-------|----------|-------|
| `home` | HomeScreen | Trang chủ |
| `category` | CategoryScreen | Lưới 12 danh mục (Figma 4) |
| `category_detail/{index}` | CategoryDetailScreen | Danh sách sách theo thể loại (Figma 5) |
| `cart` | CartScreen | Giỏ hàng |
| `checkout` | CheckoutScreen | Thanh toán (7 form fields + phương thức thanh toán) |
| `account` | AccountScreen | Tài khoản (auth guard) |
| `login/{returnRoute}` | LoginScreen | Đăng nhập |
| `register` | RegisterScreen | Đăng ký |
| `profile` | ProfileScreen | Thông tin tài khoản — 6 trường (fullName, email, phone, province, district, detailedAddress); liên thông với CheckoutScreen |
| `order_history` | OrderHistoryScreen | Lịch sử đơn hàng |
| `change_password` | ChangePasswordScreen | Đổi mật khẩu |
| `contact` | ContactScreen | Liên hệ — form gửi tin nhắn, thông tin liên hệ, social links, map, hệ thống cửa hàng |

---

## ⚙️ Cấu hình môi trường (RetrofitClient.kt)

```kotlin
// true  = chạy backend local (IntelliJ)
// false = chạy backend Railway (production)
private const val USE_LOCAL = true

private const val LOCAL_URL      = "http://192.168.1.5:8081/"
private const val PRODUCTION_URL = "https://bookstore-backend-production-4b7e.up.railway.app/"
```

> ⚠️ Khi dùng **điện thoại thật**, đảm bảo điện thoại và máy tính **cùng WiFi** và đổi IP tương ứng.  
> Khi dùng **Android Emulator**, đổi IP thành `10.0.2.2` (alias của `localhost` máy tính).

---

## 🚀 Hướng dẫn chạy

1. Clone dự án và mở bằng **Android Studio**.
2. Đảm bảo backend đang chạy (IntelliJ hoặc Railway).
3. Chỉnh `USE_LOCAL` trong `RetrofitClient.kt` theo môi trường.
4. Chạy app trên Emulator hoặc điện thoại thật (minSdk 26 / Android 8.0+).

---

## 📋 Yêu cầu hệ thống

- **minSdk**: 26 (Android 8.0)
- **targetSdk / compileSdk**: 36
- **JVM Target**: 11
- **Android Studio**: Hedgehog trở lên

