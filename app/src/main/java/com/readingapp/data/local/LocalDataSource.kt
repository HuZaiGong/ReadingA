package com.readingapp.data.local

import android.content.Context
import android.content.SharedPreferences
import com.readingapp.domain.model.Book
import com.readingapp.domain.model.ReadingProgress
import com.readingapp.domain.model.ReadingSettings
import org.json.JSONArray
import org.json.JSONObject

/**
 * 本地数据存储 - 使用SharedPreferences存储书籍数据和阅读进度
 */
class LocalDataSource(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "reading_app_prefs"
        private const val KEY_BOOKS = "books"
        private const val KEY_PROGRESS_PREFIX = "progress_"
        private const val KEY_SETTINGS = "settings"
    }
    
    // ========== 书籍操作 ==========
    
    fun getAllBooks(): List<Book> {
        val json = prefs.getString(KEY_BOOKS, "[]") ?: "[]"
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                Book(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    author = obj.getString("author"),
                    filePath = obj.getString("filePath"),
                    coverPath = obj.optString("coverPath", null),
                    totalChars = obj.optLong("totalChars", 0),
                    lastReadPosition = obj.optInt("lastReadPosition", 0),
                    lastReadTime = obj.optLong("lastReadTime", 0),
                    isFavorite = obj.optBoolean("isFavorite", false)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun saveBooks(books: List<Book>) {
        val array = JSONArray()
        books.forEach { book ->
            val obj = JSONObject().apply {
                put("id", book.id)
                put("title", book.title)
                put("author", book.author)
                put("filePath", book.filePath)
                put("coverPath", book.coverPath ?: "")
                put("totalChars", book.totalChars)
                put("lastReadPosition", book.lastReadPosition)
                put("lastReadTime", book.lastReadTime)
                put("isFavorite", book.isFavorite)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_BOOKS, array.toString()).apply()
    }
    
    fun getBookById(bookId: String): Book? {
        return getAllBooks().find { it.id == bookId }
    }
    
    fun addBook(book: Book) {
        val books = getAllBooks().toMutableList()
        books.add(book)
        saveBooks(books)
    }
    
    fun updateBook(book: Book) {
        val books = getAllBooks().toMutableList()
        val index = books.indexOfFirst { it.id == book.id }
        if (index != -1) {
            books[index] = book
            saveBooks(books)
        }
    }
    
    fun deleteBook(bookId: String) {
        val books = getAllBooks().filter { it.id != bookId }
        saveBooks(books)
        // 同时删除阅读进度
        prefs.edit().remove("$KEY_PROGRESS_PREFIX$bookId").apply()
    }
    
    // ========== 阅读进度操作 ==========
    
    fun saveReadingProgress(progress: ReadingProgress) {
        val json = JSONObject().apply {
            put("bookId", progress.bookId)
            put("position", progress.position)
            put("percentage", progress.percentage.toDouble())
            put("lastReadTime", progress.lastReadTime)
        }
        prefs.edit().putString("$KEY_PROGRESS_PREFIX${progress.bookId}", json.toString()).apply()
    }
    
    fun getReadingProgress(bookId: String): ReadingProgress? {
        val json = prefs.getString("$KEY_PROGRESS_PREFIX$bookId", null) ?: return null
        return try {
            val obj = JSONObject(json)
            ReadingProgress(
                bookId = obj.getString("bookId"),
                position = obj.getInt("position"),
                percentage = obj.getDouble("percentage").toFloat(),
                lastReadTime = obj.getLong("lastReadTime")
            )
        } catch (e: Exception) {
            null
        }
    }
    
    // ========== 阅读设置操作 ==========
    
    fun getReadingSettings(): ReadingSettings {
        val json = prefs.getString(KEY_SETTINGS, null)
        return if (json != null) {
            try {
                val obj = JSONObject(json)
                ReadingSettings(
                    fontSize = obj.optInt("fontSize", 18),
                    lineSpacing = obj.optDouble("lineSpacing", 1.5).toFloat(),
                    isDarkMode = obj.optBoolean("isDarkMode", false),
                    backgroundColor = obj.optLong("backgroundColor", 0xFFF5F5DC),
                    textColor = obj.optLong("textColor", 0xFF333333)
                )
            } catch (e: Exception) {
                ReadingSettings()
            }
        } else {
            ReadingSettings()
        }
    }
    
    fun saveReadingSettings(settings: ReadingSettings) {
        val json = JSONObject().apply {
            put("fontSize", settings.fontSize)
            put("lineSpacing", settings.lineSpacing.toDouble())
            put("isDarkMode", settings.isDarkMode)
            put("backgroundColor", settings.backgroundColor)
            put("textColor", settings.textColor)
        }
        prefs.edit().putString(KEY_SETTINGS, json.toString()).apply()
    }
}