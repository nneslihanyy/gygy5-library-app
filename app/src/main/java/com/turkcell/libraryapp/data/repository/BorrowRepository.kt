package com.turkcell.libraryapp.data.repository

import com.turkcell.libraryapp.data.model.BorrowRecord
import com.turkcell.libraryapp.data.supabase.supabase
import io.github.jan.supabase.postgrest.postgrest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BorrowRepository {

    suspend fun borrowBook(record: BorrowRecord): Result<Unit> = runCatching {
        supabase.postgrest["borrow_records"].insert(record)

        val book = supabase.postgrest["books"]
            .select { filter { eq("id", record.bookId) } }
            .decodeSingle<com.turkcell.libraryapp.data.model.Book>()

        supabase.postgrest["books"].update(
            { set("available_copies", book.avaiableCopies - 1) }
        ) {
            filter { eq("id", record.bookId) }
        }
    }

    suspend fun returnBook(recordId: String, bookId: String): Result<Unit> = runCatching {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val now = dateFormat.format(Calendar.getInstance().time)

        supabase.postgrest["borrow_records"].update(
            {
                set("returned_at", now)
                set("status", "returned")
            }
        ) {
            filter { eq("id", recordId) }
        }

        val book = supabase.postgrest["books"]
            .select { filter { eq("id", bookId) } }
            .decodeSingle<com.turkcell.libraryapp.data.model.Book>()

        supabase.postgrest["books"].update(
            { set("available_copies", book.avaiableCopies + 1) }
        ) {
            filter { eq("id", bookId) }
        }
    }

    suspend fun getMyBorrows(studentId: String): Result<List<BorrowRecord>> = runCatching {
        supabase.postgrest["borrow_records"]
            .select { filter { eq("student_id", studentId) } }
            .decodeList<BorrowRecord>()
    }

    suspend fun getActiveBorrows(studentId: String): Result<List<BorrowRecord>> = runCatching {
        supabase.postgrest["borrow_records"]
            .select {
                filter {
                    eq("student_id", studentId)
                    eq("status", "active")
                }
            }
            .decodeList<BorrowRecord>()
    }
}
