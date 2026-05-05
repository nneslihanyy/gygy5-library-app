package com.turkcell.libraryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.libraryapp.data.model.BorrowRecord
import com.turkcell.libraryapp.data.repository.BorrowRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BorrowViewModel : ViewModel() {
    private val repository = BorrowRepository()

    private val _borrows = MutableStateFlow<List<BorrowRecord>>(emptyList())
    val borrows: StateFlow<List<BorrowRecord>> = _borrows

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _borrowSuccess = MutableStateFlow(false)
    val borrowSuccess: StateFlow<Boolean> = _borrowSuccess

    fun borrowBook(studentId: String, bookId: String, days: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _borrowSuccess.value = false

            val borrowDays = days.coerceIn(1, 5)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val today = Calendar.getInstance()
            val todayStr = dateFormat.format(today.time)

            val dueCalendar = Calendar.getInstance()
            dueCalendar.add(Calendar.DAY_OF_YEAR, borrowDays)
            val dueDateStr = dateFormat.format(dueCalendar.time)

            val record = BorrowRecord(
                studentId = studentId,
                bookId = bookId,
                borrowedAt = todayStr,
                dueDate = dueDateStr,
                status = "active"
            )

            repository.borrowBook(record)
                .onSuccess {
                    _borrowSuccess.value = true
                    loadBorrows(studentId)
                }
                .onFailure {
                    _error.value = it.message
                }
            _isLoading.value = false
        }
    }

    fun returnBook(recordId: String, bookId: String, studentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.returnBook(recordId, bookId)
                .onSuccess {
                    loadBorrows(studentId)
                }
                .onFailure {
                    _error.value = it.message
                }
            _isLoading.value = false
        }
    }

    fun loadBorrows(studentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getMyBorrows(studentId)
                .onSuccess { _borrows.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun resetBorrowSuccess() {
        _borrowSuccess.value = false
    }

    fun clearError() {
        _error.value = null
    }
}
