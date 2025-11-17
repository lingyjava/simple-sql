@echo off
setlocal EnableDelayedExpansion
chcp 65001 >nul

echo ========================================
echo    Simple SQL 应用程序启动脚本
echo ========================================
echo.

echo [信息] 检查Java版本...
where java >nul 2>&1
if errorlevel 1 (
    echo [错误] 未找到Java，请先安装JDK 17+
    echo 请访问: https://www.oracle.com/java/technologies/downloads/
    goto :err
)

rem 提取版本号字符串（例如 "17.0.12" 或 "1.8.0_321"）
for /f "tokens=3" %%v in ('java -version 2^>^&1 ^| findstr /i "version"') do set "JAVA_VERSION_FULL=%%v"
set "JAVA_VERSION_FULL=!JAVA_VERSION_FULL:\"=!"

rem 解析主版本号（点号前的数字）
for /f "tokens=1 delims=." %%m in ("!JAVA_VERSION_FULL!") do set "JAVA_VERSION_MAJOR=%%m"
if "!JAVA_VERSION_MAJOR!"=="" set "JAVA_VERSION_MAJOR=0"

rem 数值化并比较主版本号
2>nul set /a JAVA_VERSION_MAJOR_NUM=!JAVA_VERSION_MAJOR!+0
if !JAVA_VERSION_MAJOR_NUM! LSS 17 (
    echo [错误] Java版本过低，当前版本: !JAVA_VERSION_FULL!，需要JDK 17+
    echo 请升级到JDK 17或更高版本
    goto :err
)

echo [成功] Java版本检查通过: !JAVA_VERSION_FULL!
echo.

set "JAR_FILE=simple-sql.jar"
if not exist "%JAR_FILE%" (
    echo [错误] 找不到jar包 %JAR_FILE%
    echo 请确保jar包与启动脚本在同一目录下
    echo 当前目录: %cd%
    echo.
    echo 目录内容:
    dir /a
    echo.
    echo [提示] 从GitHub下载的jar包应该放在与启动脚本相同的目录中
    echo jar包名称应该是: %JAR_FILE%（不包含版本号）
    goto :err
)

echo [成功] 找到jar包: %JAR_FILE%
echo.

set "JAVAFX_SDK_PATH="

rem 1) 优先使用环境变量
if defined JAVAFX_HOME (
    set "JAVAFX_SDK_PATH=%JAVAFX_HOME%"
    echo [成功] 检测到环境变量 JAVAFX_HOME: %JAVAFX_SDK_PATH%
)
if not defined JAVAFX_SDK_PATH if defined JAVAFX_SDK_HOME (
    set "JAVAFX_SDK_PATH=%JAVAFX_SDK_HOME%"
    echo [成功] 检测到环境变量 JAVAFX_SDK_HOME: %JAVAFX_SDK_PATH%
)

rem 2) 若未设置，检查Windows常见安装路径
if not defined JAVAFX_SDK_PATH (
    echo [信息] 检查常见JavaFX SDK安装路径...
    for %%p in ("%USERPROFILE%\javafx-sdk" "C:\\javafx-sdk" "C:\\Program Files\\javafx-sdk" "C:\\Program Files\\Java\\javafx-sdk") do (
        if exist "%%~p" (
            set "JAVAFX_SDK_PATH=%%~p"
            echo [成功] 在常见路径找到JavaFX SDK: !JAVAFX_SDK_PATH!
            goto :foundFx
        )
    )
)

:foundFx
echo.
echo [信息] 检测运行环境...

if defined JAVAFX_SDK_PATH (
    echo [成功] 检测到JavaFX SDK，准备启动应用程序...
    echo JavaFX SDK路径: %JAVAFX_SDK_PATH%
    echo 启动命令: java --module-path "%JAVAFX_SDK_PATH%\lib" --add-modules javafx.controls,javafx.fxml -jar "%JAR_FILE%"
    echo.
    echo [启动] 正在启动 Simple SQL 应用程序...
    java --module-path "%JAVAFX_SDK_PATH%\lib" --add-modules javafx.controls,javafx.fxml -jar "%JAR_FILE%"
    if errorlevel 1 (
        echo [错误] 启动失败，请检查JavaFX SDK配置
        goto :err
    ) else (
        echo [成功] 应用程序已启动
        goto :eof
    )
) else (
    echo [错误] 未检测到JavaFX SDK，无法启动应用程序
    echo.
    echo [下载] 请访问以下链接下载JavaFX SDK:
    echo    https://gluonhq.com/products/javafx/
    echo.
    echo [安装步骤]:
    echo    1. 下载适合您系统的JavaFX SDK
    echo    2. 解压到指定目录（例如：C:\\javafx-sdk）
    echo    3. 设置环境变量（可选）:
    echo       setx JAVAFX_HOME "C:\\javafx-sdk"
    echo    4. 重新运行此启动脚本
    echo.
    echo [提示] 安装完成后，重新运行此脚本即可自动启动
    goto :err
)

:err
echo.
pause
exit /b 1

:eof
endlocal
exit /b 0
