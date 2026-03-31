# ReadingApp 构建指南

## 项目结构完整性

已补充的构建文件：
- ✅ `gradle/wrapper/gradle-wrapper.properties` - Gradle 8.2 配置
- ✅ `gradlew` - Linux/Mac 执行脚本
- ✅ `gradlew.bat` - Windows 执行脚本
- ✅ `app/src/main/res/drawable/ic_launcher.xml` - 应用图标

## 使用 GitHub Actions 构建 APK

### 1. 推送到 GitHub

```bash
# 初始化 git 仓库（如尚未初始化）
cd ReadingApp
git init
git add .
git commit -m "Initial commit: ReadingApp with MVVM + Clean Architecture"

# 添加远程仓库（替换为你的仓库地址）
git remote add origin https://github.com/YOUR_USERNAME/ReadingApp.git
git branch -M main
git push -u origin main
```

### 2. 触发构建

#### 自动触发
- **Push 到 main/master 分支**：自动构建 Debug APK
- **创建标签**（如 `v1.0.0`）：自动构建 Release APK 并创建 GitHub Release

```bash
# 创建版本标签触发 Release 构建
git tag v1.0.0
git push origin v1.0.0
```

#### 手动触发
1. 进入 GitHub 仓库页面
2. 点击 **Actions** 标签
3. 选择 **Build Android APK** 工作流
4. 点击 **Run workflow** 按钮

### 3. 获取构建产物

构建完成后：
- **Debug APK**：在 Actions 页面 → 选择运行记录 → Artifacts 区域下载 `app-debug`
- **Release APK**：在仓库 **Releases** 页面下载（通过标签触发时自动创建）

## 本地构建（可选）

如果需要在本地构建，确保已安装：
- JDK 17+
- Android SDK（包含 Android 14 / API 34）

```bash
# 授予执行权限（Linux/Mac）
chmod +x gradlew

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease

# 清理构建
./gradlew clean
```

构建产物位置：
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

## GitHub Actions 工作流说明

`build.yml` 包含两个 Job：

### `build` (Debug 构建)
- 触发条件：push/PR 到 main/master 分支，或手动触发
- 产出：Debug APK（保留 7 天）

### `build-release` (Release 构建)
- 触发条件：仅当推送 `v*` 格式的版本标签时
- 依赖：`build` job 成功完成
- 产出：
  - Release APK（保留 30 天）
  - 自动创建 GitHub Release 并附加 APK

## 自定义签名（可选）

如需签名 Release APK，在仓库 Settings → Secrets and variables → Actions 中添加：

```
ANDROID_KEYSTORE_BASE64: <keystore 文件的 base64 编码>
KEYSTORE_PASSWORD: <密钥库密码>
KEY_ALIAS: <密钥别名>
KEY_PASSWORD: <密钥密码>
```

然后修改 `app/build.gradle.kts` 配置签名，并更新工作流使用签名构建。

## 技术栈

- **Gradle**: 8.2
- **Android Gradle Plugin**: 8.2.0
- **JDK**: 17
- **Target SDK**: Android 14 (API 34)
- **Kotlin**: 1.9.0
- **Jetpack Compose**: 2023.08.00