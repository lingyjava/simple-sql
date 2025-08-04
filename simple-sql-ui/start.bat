@echo off
setlocal enabledelayedexpansion

REM Simple SQL 智能启动脚本 (Windows版本)
REM 自动选择最佳运行方式

echo 🚀 启动 Simple SQL 应用程序...
echo.

REM 检查Java版本
echo 📋 检查Java版本...
java --version
echo.

REM 检查是否在项目目录中
if not exist "pom.xml" (
    echo ❌ 错误: 请在项目根目录运行此脚本
    echo    当前目录: %CD%
    echo    请切换到 simple-sql-ui 目录
    pause
    exit /b 1
)

REM 检查jar包是否存在
set JAR_FILE=target\simple-sql-1.1.0.jar
if not exist "%JAR_FILE%" (
    echo ⚠️  警告: 找不到jar包 %JAR_FILE%
    echo    正在构建项目...
    mvn clean package -DskipTests
    echo.
)

REM 检查是否有JavaFX SDK
set JAVAFX_SDK_PATH=

REM 1. 首先检查环境变量
if defined JAVAFX_HOME (
    set JAVAFX_SDK_PATH=%JAVAFX_HOME%
) else if defined JAVAFX_SDK_HOME (
    set JAVAFX_SDK_PATH=%JAVAFX_SDK_HOME%
)

REM 2. 如果环境变量未设置，检查常见安装路径
if "%JAVAFX_SDK_PATH%"=="" (
    REM Windows 常见路径
    for %%p in ("C:\Program Files\Java\javafx-sdk" "C:\Program Files (x86)\Java\javafx-sdk" "%USERPROFILE%\javafx-sdk" "C:\javafx-sdk") do (
        if exist "%%p" (
            set JAVAFX_SDK_PATH=%%p
            goto :found_javafx
        )
    )
)

:found_javafx

echo 🔍 检测运行环境...
echo.

REM 方式1: 使用Maven运行（推荐）
echo ✅ 方式1: 使用Maven运行（推荐）
echo    命令: mvn javafx:run
echo    优点: 自动处理JavaFX依赖，无需额外配置
echo.

REM 方式2: 如果有JavaFX SDK，使用jar包运行
if not "%JAVAFX_SDK_PATH%"=="" (
    echo ✅ 方式2: 使用jar包运行（检测到JavaFX SDK）
    echo    JavaFX SDK路径: %JAVAFX_SDK_PATH%
    echo    命令: java --module-path "%JAVAFX_SDK_PATH%\lib" --add-modules javafx.controls,javafx.fxml -jar %JAR_FILE%
    echo.
) else (
    echo ⚠️  方式2: 使用jar包运行（需要JavaFX SDK）
    echo    需要下载JavaFX SDK: https://gluonhq.com/products/javafx/
    echo    命令: java --module-path C:\path\to\javafx-sdk\lib --add-modules javafx.controls,javafx.fxml -jar %JAR_FILE%
    echo.
)

REM 方式3: 直接运行jar包（会失败，但用于演示）
echo ❌ 方式3: 直接运行jar包（不推荐）
echo    命令: java -jar %JAR_FILE%
echo    结果: 会失败，缺少JavaFX运行时
echo.

echo 🎯 推荐运行方式:
echo    1. 开发环境: mvn javafx:run
echo    2. 生产环境: 安装JavaFX SDK后使用jar包运行
echo.

REM 询问用户选择
echo 请选择运行方式:
echo 1) 使用Maven运行（推荐）
echo 2) 使用jar包运行（需要JavaFX SDK）
echo 3) 直接运行jar包（会失败，仅用于演示）
echo 4) 退出
echo.
set /p choice="请输入选择 (1-4): "

if "%choice%"=="1" (
    echo 🚀 使用Maven启动应用程序...
    mvn javafx:run
) else if "%choice%"=="2" (
    if not "%JAVAFX_SDK_PATH%"=="" (
        echo 🚀 使用jar包启动应用程序...
        java --module-path "%JAVAFX_SDK_PATH%\lib" --add-modules javafx.controls,javafx.fxml -jar "%JAR_FILE%"
    ) else (
        echo ❌ 未检测到JavaFX SDK
        echo 请下载并安装JavaFX SDK: https://gluonhq.com/products/javafx/
        echo 然后重新运行此脚本
        pause
    )
) else if "%choice%"=="3" (
    echo 🚀 直接运行jar包（会失败）...
    java -jar "%JAR_FILE%"
) else if "%choice%"=="4" (
    echo 👋 退出
    exit /b 0
) else (
    echo ❌ 无效选择
    exit /b 1
)

pause 