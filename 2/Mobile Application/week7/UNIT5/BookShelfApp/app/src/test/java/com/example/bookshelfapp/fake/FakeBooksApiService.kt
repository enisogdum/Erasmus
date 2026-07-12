package com.example.bookshelfapp.fake

import com.example.bookshelfapp.data.Book
import com.example.bookshelfapp.data.BooksResponse
import com.example.bookshelfapp.data.ImageLinks
import com.example.bookshelfapp.data.VolumeInfo
import com.example.bookshelfapp.network.BooksApiService

/**
 * Fake implementation of [BooksApiService] for unit tests.
 * Returns [FakeDataSource.booksList] without making any network calls.
 */
class FakeBooksApiService : BooksApiService {

    override suspend fun searchBooks(query: String, maxResults: Int, apiKey: String?): BooksResponse {
        return BooksResponse(items = FakeDataSource.booksList)
    }

    override suspend fun getBook(volumeId: String, apiKey: String?): Book {
        return FakeDataSource.booksList.first { it.id == volumeId }
    }
}

object FakeDataSource {
    val book1 = Book(
        id = "id1",
        volumeInfo = VolumeInfo(
            title = "Title 1",
            authors = listOf("Author 1"),
            description = "Description 1",
            publisher = "Publisher 1",
            publishedDate = "2021-06-15",
            pageCount = 100,
            imageLinks = ImageLinks(
                // Note: API returns http:// URLs — our httpsThumbnail getter rewrites them
                smallThumbnail = "http://thumbnail1.com/small",
                thumbnail = "http://thumbnail1.com/full"
            ),
            previewLink = "https://books.google.com/books?id=id1"
        )
    )

    val book2 = Book(
        id = "id2",
        volumeInfo = VolumeInfo(
            title = "Title 2",
            authors = listOf("Author 2", "Co-Author 2"),
            description = "Description 2",
            publisher = "Publisher 2",
            publishedDate = "2022-03-01",
            pageCount = 200,
            imageLinks = ImageLinks(
                smallThumbnail = "http://thumbnail2.com/small",
                thumbnail = "http://thumbnail2.com/full"
            ),
            previewLink = "https://books.google.com/books?id=id2"
        )
    )

    val booksList = listOf(book1, book2)
}
