package com.example.bookshelfapp

import com.example.bookshelfapp.fake.FakeBooksRepository
import com.example.bookshelfapp.fake.FakeDataSource
import com.example.bookshelfapp.ui.BooksUiState
import com.example.bookshelfapp.ui.BooksViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BooksViewModelTest {

    // UnconfinedTestDispatcher runs launched coroutines eagerly, so we don't
    // need to call advanceUntilIdle() manually after creating the ViewModel.
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun booksViewModel_getBooks_verifyBooksUiStateSuccess() = runTest(testDispatcher) {
        val viewModel = BooksViewModel(booksRepository = FakeBooksRepository())

        assertEquals(
            BooksUiState.Success(FakeDataSource.booksList),
            viewModel.booksUiState
        )
    }

    @Test
    fun booksViewModel_getBooks_verifyBooksUiStateError() = runTest(testDispatcher) {
        val fakeRepository = FakeBooksRepository().apply {
            shouldReturnError = true
        }
        val viewModel = BooksViewModel(booksRepository = fakeRepository)

        assertEquals(
            BooksUiState.Error("Fake repository error"),
            viewModel.booksUiState
        )
    }
}
