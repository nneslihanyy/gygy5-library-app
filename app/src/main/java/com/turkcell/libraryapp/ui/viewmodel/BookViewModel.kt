package com.turkcell.libraryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.libraryapp.data.model.Book
import com.turkcell.libraryapp.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    private val repository = BookRepository()

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadBooks()
    }

    // Tüm kitapları yükle
    fun loadBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository
                .getAllBooks()
                .onSuccess { _books.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    // Yeni kitap ekle
    fun addBook(book: Book) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository
                .addBook(book)
                .onSuccess { loadBooks() }
                .onFailure { _error.value = it.message }
        }
    }

    // ÖDEV 2: Kitap güncelle
    fun updateBook(book: Book) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository
                .updateBook(book)
                .onSuccess { loadBooks() }
                .onFailure { _error.value = it.message }
        }
    }

    // ÖDEV 2: Kitap sil
    fun deleteBook(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository
                .deleteBook(id)
                .onSuccess { loadBooks() }
                .onFailure { _error.value = it.message }
        }
    }

    // ÖDEV 2: Kitap ara (başlık veya yazara göre)
    fun searchBooks(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            if (query.isBlank()) {
                loadBooks()
                return@launch
            }
            repository
                .searchBooks(query)
                .onSuccess { _books.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}