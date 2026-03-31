package com.readingapp.domain.model

/**
 * 书籍实体类
 */
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val filePath: String,
    val coverPath: String? = null,
    val totalChars: Long = 0,
    val lastReadPosition: Int = 0,
    val lastReadTime: Long = 0,
    val isFavorite: Boolean = false
)

/**
 * 阅读进度
 */
data class ReadingProgress(
    val bookId: String,
    val position: Int,
    val percentage: Float,
    val lastReadTime: Long
)

/**
 * 阅读设置
 */
data class ReadingSettings(
    val fontSize: Int = 18,
    val lineSpacing: Float = 1.5f,
    val isDarkMode: Boolean = false,
    val backgroundColor: Long = 0xFFF5F5DC,
    val textColor: Long = 0xFF333333
)