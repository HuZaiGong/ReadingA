package com.readingapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.readingapp.data.local.LocalDataSource
import com.readingapp.data.repository.SettingsRepositoryImpl
import com.readingapp.domain.model.ReadingSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 设置ViewModel - 管理阅读器设置
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val localDataSource = LocalDataSource(application)
    private val settingsRepository = SettingsRepositoryImpl(localDataSource)

    private val _settings = MutableStateFlow(ReadingSettings())
    val settings: StateFlow<ReadingSettings> = _settings.asStateFlow()

    init {
        loadSettings()
    }

    // 加载设置
    private fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val loadedSettings = settingsRepository.getReadingSettings()
            _settings.value = loadedSettings
        }
    }

    // 更新字体大小
    fun updateFontSize(size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val newSettings = _settings.value.copy(fontSize = size)
            settingsRepository.saveReadingSettings(newSettings)
            _settings.value = newSettings
        }
    }

    // 切换夜间模式
    fun toggleDarkMode() {
        viewModelScope.launch(Dispatchers.IO) {
            val newSettings = _settings.value.copy(isDarkMode = !_settings.value.isDarkMode)
            settingsRepository.saveReadingSettings(newSettings)
            _settings.value = newSettings
        }
    }

    // 设置夜间模式
    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newSettings = _settings.value.copy(isDarkMode = isDark)
            settingsRepository.saveReadingSettings(newSettings)
            _settings.value = newSettings
        }
    }

    // 更新行间距
    fun updateLineSpacing(spacing: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            val newSettings = _settings.value.copy(lineSpacing = spacing)
            settingsRepository.saveReadingSettings(newSettings)
            _settings.value = newSettings
        }
    }

    // 更新背景色
    fun updateBackgroundColor(color: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val newSettings = _settings.value.copy(backgroundColor = color)
            settingsRepository.saveReadingSettings(newSettings)
            _settings.value = newSettings
        }
    }

    // 更新文字颜色
    fun updateTextColor(color: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val newSettings = _settings.value.copy(textColor = color)
            settingsRepository.saveReadingSettings(newSettings)
            _settings.value = newSettings
        }
    }
}