package com.example.bookshelfapp.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelfapp.BooksApplication
import com.example.bookshelfapp.data.Book
import com.example.bookshelfapp.data.BooksRepository
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException

sealed interface BooksUiState {
    data class Success(val books: List<Book>) : BooksUiState
    data class Error(val message: String = "Please check your network connection and try again.") : BooksUiState
    object Loading : BooksUiState
}

class BooksViewModel(private val booksRepository: BooksRepository) : ViewModel() {
    var booksUiState: BooksUiState by mutableStateOf(BooksUiState.Loading)
        private set

    var searchQuery by mutableStateOf("Kotlin")
        private set

    var selectedBook: Book? by mutableStateOf(null)

    init {
        getBooks(searchQuery)
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun getBooks(query: String = searchQuery) {
        viewModelScope.launch {
            booksUiState = BooksUiState.Loading
            booksUiState = try {
                val books = booksRepository.getBooks(query)
                BooksUiState.Success(books)
            } catch (e: HttpException) {
                val message = "Request failed (HTTP ${e.code()}). Check API key/quota and try again."
                Log.e("BooksViewModel", "HTTP error while loading books", e)
                BooksUiState.Error(message)
            } catch (e: IOException) {
                Log.e("BooksViewModel", "Network I/O error while loading books", e)
                BooksUiState.Error("Network error. Please check connection and try again.")
            } catch (e: Exception) {
                Log.e("BooksViewModel", "Unexpected error while loading books", e)
                val message = e.message?.takeIf { it.isNotBlank() }
                    ?: "Unexpected error occurred while loading books."
                BooksUiState.Error(message)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BooksApplication)
                val booksRepository = application.container.booksRepository
                BooksViewModel(booksRepository = booksRepository)
            }
        }
    }
}
