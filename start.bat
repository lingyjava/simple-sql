@echo off
REM Simple SQL Application Startup Script
setlocal enabledelayedexpansion

echo ========================================
echo    Simple SQL Application Startup Script
echo ========================================
echo.

REM Check Java version
echo [INFO] Checking Java version...
echo [DEBUG] About to run: java -version

REM Try to run java -version and capture the result
java -version >nul 2>&1
set JAVA_CHECK_RESULT=%errorlevel%

echo [DEBUG] Java check result: %JAVA_CHECK_RESULT%

if %JAVA_CHECK_RESULT% neq 0 (
    echo [ERROR] Java not found, please install JDK 17+
    echo Please visit: https://www.oracle.com/java/technologies/downloads/
    echo.
    echo [DEBUG] Error level: %JAVA_CHECK_RESULT%
    pause
    exit /b 1
)

echo [SUCCESS] Java version check passed
echo [DEBUG] Continuing to next step...
echo ""

REM Check if jar file exists
echo [INFO] Checking for JAR file...
set "JAR_FILE=simple-sql.jar"
echo [DEBUG] Looking for: %JAR_FILE%

if not exist "%JAR_FILE%" (
    echo [ERROR] JAR file not found: %JAR_FILE%
    echo Please ensure the JAR file is in the same directory as this script
    echo Current directory: %CD%
    echo.
    echo Directory contents:
    dir
    echo.
    echo [TIP] Download the JAR file from GitHub and place it in the same directory
    echo JAR file name should be: %JAR_FILE% (without version number)
    pause
    exit /b 1
)

echo [SUCCESS] Found JAR file: %JAR_FILE%
echo [DEBUG] JAR file check completed
echo.

REM Check JavaFX SDK
echo [INFO] Checking JavaFX SDK...
set "JAVAFX_SDK_PATH="

REM Check environment variables
echo [DEBUG] Checking environment variables...
if defined JAVAFX_HOME (
    set "JAVAFX_SDK_PATH=%JAVAFX_HOME%"
    echo [SUCCESS] Detected environment variable JAVAFX_HOME: %JAVAFX_SDK_PATH%
) else if defined JAVAFX_SDK_HOME (
    set "JAVAFX_SDK_PATH=%JAVAFX_SDK_HOME%"
    echo [SUCCESS] Detected environment variable JAVAFX_SDK_HOME: %JAVAFX_SDK_PATH%
) else (
    echo [INFO] No JavaFX environment variables found
)

REM Check common installation paths
if "%JAVAFX_SDK_PATH%"=="" (
    echo [INFO] Checking common JavaFX SDK installation paths...
    echo [DEBUG] Checking: C:\Program Files\Java\javafx-sdk
    if exist "C:\Program Files\Java\javafx-sdk" (
        set "JAVAFX_SDK_PATH=C:\Program Files\Java\javafx-sdk"
        echo [SUCCESS] Found JavaFX SDK in common path: %JAVAFX_SDK_PATH%
    ) else (
        echo [DEBUG] Not found: C:\Program Files\Java\javafx-sdk
        echo [DEBUG] Checking: C:\Program Files (x86)\Java\javafx-sdk
        if exist "C:\Program Files (x86)\Java\javafx-sdk" (
            set "JAVAFX_SDK_PATH=C:\Program Files (x86)\Java\javafx-sdk"
            echo [SUCCESS] Found JavaFX SDK in common path: %JAVAFX_SDK_PATH%
        ) else (
            echo [DEBUG] Not found: C:\Program Files (x86)\Java\javafx-sdk
            echo [DEBUG] Checking: %USERPROFILE%\javafx-sdk
            if exist "%USERPROFILE%\javafx-sdk" (
                set "JAVAFX_SDK_PATH=%USERPROFILE%\javafx-sdk"
                echo [SUCCESS] Found JavaFX SDK in common path: %JAVAFX_SDK_PATH%
            ) else (
                echo [DEBUG] Not found: %USERPROFILE%\javafx-sdk
                echo [DEBUG] Checking: C:\javafx-sdk
                if exist "C:\javafx-sdk" (
                    set "JAVAFX_SDK_PATH=C:\javafx-sdk"
                    echo [SUCCESS] Found JavaFX SDK in common path: %JAVAFX_SDK_PATH%
                ) else (
                    echo [DEBUG] Not found: C:\javafx-sdk
                    echo [INFO] No JavaFX SDK found in common paths
                )
            )
        )
    )
)

echo.
echo [INFO] Runtime environment detection completed
echo [INFO] JavaFX SDK Path: %JAVAFX_SDK_PATH%

REM If JavaFX SDK is found, start the application directly
if not "%JAVAFX_SDK_PATH%"=="" (
    echo [SUCCESS] JavaFX SDK detected, preparing to start application...
    echo JavaFX SDK path: %JAVAFX_SDK_PATH%
    echo Startup command: java --module-path "%JAVAFX_SDK_PATH%\lib" --add-modules javafx.controls,javafx.fxml -jar %JAR_FILE%
    echo.
    echo [STARTUP] Starting Simple SQL application...
    
    java --module-path "%JAVAFX_SDK_PATH%\lib" --add-modules javafx.controls,javafx.fxml -jar "%JAR_FILE%"
    set STARTUP_RESULT=%errorlevel%
    echo [DEBUG] Startup result: %STARTUP_RESULT%
    
    if %STARTUP_RESULT% neq 0 (
        echo [ERROR] Startup failed, please check JavaFX SDK configuration
        echo [ERROR] Error code: %STARTUP_RESULT%
        pause
        exit /b 1
    ) else (
        echo [SUCCESS] Application started successfully
    )
) else (
    echo [ERROR] JavaFX SDK not detected, cannot start application
    echo.
    echo [DOWNLOAD] Please visit the following link to download JavaFX SDK:
    echo    https://gluonhq.com/products/javafx/
    echo.
    echo [INSTALLATION STEPS]:
    echo    1. Download JavaFX SDK for your system
    echo    2. Extract to a specific directory (e.g., C:\javafx-sdk)
    echo    3. Set environment variable (optional):
    echo       set JAVAFX_HOME=C:\javafx-sdk
    echo    4. Run this startup script again
    echo.
    echo [TIP] After installation, run this script again to start automatically
    echo.
    echo [DEBUG] Current environment:
    echo   JAVAFX_HOME: %JAVAFX_HOME%
    echo   JAVAFX_SDK_HOME: %JAVAFX_SDK_HOME%
    echo   USERPROFILE: %USERPROFILE%
    pause
    exit /b 1
)

echo.
echo [INFO] Script execution completed
echo [DEBUG] All steps completed successfully
pause 