package com.turkcell.libraryapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.libraryapp.data.model.Book

// Kitap ekleme ve düzenleme için ortak dialog
@Composable
fun BookFormDialog(
    book: Book? = null,
    onDismiss: () -> Unit,
    onSave: (Book) -> Unit
) {
    // Eğer book null değilse düzenleme modundayız
    val isEditMode = book != null

    var title by remember { mutableStateOf(book?.title ?: "") }
    var author by remember { mutableStateOf(book?.author ?: "") }
    var isbn by remember { mutableStateOf(book?.isbn ?: "") }
    var category by remember { mutableStateOf(book?.category ?: "") }
    var pageCount by remember { mutableStateOf(book?.pageCount?.toString() ?: "") }
    var totalCopies by remember { mutableStateOf(book?.totalCopies?.toString() ?: "1") }
    var availableCopies by remember { mutableStateOf(book?.avaiableCopies?.toString() ?: "1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditMode) "Kitap Düzenle" else "Yeni Kitap Ekle",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Kitap Adı *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Yazar *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = isbn,
                    onValueChange = { isbn = it },
                    label = { Text("ISBN") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = pageCount,
                    onValueChange = { pageCount = it },
                    label = { Text("Sayfa Sayısı *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = totalCopies,
                    onValueChange = { totalCopies = it },
                    label = { Text("Toplam Kopya") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = availableCopies,
                    onValueChange = { availableCopies = it },
                    label = { Text("Mevcut Kopya") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newBook = Book(
                        id = book?.id ?: "",
                        title = title.trim(),
                        author = author.trim(),
                        isbn = isbn.trim(),
                        category = category.trim(),
                        pageCount = pageCount.toIntOrNull() ?: 0,
                        totalCopies = totalCopies.toIntOrNull() ?: 1,
                        avaiableCopies = availableCopies.toIntOrNull() ?: 1
                    )
                    onSave(newBook)
                },
                enabled = title.isNotBlank() && author.isNotBlank() && pageCount.isNotBlank()
            ) {
                Text(if (isEditMode) "Güncelle" else "Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
