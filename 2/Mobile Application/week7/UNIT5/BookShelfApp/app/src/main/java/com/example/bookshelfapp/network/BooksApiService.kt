package com.example.bookshelfapp.network

import com.example.bookshelfapp.BuildConfig
import com.example.bookshelfapp.data.Book
import com.example.bookshelfapp.data.BooksResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BooksApiService {
    /**
     * Search for books by query term.
     * Example: GET /volumes?q=kotlin&maxResults=20&key=YOUR_KEY
     *
     * The [key] parameter is the Google Books API key from BuildConfig.
     * If it is empty the request is sent without a key (anonymous quota applies).
     */
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Query("key") apiKey: String? = BuildConfig.BOOKS_API_KEY.takeIf { it.isNotBlank() }
    ): BooksResponse

    /**
     * Fetch full details for a specific volume by its ID.
     * Example: GET /volumes/EPUTEAAAQBAJ?key=YOUR_KEY
     */
    @GET("volumes/{id}")
    suspend fun getBook(
        @Path("id") volumeId: String,
        @Query("key") apiKey: String? = BuildConfig.BOOKS_API_KEY.takeIf { it.isNotBlank() }
    ): Book
}
