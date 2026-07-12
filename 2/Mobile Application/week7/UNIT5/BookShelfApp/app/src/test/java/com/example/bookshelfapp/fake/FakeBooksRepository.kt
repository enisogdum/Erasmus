package com.example.bookshelfapp.fake

import com.example.bookshelfapp.data.Book
import com.example.bookshelfapp.data.BooksRepository

class FakeBooksRepository : BooksRepository {
    var shouldReturnError = false

    override suspend fun getBooks(query: String): List<Book> {
        if (shouldReturnError) {
            throw Exception("Fake repository error")
        }
        return FakeDataSource.booksList
    }
}
