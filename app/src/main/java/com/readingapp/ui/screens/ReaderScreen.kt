package com.readingapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.readingapp.domain.model.Book
import com.readingapp.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 阅读器界面 - 显示书籍内容，支持夜间模式、进度记忆等
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    book: Book?,
    content: String,
    progress: Float?,
    isDarkMode: Boolean,
    fontSize: Int,
    onBackClick: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onProgressChange: (Int) -> Unit,
    isLoading: Boolean
) {
    var showControls by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    
    // 滚动状态
    val scrollState = rememberScrollState()
    
    // 自动保存阅读进度
    LaunchedEffect(scrollState.value) {
        if (content.isNotEmpty()) {
            onProgressChange(scrollState.value)
        }
    }
    
    // 计算背景和文字颜色
    val backgroundColor = when {
        isDarkMode -> DarkBackground
        else -> LightBackground
    }
    
    val textColor = when {
        isDarkMode -> DarkText
        else -> LightText
    }

    // 主界面
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                showControls = !showControls
            }
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (book == null || content.isEmpty()) {
            Text(
                text = "无法加载内容",
                modifier = Modifier.align(Alignment.Center),
                color = textColor
            )
        } else {
            // 阅读内容
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = if (showControls) 80.dp else 24.dp,
                        bottom = if (showControls) 80.dp else 24.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                    .verticalScroll(scrollState)
            ) {
                // 章节标题（使用书名）
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = (fontSize + 4).sp,
                        color = textColor
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // 正文内容
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * 1.8).sp,
                        color = textColor
                    ),
                    textAlign = TextAlign.Justify
                )
                
                // 底部留白，确保可以滚动到最后
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // 顶部控制栏
        if (showControls && book != null) {
            TopAppBar(
                title = {
                    Text(
                        text = book.title,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showSettings = !showSettings }) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }

        // 底部进度条
        if (showControls && content.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 进度百分比
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "已读 ${String.format("%.1f", progress ?: 0f)}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "位置: ${scrollState.value}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 进度滑块
                    Slider(
                        value = scrollState.value.toFloat(),
                        onValueChange = { scrollState.scrollTo(it.toInt()) },
                        valueRange = 0f..scrollState.maxValue.toFloat(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 设置面板
        if (showSettings && showControls) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "阅读设置",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // 夜间模式开关
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("夜间模式")
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { onToggleDarkMode() }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 字体大小调节
                    Text(
                        text = "字体大小: ${fontSize}sp",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDecreaseFontSize) {
                            Icon(Icons.Default.Remove, contentDescription = "减小")
                        }
                        Slider(
                            value = fontSize.toFloat(),
                            onValueChange = { /* 拖动时由按钮处理 */ },
                            valueRange = 12f..32f,
                            steps = 9,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onIncreaseFontSize) {
                            Icon(Icons.Default.Add, contentDescription = "增大")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 快捷字体按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(14, 18, 22, 26).forEach { size ->
                            TextButton(
                                onClick = { 
                                    // 这里需要外部处理，暂时不可用
                                }
                            ) {
                                Text("${size}")
                            }
                        }
                    }
                }
            }
        }
    }
}