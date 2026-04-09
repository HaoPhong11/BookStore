package com.example.bookstore.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.bookstore.data.model.Book
import com.example.bookstore.data.model.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {

    val cartItems = mutableStateListOf<CartItem>()

    val shippingFee: Double = 20_000.0

    var discountCode by mutableStateOf("")
    var discountAmount by mutableStateOf(0.0)
        private set
    var discountError by mutableStateOf<String?>(null)
        private set

    val subtotal: Double get() = cartItems.sumOf { it.book.price * it.quantity }
    val total: Double get() = subtotal + shippingFee - discountAmount

    fun addBook(book: Book, quantity: Int = 1) {
        val index = cartItems.indexOfFirst { it.book.id == book.id }
        if (index >= 0) {
            cartItems[index] = cartItems[index].copy(quantity = cartItems[index].quantity + quantity)
        } else {
            cartItems.add(CartItem(book, quantity))
        }
    }

    fun increaseQuantity(bookId: String) {
        val index = cartItems.indexOfFirst { it.book.id == bookId }
        if (index >= 0) {
            cartItems[index] = cartItems[index].copy(quantity = cartItems[index].quantity + 1)
        }
    }

    fun decreaseQuantity(bookId: String) {
        val index = cartItems.indexOfFirst { it.book.id == bookId }
        if (index >= 0) {
            val newQty = cartItems[index].quantity - 1
            if (newQty <= 0) cartItems.removeAt(index)
            else cartItems[index] = cartItems[index].copy(quantity = newQty)
        }
    }

    fun removeItem(bookId: String) {
        cartItems.removeAll { it.book.id == bookId }
    }

    fun applyDiscount() {
        discountError = null
        discountAmount = when (discountCode.trim().uppercase()) {
            "SALE10"   -> subtotal * 0.10
            "GIAM17"   -> 17_000.0
            "FREESHIP" -> shippingFee
            else -> {
                discountError = "Mã giảm giá không hợp lệ"
                0.0
            }
        }
    }

    fun clearCart() {
        cartItems.clear()
        discountCode = ""
        discountAmount = 0.0
        discountError = null
    }
}

