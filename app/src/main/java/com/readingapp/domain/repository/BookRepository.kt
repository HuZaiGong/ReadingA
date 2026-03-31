package com.readingapp.domain.repository

import com.readingapp.domain.model.Book
import com.readingapp.domain.model.ReadingProgress
import com.readingapp.domain.model.ReadingSettings

/**
 * 书籍仓库接口 - 领域层定义
 */
interface BookRepository {
    /**
     * 获取所有书籍
     */
    suspend fun getAllBooks(): List<Book>
    
    /**
     * 添加书籍
     */
    suspend fun addBook(book: Book)
    
    /**
     * 删除书籍
     */
    suspend fun deleteBook(bookId: String)
    
    /**
     * 更新书籍
     */
    suspend fun updateBook(book: Book)
    
    /**
     * 根据ID获取书籍
     */
    suspend fun getBookById(bookId: String): Book?
    
    /**
     * 保存阅读进度
     */
    suspend fun saveReadingProgress(progress: ReadingProgress)
    
    /**
     * 获取阅读进度
     */
    suspend fun getReadingProgress(bookId: String): ReadingProgress?
}

/**
 * 阅读设置仓库接口
 */
interface SettingsRepository {
    /**
     * 获取阅读设置
     */
    suspend fun getReadingSettings(): ReadingSettings
    
    /**
     * 保存阅读设置
     */
    suspend fun saveReadingSettings(settings: ReadingSettings)
}