@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

echo Starting Simple SQL Application...
echo.

REM Check if in project directory
if not exist "pom.xml" (
    echo Error: Please run this script in the project root directory
    echo Current directory: %CD%
    echo Please switch to simple-sql-ui directory
    pause
    exit /b 1
)

REM Check Java version
echo Checking Java version...
java --version
echo.

REM Check if jar file exists
set "JAR_FILE=target\simple-sql-1.2.0.jar"
if not exist "%JAR_FILE%" (
    echo Warning: JAR file not found %JAR_FILE%
    echo Building project...
    mvn clean package -DskipTests
    echo.
)

REM Check for JavaFX SDK
set "JAVAFX_SDK_PATH="

REM Check environment variables
if defined JAVAFX_HOME (
    set "JAVAFX_SDK_PATH=%JAVAFX_HOME%"
) else if defined JAVAFX_SDK_HOME (
    set "JAVAFX_SDK_PATH=%JAVAFX_SDK_HOME%"
)

REM Check common installation paths
if "%JAVAFX_SDK_PATH%"=="" (
    if exist "C:\Program Files\Java\javafx-sdk" (
        set "JAVAFX_SDK_PATH=C:\Program Files\Java\javafx-sdk"
    ) else if exist "C:\Program Files (x86)\Java\javafx-sdk" (
        set "JAVAFX_SDK_PATH=C:\Program Files (x86)\Java\javafx-sdk"
    ) else if exist "%USERPROFILE%\javafx-sdk" (
        set "JAVAFX_SDK_PATH=%USERPROFILE%\javafx-sdk"
    ) else if exist "C:\javafx-sdk" (
        set "JAVAFX_SDK_PATH=C:\javafx-sdk"
    )
)

echo Detecting runtime environment...
echo.

echo Option 1: Run with Maven (Recommended)
echo Command: mvn javafx:run
echo Advantage: Automatically handles JavaFX dependencies
echo.

if not "%JAVAFX_SDK_PATH%"=="" (
    echo Option 2: Run with JAR file (JavaFX SDK detected)
    echo JavaFX SDK Path: %JAVAFX_SDK_PATH%
    echo Command: java --module-path "%JAVAFX_SDK_PATH%\lib" --add-modules javafx.controls,javafx.fxml -jar "%JAR_FILE%"
    echo.
) else (
    echo Option 2: Run with JAR file (JavaFX SDK required)
    echo Download JavaFX SDK: https://gluonhq.com/products/javafx/
    echo Command: java --module-path C:\path\to\javafx-sdk\lib --add-modules javafx.controls,javafx.fxml -jar "%JAR_FILE%"
    echo.
)

echo Option 3: Direct JAR run (Not recommended)
echo Command: java -jar "%JAR_FILE%"
echo Result: Will fail, missing JavaFX runtime
echo.

echo Recommended run methods:
echo 1. Development: mvn javafx:run
echo 2. Production: Install JavaFX SDK then use JAR file
echo.

echo Please select run method:
echo 1) Run with Maven (Recommended)
echo 2) Run with JAR file (JavaFX SDK required)
echo 3) Direct JAR run (Will fail, demo only)
echo 4) Exit
echo.

set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" (
    echo Starting application with Maven...
    mvn javafx:run
    goto :end
)

if "%choice%"=="2" (
    if not "%JAVAFX_SDK_PATH%"=="" (
        echo Starting application with JAR file...
        java --module-path "%JAVAFX_SDK_PATH%\lib" --add-modules javafx.controls,javafx.fxml -jar "%JAR_FILE%"
    ) else (
        echo JavaFX SDK not detected
        echo Please download and install JavaFX SDK: https://gluonhq.com/products/javafx/
        echo Then run this script again
        pause
    )
    goto :end
)

if "%choice%"=="3" (
    echo Running JAR file directly (will fail)...
    java -jar "%JAR_FILE%"
    goto :end
)

if "%choice%"=="4" (
    echo Exiting
    exit /b 0
)

echo Invalid choice
exit /b 1

:end
pause 