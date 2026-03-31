package com.readingapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.readingapp.ui.screens.LibraryScreen
import com.readingapp.ui.screens.ReaderScreen
import com.readingapp.ui.theme.ReadingAppTheme
import com.readingapp.ui.viewmodel.BookViewModel
import com.readingapp.ui.viewmodel.SettingsViewModel

/**
 * 主Activity - 应用入口
 */
class MainActivity : ComponentActivity() {
    
    private val openDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { bookViewModel.addBookFromUri(it) }
    }
    
    private lateinit var bookViewModel: BookViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val bookViewModel: BookViewModel = viewModel()
            val settingsViewModel: SettingsViewModel = viewModel()
            
            val books by bookViewModel.books.collectAsState()
            val currentBook by bookViewModel.currentBook.collectAsState()
            val bookContent by bookViewModel.bookContent.collectAsState()
            val readingProgress by bookViewModel.readingProgress.collectAsState()
            val isLoading by bookViewModel.isLoading.collectAsState()
            
            val settings by settingsViewModel.settings.collectAsState()
            
            var currentScreen by remember { mutableStateOf(Screen.Library) }
            
            ReadingAppTheme(darkTheme = settings.isDarkMode) {
                when (currentScreen) {
                    Screen.Library -> {
                        LibraryScreen(
                            books = books,
                            onBookClick = { book ->
                                bookViewModel.selectBook(book)
                                currentScreen = Screen.Reader
                            },
                            onAddBookClick = {
                                openDocumentLauncher.launch(arrayOf("text/plain"))
                            },
                            onDeleteBook = { bookId ->
                                bookViewModel.deleteBook(bookId)
                            },
                            isLoading = isLoading
                        )
                    }
                    
                    Screen.Reader -> {
                        ReaderScreen(
                            book = currentBook,
                            content = bookContent,
                            progress = readingProgress?.percentage,
                            isDarkMode = settings.isDarkMode,
                            fontSize = settings.fontSize,
                            onBackClick = {
                                bookViewModel.clearCurrentBook()
                                currentScreen = Screen.Library
                            },
                            onToggleDarkMode = { 
                                settingsViewModel.toggleDarkMode()
                            },
                            onIncreaseFontSize = { 
                                val newSize = (settings.fontSize + 2).coerceAtMost(32)
                                settingsViewModel.updateFontSize(newSize)
                            },
                            onDecreaseFontSize = { 
                                val newSize = (settings.fontSize - 2).coerceAtLeast(12)
                                settingsViewModel.updateFontSize(newSize)
                            },
                            onProgressChange = { position ->
                                bookViewModel.updateProgress(position)
                            },
                            isLoading = isLoading
                        )
                    }
                }
            }
        }
        
        // 处理外部打开的TXT文件（通过Intent）
        intent?.data?.let { uri ->
            bookViewModel.addBookFromUri(uri)
        }
    }
    
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        intent.data?.let { uri ->
            if (::bookViewModel.isInitialized) {
                bookViewModel.addBookFromUri(uri)
            }
        }
    }
}

/**
 * 屏幕状态
 */
sealed class Screen {
    data object Library : Screen()
    data object Reader : Screen()
}