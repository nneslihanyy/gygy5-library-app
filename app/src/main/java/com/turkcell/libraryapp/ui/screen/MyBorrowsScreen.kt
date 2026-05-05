package com.turkcell.libraryapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.libraryapp.data.model.BorrowRecord
import com.turkcell.libraryapp.ui.viewmodel.BookViewModel
import com.turkcell.libraryapp.ui.viewmodel.BorrowViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBorrowsScreen(
    studentId: String,
    borrowViewModel: BorrowViewModel,
    bookViewModel: BookViewModel,
    onBack: () -> Unit
) {
    val borrows by borrowViewModel.borrows.collectAsState()
    val isLoading by borrowViewModel.isLoading.collectAsState()
    val error by borrowViewModel.error.collectAsState()
    val books by bookViewModel.books.collectAsState()

    LaunchedEffect(studentId) {
        borrowViewModel.loadBorrows(studentId)
    }

    val activeBorrows = borrows.filter { it.isActive }
    val pastBorrows = borrows.filter { !it.isActive }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kiralamalarım",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            error?.let { errMsg ->
                Text(
                    text = errMsg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (borrows.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz kiralama kaydınız yok.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    if (activeBorrows.isNotEmpty()) {
                        item {
                            Text(
                                text = "📖 Aktif Kiralamalar",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(activeBorrows) { record ->
                            BorrowRecordCard(
                                record = record,
                                bookTitle = books.find { it.id == record.bookId }?.title ?: "Kitap #${record.bookId.take(8)}",
                                bookAuthor = books.find { it.id == record.bookId }?.author ?: "",
                                onReturnClick = {
                                    borrowViewModel.returnBook(record.id, record.bookId, studentId)
                                    bookViewModel.loadBooks()
                                }
                            )
                        }
                    }

                    if (pastBorrows.isNotEmpty()) {
                        item {
                            Text(
                                text = "📚 Geçmiş Kiralamalar",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 16.dp,
                                    bottom = 8.dp
                                )
                            )
                        }
                        items(pastBorrows) { record ->
                            BorrowRecordCard(
                                record = record,
                                bookTitle = books.find { it.id == record.bookId }?.title ?: "Kitap #${record.bookId.take(8)}",
                                bookAuthor = books.find { it.id == record.bookId }?.author ?: "",
                                onReturnClick = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BorrowRecordCard(
    record: BorrowRecord,
    bookTitle: String,
    bookAuthor: String,
    onReturnClick: (() -> Unit)?
) {
    val isOverdue = record.isActive && try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dueDate = dateFormat.parse(record.dueDate)
        val today = Calendar.getInstance().time
        dueDate != null && dueDate.before(today)
    } catch (_: Exception) { false }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isOverdue -> MaterialTheme.colorScheme.errorContainer
                record.isActive -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bookTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            text = when {
                                isOverdue -> "GECİKMİŞ"
                                record.isActive -> "AKTİF"
                                else -> "İADE EDİLDİ"
                            },
                            fontSize = 11.sp
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when {
                            isOverdue -> MaterialTheme.colorScheme.error
                            record.isActive -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        labelColor = when {
                            isOverdue -> MaterialTheme.colorScheme.onError
                            record.isActive -> MaterialTheme.colorScheme.onPrimary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                )
            }

            if (bookAuthor.isNotEmpty()) {
                Text(
                    text = "Yazar: $bookAuthor",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Alınma: ${record.borrowedAt}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Son iade: ${record.dueDate}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isOverdue) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            record.returnedAt?.let { returnDate ->
                Text(
                    text = "İade edildi: $returnDate",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (onReturnClick != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onReturnClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOverdue)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("İade Et")
                }
            }
        }
    }
}
