package com.turkcell.libraryapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turkcell.libraryapp.data.model.Book
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun BorrowDialog(
    book: Book,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var days by remember { mutableStateOf("5") }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayStr = dateFormat.format(Calendar.getInstance().time)
    val dueDateStr = remember(days) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, (days.toIntOrNull() ?: 5).coerceIn(1, 5))
        dateFormat.format(cal.time)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Kitap Ödünç Al",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = book.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Yazar: ${book.author}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Mevcut kopya: ${book.avaiableCopies}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                OutlinedTextField(
                    value = days,
                    onValueChange = {
                        val num = it.filter { c -> c.isDigit() }
                        days = num
                    },
                    label = { Text("Kaç gün? (Maks. 5)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = {
                        Text("1 ile 5 gün arasında seçim yapabilirsiniz")
                    }
                )

                HorizontalDivider()
                Text(
                    text = "Ödünç alma tarihi: $todayStr",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "İade tarihi: $dueDateStr",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dayCount = (days.toIntOrNull() ?: 5).coerceIn(1, 5)
                    onConfirm(dayCount)
                },
                enabled = days.isNotBlank() && (days.toIntOrNull() ?: 0) in 1..5
            ) {
                Text("Ödünç Al")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
