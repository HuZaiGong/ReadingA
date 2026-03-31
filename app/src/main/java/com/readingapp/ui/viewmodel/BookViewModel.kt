package com.readingapp.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.readingapp.data.local.LocalDataSource
import com.readingapp.data.repository.BookRepositoryImpl
import com.readingapp.domain.model.Book
import com.readingapp.domain.model.ReadingProgress
import com.readingapp.domain.usecase.AddBookUseCase
import com.readingapp.domain.usecase.DeleteBookUseCase
import com.readingapp.domain.usecase.GetAllBooksUseCase
import com.readingapp.domain.usecase.GetReadingProgressUseCase
import com.readingapp.domain.usecase.UpdateReadingProgressUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

/**
 * 书籍ViewModel - 管理书架和阅读器的状态
 */
class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val localDataSource = LocalDataSource(application)
    private val repository = BookRepositoryImpl(localDataSource)

    private val getAllBooksUseCase = GetAllBooksUseCase(repository)
    private val addBookUseCase = AddBookUseCase(repository)
    private val deleteBookUseCase = DeleteBookUseCase(repository)
    private val getReadingProgressUseCase = GetReadingProgressUseCase(repository)
    private val updateReadingProgressUseCase = UpdateReadingProgressUseCase(repository)

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books.asStateFlow()

    private val _currentBook = MutableStateFlow<Book?>(null)
    val currentBook: StateFlow<Book?> = _currentBook.asStateFlow()

    private val _bookContent = MutableStateFlow<String>("")
    val bookContent: StateFlow<String> = _bookContent.asStateFlow()

    private val _readingProgress = MutableStateFlow<ReadingProgress?>(null)
    val readingProgress: StateFlow<ReadingProgress?> = _readingProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadBooks()
    }

    // 加载所有书籍
    fun loadBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            val bookList = getAllBooksUseCase()
            _books.value = bookList
        }
    }

    // 从URI添加书籍
    fun addBookFromUri(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val context = getApplication<Application>()
                val inputStream = context.contentResolver.openInputStream(uri)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val content = reader.readText()
                reader.close()

                val fileName = uri.lastPathSegment ?: "未知书籍"
                val title = fileName.substringBeforeLast(".")

                val book = Book(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    author = "未知作者",
                    filePath = uri.toString(),
                    totalChars = content.length.toLong()
                )

                addBookUseCase(book)
                _bookContent.value = content
                _currentBook.value = book

                // 加载阅读进度
                val progress = getReadingProgressUseCase(book.id)
                _readingProgress.value = progress

                loadBooks()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 选择书籍阅读
    fun selectBook(book: Book) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val context = getApplication<Application>()
                val uri = Uri.parse(book.filePath)
                val inputStream = context.contentResolver.openInputStream(uri)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val content = reader.readText()
                reader.close()

                _currentBook.value = book
                _bookContent.value = content

                // 加载阅读进度
                val progress = getReadingProgressUseCase(book.id)
                _readingProgress.value = progress
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 删除书籍
    fun deleteBook(bookId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteBookUseCase(bookId)
            if (_currentBook.value?.id == bookId) {
                _currentBook.value = null
                _bookContent.value = ""
                _readingProgress.value = null
            }
            loadBooks()
        }
    }

    // 更新阅读进度
    fun updateProgress(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val book = _currentBook.value ?: return@launch
            val totalChars = _bookContent.value.length
            val percentage = if (totalChars > 0) (position.toFloat() / totalChars) * 100 else 0f

            val progress = ReadingProgress(
                bookId = book.id,
                position = position,
                percentage = percentage,
                lastReadTime = System.currentTimeMillis()
            )

            updateReadingProgressUseCase(progress)
            _readingProgress.value = progress

            // 同时更新书籍的最后阅读位置
            val updatedBook = book.copy(
                lastReadPosition = position,
                lastReadTime = System.currentTimeMillis()
            )
            repository.updateBook(updatedBook)
        }
    }

    // 清除当前书籍
    fun clearCurrentBook() {
        _currentBook.value = null
        _bookContent.value = ""
        _readingProgress.value = null
    }
}