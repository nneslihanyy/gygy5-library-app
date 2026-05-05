package com.turkcell.libraryapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.libraryapp.data.model.Book
import com.turkcell.libraryapp.ui.viewmodel.AuthViewModel
import com.turkcell.libraryapp.ui.viewmodel.BookViewModel
import com.turkcell.libraryapp.ui.viewmodel.BorrowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    bookViewModel: BookViewModel,
    borrowViewModel: BorrowViewModel,
    onLogout: () -> Unit = {},
    onNavigateToMyBorrows: () -> Unit = {}
) {
    val profileState by authViewModel.profile.collectAsState()
    val books by bookViewModel.books.collectAsState()
    val isLoading by bookViewModel.isLoading.collectAsState()
    val error by bookViewModel.error.collectAsState()
    val borrowSuccess by borrowViewModel.borrowSuccess.collectAsState()
    val borrowError by borrowViewModel.error.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingBook by remember { mutableStateOf<Book?>(null) }
    var borrowingBook by remember { mutableStateOf<Book?>(null) }

    LaunchedEffect(borrowSuccess) {
        if (borrowSuccess) {
            bookViewModel.loadBooks()
            borrowViewModel.resetBorrowSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Kütüphane", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToMyBorrows) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Kiralamalarım")
                    }
                    IconButton(onClick = { authViewModel.signOut(); onLogout() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Çıkış Yap")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Kitap Ekle")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it; bookViewModel.searchBooks(it) },
                label = { Text("Kitap veya yazar ara...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ara") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )

            profileState?.let { profile ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hoş geldin, ${profile.fullName}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = onNavigateToMyBorrows) { Text("Kiralamalarım", fontSize = 13.sp) }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            borrowError?.let { errMsg ->
                Text("Ödünç alma hatası: $errMsg", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp))
            }
            error?.let { errMsg ->
                Text(errMsg, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp))
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (books.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Kitap bulunamadı.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn {
                    items(books) { book ->
                        BookCard(
                            book = book,
                            onDeleteClick = { bookId -> bookViewModel.deleteBook(bookId) },
                            onEditClick = { selectedBook -> editingBook = selectedBook },
                            onBorrowClick = { selectedBook -> borrowingBook = selectedBook }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        BookFormDialog(book = null, onDismiss = { showAddDialog = false }, onSave = { newBook -> bookViewModel.addBook(newBook); showAddDialog = false })
    }
    editingBook?.let { book ->
        BookFormDialog(book = book, onDismiss = { editingBook = null }, onSave = { updatedBook -> bookViewModel.updateBook(updatedBook); editingBook = null })
    }
    borrowingBook?.let { book ->
        BorrowDialog(book = book, onDismiss = { borrowingBook = null }, onConfirm = { days ->
            profileState?.let { profile -> borrowViewModel.borrowBook(studentId = profile.userId, bookId = book.id, days = days) }
            borrowingBook = null
        })
    }
}