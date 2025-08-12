#!/bin/bash

# Simple SQL 智能启动脚本
# 专门针对从GitHub下载jar包的情况
# jar包应与脚本在同一目录下

echo "========================================"
echo "   Simple SQL 应用程序启动脚本"
echo "========================================"
echo ""

# 检查Java版本
echo "[信息] 检查Java版本..."
if ! command -v java >/dev/null 2>&1; then
    echo "[错误] 未找到Java，请先安装JDK 17+"
    echo "请访问: https://adoptium.net/ 或 https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi

# 检查Java版本是否满足要求
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "[错误] Java版本过低，当前版本: $JAVA_VERSION，需要JDK 17+"
    echo "请升级到JDK 17或更高版本"
    exit 1
fi

echo "[成功] Java版本检查通过: $(java -version 2>&1 | head -n 1)"
echo ""

# 检查jar包是否存在
JAR_FILE="simple-sql.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "[错误] 找不到jar包 $JAR_FILE"
    echo "请确保jar包与启动脚本在同一目录下"
    echo "当前目录: $(pwd)"
    echo ""
    echo "目录内容:"
    ls -la
    echo ""
    echo "[提示] 从GitHub下载的jar包应该放在与启动脚本相同的目录中"
    echo "jar包名称应该是: $JAR_FILE（不包含版本号）"
    exit 1
fi

echo "[成功] 找到jar包: $JAR_FILE"
echo ""

# 检查是否有JavaFX SDK
JAVAFX_SDK_PATH=""

# 1. 首先检查环境变量
if [ -n "$JAVAFX_HOME" ]; then
    JAVAFX_SDK_PATH="$JAVAFX_HOME"
    echo "[成功] 检测到环境变量 JAVAFX_HOME: $JAVAFX_SDK_PATH"
elif [ -n "$JAVAFX_SDK_HOME" ]; then
    JAVAFX_SDK_PATH="$JAVAFX_SDK_HOME"
    echo "[成功] 检测到环境变量 JAVAFX_SDK_HOME: $JAVAFX_SDK_PATH"
fi

# 2. 如果环境变量未设置，检查常见安装路径
if [ -z "$JAVAFX_SDK_PATH" ]; then
    echo "[信息] 检查常见JavaFX SDK安装路径..."
    
    # macOS 常见路径
    for path in "/Library/Java/JavaVirtualMachines/javafx-sdk" "/usr/local/javafx-sdk" "$HOME/javafx-sdk"; do
        if [ -d "$path" ]; then
            JAVAFX_SDK_PATH="$path"
            echo "[成功] 在常见路径找到JavaFX SDK: $JAVAFX_SDK_PATH"
            break
        fi
    done
    
    # Linux 常见路径
    if [ -z "$JAVAFX_SDK_PATH" ]; then
        for path in "/opt/javafx-sdk" "/usr/local/javafx-sdk" "$HOME/javafx-sdk"; do
            if [ -d "$path" ]; then
                JAVAFX_SDK_PATH="$path"
                echo "[成功] 在常见路径找到JavaFX SDK: $JAVAFX_SDK_PATH"
                break
            fi
        done
    fi
fi

echo ""
echo "[信息] 检测运行环境..."

# 如果有JavaFX SDK，直接启动应用程序
if [ -n "$JAVAFX_SDK_PATH" ]; then
    echo "[成功] 检测到JavaFX SDK，准备启动应用程序..."
    echo "JavaFX SDK路径: $JAVAFX_SDK_PATH"
    echo "启动命令: java --module-path $JAVAFX_SDK_PATH/lib --add-modules javafx.controls,javafx.fxml -jar $JAR_FILE"
    echo ""
    echo "[启动] 正在启动 Simple SQL 应用程序..."
    
    if java --module-path "$JAVAFX_SDK_PATH/lib" --add-modules javafx.controls,javafx.fxml -jar "$JAR_FILE"; then
        echo "[成功] 应用程序已启动"
    else
        echo "[错误] 启动失败，请检查JavaFX SDK配置"
        exit 1
    fi
else
    echo "[错误] 未检测到JavaFX SDK，无法启动应用程序"
    echo ""
    echo "[下载] 请访问以下链接下载JavaFX SDK:"
    echo "   https://gluonhq.com/products/javafx/"
    echo ""
    echo "[安装步骤]:"
    echo "   1. 下载适合您系统的JavaFX SDK"
    echo "   2. 解压到指定目录（如: /usr/local/javafx-sdk）"
    echo "   3. 设置环境变量（可选）:"
    echo "      export JAVAFX_HOME=/usr/local/javafx-sdk"
    echo "   4. 重新运行此启动脚本"
    echo ""
    echo "[提示] 安装完成后，重新运行此脚本即可自动启动"
    exit 1
fi 