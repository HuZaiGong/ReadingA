package com.readingapp.domain.usecase

import com.readingapp.domain.model.Book
import com.readingapp.domain.repository.BookRepository
import java.util.UUID

/**
 * 添加书籍用例
 */
class AddBookUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(
        title: String,
        author: String,
        filePath: String,
        totalChars: Long
    ): Book {
        val book = Book(
            id = UUID.randomUUID().toString(),
            title = title,
            author = author,
            filePath = filePath,
            totalChars = totalChars
        )
        repository.addBook(book)
        return book
    }
}

/**
 * 获取所有书籍用例
 */
class GetAllBooksUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(): List<Book> {
        return repository.getAllBooks()
    }
}

/**
 * 删除书籍用例
 */
class DeleteBookUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(bookId: String) {
        repository.deleteBook(bookId)
    }
}

/**
 * 更新阅读进度用例
 */
class UpdateReadingProgressUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(
        bookId: String,
        position: Int,
        totalChars: Long
    ) {
        val progress = com.readingapp.domain.model.ReadingProgress(
            bookId = bookId,
            position = position,
            percentage = if (totalChars > 0) (position.toFloat() / totalChars) * 100 else 0f,
            lastReadTime = System.currentTimeMillis()
        )
        repository.saveReadingProgress(progress)
    }
}

/**
 * 获取阅读进度用例
 */
class GetReadingProgressUseCase(private val repository: BookRepository) {
    suspend operator fun invoke(bookId: String): com.readingapp.domain.model.ReadingProgress? {
        return repository.getReadingProgress(bookId)
    }
}