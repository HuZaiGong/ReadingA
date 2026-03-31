package com.readingapp.data.repository

import com.readingapp.data.local.LocalDataSource
import com.readingapp.domain.model.Book
import com.readingapp.domain.model.ReadingProgress
import com.readingapp.domain.model.ReadingSettings
import com.readingapp.domain.repository.BookRepository
import com.readingapp.domain.repository.SettingsRepository

/**
 * 书籍仓库实现 - 数据层
 */
class BookRepositoryImpl(
    private val localDataSource: LocalDataSource
) : BookRepository {
    
    override suspend fun getAllBooks(): List<Book> {
        return localDataSource.getAllBooks().sortedByDescending { it.lastReadTime }
    }
    
    override suspend fun addBook(book: Book) {
        localDataSource.addBook(book)
    }
    
    override suspend fun deleteBook(bookId: String) {
        localDataSource.deleteBook(bookId)
    }
    
    override suspend fun updateBook(book: Book) {
        localDataSource.updateBook(book)
    }
    
    override suspend fun getBookById(bookId: String): Book? {
        return localDataSource.getBookById(bookId)
    }
    
    override suspend fun saveReadingProgress(progress: ReadingProgress) {
        localDataSource.saveReadingProgress(progress)
    }
    
    override suspend fun getReadingProgress(bookId: String): ReadingProgress? {
        return localDataSource.getReadingProgress(bookId)
    }
}

/**
 * 设置仓库实现 - 数据层
 */
class SettingsRepositoryImpl(
    private val localDataSource: LocalDataSource
) : SettingsRepository {
    
    override suspend fun getReadingSettings(): ReadingSettings {
        return localDataSource.getReadingSettings()
    }
    
    override suspend fun saveReadingSettings(settings: ReadingSettings) {
        localDataSource.saveReadingSettings(settings)
    }
}