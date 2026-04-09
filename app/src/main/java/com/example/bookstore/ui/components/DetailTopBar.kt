package com.example.bookstore.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onCartClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCategoryClick: () -> Unit,
    onSearchSubmit: (String) -> Unit)
{
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    var expanded by remember { mutableStateOf(false) }
    if (isSearchActive) {
        BackHandler { isSearchActive = false }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF3B5998))
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút Back này để tắt chế độ Search
            IconButton(onClick = { isSearchActive = false }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Close Search", tint = Color.White)
            }

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Tìm kiếm sách, tác giả...", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchText.isNotBlank()) {
                            onSearchSubmit(searchText)
                            isSearchActive = false
                        }
                    }
                ),
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                    }
                },
                shape = RoundedCornerShape(50),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF5C78B7),
                    unfocusedContainerColor = Color(0xFF5C78B7),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }

        // Tự động bật bàn phím ngay khi thanh search hiện ra
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

    }else {
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            actions = {
                IconButton(onClick = { isSearchActive = true }) { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White) }
                IconButton(onClick =  onHomeClick ) { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White) }
                IconButton(onClick =  onCartClick ) { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White) }
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }, // Bấm ra ngoài thì đóng menu
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tài khoản", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Outlined.PersonOutline, contentDescription = null, tint = Color.Black) },
                            onClick = {
                                expanded = false // Đóng menu trước
                                onAccountClick() // Gọi lệnh chuyển trang sau
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Danh mục", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Outlined.GridView, contentDescription = null, tint = Color.Black) },
                            onClick = {
                                expanded = false
                                onCategoryClick()
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF3B5998)
            )
        )
    }

}