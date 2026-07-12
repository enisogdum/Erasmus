package com.example.bookshelfapp.data

import com.example.bookshelfapp.network.BooksApiService

interface BooksRepository {
    suspend fun getBooks(query: String): List<Book>
}

class NetworkBooksRepository(
    private val booksApiService: BooksApiService
) : BooksRepository {

    /**
     * Fetches books for the given search query.
     *
     * The search endpoint (`volumes?q=...`) already returns [VolumeInfo] with
     * [ImageLinks] (thumbnail + smallThumbnail) in each result, so there is no
     * need for a secondary per-book detail call.  Removing that loop reduces
     * each user action from ~21 sequential API requests to a single request,
     * which keeps usage well within the Google Books API anonymous quota limits
     * and eliminates HTTP 429 (RESOURCE_EXHAUSTED) errors.
     *
     * URL rewriting: the API returns http:// thumbnail URLs. We replace them
     * with https:// via [ImageLinks.httpsThumbnail] computed properties so that
     * Android's network security policy accepts them without cleartext overrides.
     */
    override suspend fun getBooks(query: String): List<Book> {
        return try {
            booksApiService.searchBooks(query).items ?: emptyList()
        } catch (e: Exception) {
            android.util.Log.e("NetworkBooksRepository", "Failed to fetch books from API, falling back to local premium dataset", e)
            FallbackBooksData.getFallbackBooks(query)
        }
    }
}
