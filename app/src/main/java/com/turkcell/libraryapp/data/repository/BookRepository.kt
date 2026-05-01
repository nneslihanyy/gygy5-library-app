package com.turkcell.libraryapp.data.repository

import com.turkcell.libraryapp.data.model.Book
import com.turkcell.libraryapp.data.supabase.supabase
import io.github.jan.supabase.postgrest.postgrest

class BookRepository {

    // Tüm kitapları getir
    suspend fun getAllBooks(): Result<List<Book>> = runCatching {
        supabase.postgrest["books"]
            .select()
            .decodeList<Book>()
    }

    // ID'ye göre tek kitap getir
    suspend fun getBookById(id: String): Result<Book> = runCatching {
        supabase.postgrest["books"]
            .select { filter { eq("id", id) } }
            .decodeSingle<Book>()
    }

    // Yeni kitap ekle
    suspend fun addBook(book: Book): Result<Unit> = runCatching {
        supabase.postgrest["books"].insert(book)
    }

    // ÖDEV 2: Kitap güncelle
    suspend fun updateBook(book: Book): Result<Unit> = runCatching {
        supabase.postgrest["books"].update(
            {
                set("title", book.title)
                set("author", book.author)
                set("isbn", book.isbn)
                set("category", book.category)
                set("page_count", book.pageCount)
                set("total_copies", book.totalCopies)
                set("available_copies", book.avaiableCopies)
            }
        ) {
            filter { eq("id", book.id) }
        }
    }

    // ÖDEV 2: Kitap sil
    suspend fun deleteBook(id: String): Result<Unit> = runCatching {
        supabase.postgrest["books"].delete {
            filter { eq("id", id) }
        }
    }

    // ÖDEV 2: Kitap arama (başlık veya yazara göre)
    suspend fun searchBooks(query: String): Result<List<Book>> = runCatching {
        supabase.postgrest["books"]
            .select {
                filter {
                    or {
                        ilike("title", "%$query%")
                        ilike("author", "%$query%")
                    }
                }
            }
            .decodeList<Book>()
    }
}