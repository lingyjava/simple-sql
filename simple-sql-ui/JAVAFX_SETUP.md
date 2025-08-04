# JavaFX SDK 环境变量设置

## 概述

智能启动脚本支持通过环境变量自动检测JavaFX SDK路径，无需手动指定路径。

## 支持的环境变量

### Linux/macOS

```bash
# 方式1: JAVAFX_HOME
export JAVAFX_HOME=/path/to/javafx-sdk

# 方式2: JAVAFX_SDK_HOME
export JAVAFX_SDK_HOME=/path/to/javafx-sdk
```

### Windows

```cmd
# 方式1: JAVAFX_HOME
set JAVAFX_HOME=C:\path\to\javafx-sdk

# 方式2: JAVAFX_SDK_HOME
set JAVAFX_SDK_HOME=C:\path\to\javafx-sdk
```

## 自动检测路径

如果未设置环境变量，脚本会自动检测以下常见安装路径：

### Linux/macOS

- `/Library/Java/JavaVirtualMachines/javafx-sdk`
- `/usr/local/javafx-sdk`
- `$HOME/javafx-sdk`
- `/opt/javafx-sdk`

### Windows

- `C:\Program Files\Java\javafx-sdk`
- `C:\Program Files (x86)\Java\javafx-sdk`
- `%USERPROFILE%\javafx-sdk`
- `C:\javafx-sdk`

## 设置示例

### Linux/macOS (bash/zsh)

```bash
# 添加到 ~/.bashrc 或 ~/.zshrc
export JAVAFX_HOME=/Users/username/javafx-sdk-21.0.7

# 重新加载配置
source ~/.bashrc
```

### Windows (PowerShell)

```powershell
# 设置环境变量
$env:JAVAFX_HOME = "C:\javafx-sdk-21.0.7"

# 永久设置（需要管理员权限）
[Environment]::SetEnvironmentVariable("JAVAFX_HOME", "C:\javafx-sdk-21.0.7", "User")
```

### Windows (CMD)

```cmd
# 临时设置
set JAVAFX_HOME=C:\javafx-sdk-21.0.7

# 永久设置
setx JAVAFX_HOME "C:\javafx-sdk-21.0.7"
```

## 验证设置

运行智能启动脚本，如果检测到JavaFX SDK，会显示：

```
✅ 方式2: 使用jar包运行（检测到JavaFX SDK）
   JavaFX SDK路径: /path/to/javafx-sdk
```

## 下载JavaFX SDK

如果未安装JavaFX SDK，可以从以下地址下载：

- **官方网站**: <https://gluonhq.com/products/javafx/>
- **版本选择**: 建议下载与JDK版本匹配的JavaFX SDK

## 注意事项

1. **路径格式**: 不要包含版本号，脚本会自动检测
2. **权限**: 确保脚本有读取JavaFX SDK目录的权限
3. **版本兼容**: 确保JavaFX SDK版本与JDK版本兼容
