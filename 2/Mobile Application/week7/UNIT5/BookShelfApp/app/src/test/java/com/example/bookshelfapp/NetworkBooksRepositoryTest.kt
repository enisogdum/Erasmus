package com.example.bookshelfapp

import com.example.bookshelfapp.data.Book
import com.example.bookshelfapp.data.BooksResponse
import com.example.bookshelfapp.data.ImageLinks
import com.example.bookshelfapp.data.NetworkBooksRepository
import com.example.bookshelfapp.fake.FakeBooksApiService
import com.example.bookshelfapp.fake.FakeDataSource
import com.example.bookshelfapp.network.BooksApiService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkBooksRepositoryTest {

    // -------------------------------------------------------------------------
    // 1. Happy path – single search call returns the book list
    // -------------------------------------------------------------------------
    @Test
    fun networkBooksRepository_getBooks_verifyBooksList() = runTest {
        val repository = NetworkBooksRepository(FakeBooksApiService())
        val result = repository.getBooks("query")
        assertEquals(FakeDataSource.booksList, result)
    }

    // -------------------------------------------------------------------------
    // 2. Verify searchBooks() is called exactly once per query
    // -------------------------------------------------------------------------
    @Test
    fun networkBooksRepository_getBooks_callsSearchBooksOnce() = runTest {
        var callCount = 0

        val trackingService = object : BooksApiService {
            override suspend fun searchBooks(query: String, maxResults: Int, apiKey: String?): BooksResponse {
                callCount++
                return BooksResponse(items = FakeDataSource.booksList)
            }
            override suspend fun getBook(volumeId: String, apiKey: String?): Book =
                throw AssertionError("getBook() should not be called")
        }

        NetworkBooksRepository(trackingService).getBooks("test")
        assertEquals(1, callCount)
    }

    // -------------------------------------------------------------------------
    // 3. Empty search result – repository returns an empty list gracefully
    // -------------------------------------------------------------------------
    @Test
    fun networkBooksRepository_getBooks_emptySearchReturnsEmptyList() = runTest {
        val emptyService = object : BooksApiService {
            override suspend fun searchBooks(query: String, maxResults: Int, apiKey: String?) =
                BooksResponse(items = null)
            override suspend fun getBook(volumeId: String, apiKey: String?): Book =
                throw AssertionError("getBook should not be called when search is empty")
        }

        val result = NetworkBooksRepository(emptyService).getBooks("obscure query")
        assertTrue(result.isEmpty())
    }

    // -------------------------------------------------------------------------
    // 4. URL rewriting – http:// thumbnail URLs are rewritten to https://
    // -------------------------------------------------------------------------
    @Test
    fun imageLinks_httpsThumbnail_replacesHttpWithHttps() {
        val links = ImageLinks(
            thumbnail = "http://books.google.com/books/content?zoom=1",
            smallThumbnail = "http://books.google.com/books/content?zoom=5"
        )
        assertEquals("https://books.google.com/books/content?zoom=1", links.httpsThumbnail)
        assertEquals("https://books.google.com/books/content?zoom=5", links.httpsSmallThumbnail)
    }

    // -------------------------------------------------------------------------
    // 5. URL rewriting – already-https URLs are left unchanged
    // -------------------------------------------------------------------------
    @Test
    fun imageLinks_httpsThumbnail_doesNotDoubleReplaceHttps() {
        val links = ImageLinks(
            thumbnail = "https://books.google.com/books/content?zoom=1",
            smallThumbnail = null
        )
        assertEquals("https://books.google.com/books/content?zoom=1", links.httpsThumbnail)
    }

    // -------------------------------------------------------------------------
    // 6. Null thumbnail – httpsThumbnail returns null without crashing
    // -------------------------------------------------------------------------
    @Test
    fun imageLinks_httpsThumbnail_returnsNullWhenThumbnailIsNull() {
        val links = ImageLinks(thumbnail = null, smallThumbnail = null)
        assertTrue(links.httpsThumbnail == null)
        assertTrue(links.httpsSmallThumbnail == null)
    }
}
