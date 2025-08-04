#!/bin/bash

# Simple SQL 智能启动脚本
# 自动选择最佳运行方式

echo "🚀 启动 Simple SQL 应用程序..."
echo ""

# 检查Java版本
echo "📋 检查Java版本..."
java --version
echo ""

# 检查是否在项目目录中
if [ ! -f "pom.xml" ]; then
    echo "❌ 错误: 请在项目根目录运行此脚本"
    echo "   当前目录: $(pwd)"
    echo "   请切换到 simple-sql-ui 目录"
    exit 1
fi

# 检查是否是多模块项目的UI模块
if [ ! -f "../pom.xml" ] || ! grep -q "simple-sql-ui" pom.xml; then
    echo "❌ 错误: 请在 simple-sql-ui 目录中运行此脚本"
    echo "   当前目录: $(pwd)"
    exit 1
fi

# 检查jar包是否存在
JAR_FILE="target/simple-sql-1.1.0.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "⚠️  警告: 找不到jar包 $JAR_FILE"
    echo "   正在构建项目..."
    mvn clean package -DskipTests
    echo ""
fi

# 检查是否有JavaFX SDK
JAVAFX_SDK_PATH=""

# 1. 首先检查环境变量
if [ -n "$JAVAFX_HOME" ]; then
    JAVAFX_SDK_PATH="$JAVAFX_HOME"
elif [ -n "$JAVAFX_SDK_HOME" ]; then
    JAVAFX_SDK_PATH="$JAVAFX_SDK_HOME"
fi

# 2. 如果环境变量未设置，检查常见安装路径
if [ -z "$JAVAFX_SDK_PATH" ]; then
    # macOS 常见路径
    for path in "/Library/Java/JavaVirtualMachines/javafx-sdk" "/usr/local/javafx-sdk" "$HOME/javafx-sdk"; do
        if [ -d "$path" ]; then
            JAVAFX_SDK_PATH="$path"
            break
        fi
    done
    
    # Linux 常见路径
    if [ -z "$JAVAFX_SDK_PATH" ]; then
        for path in "/opt/javafx-sdk" "/usr/local/javafx-sdk" "$HOME/javafx-sdk"; do
            if [ -d "$path" ]; then
                JAVAFX_SDK_PATH="$path"
                break
            fi
        done
    fi
fi

echo "🔍 检测运行环境..."

# 方式1: 使用Maven运行（推荐）
echo "✅ 方式1: 使用Maven运行（推荐）"
echo "   命令: mvn javafx:run"
echo "   优点: 自动处理JavaFX依赖，无需额外配置"
echo ""

# 方式2: 如果有JavaFX SDK，使用jar包运行
if [ -n "$JAVAFX_SDK_PATH" ]; then
    echo "✅ 方式2: 使用jar包运行（检测到JavaFX SDK）"
    echo "   JavaFX SDK路径: $JAVAFX_SDK_PATH"
    echo "   命令: java --module-path $JAVAFX_SDK_PATH/lib --add-modules javafx.controls,javafx.fxml -jar $JAR_FILE"
    echo ""
else
    echo "⚠️  方式2: 使用jar包运行（需要JavaFX SDK）"
    echo "   需要下载JavaFX SDK: https://gluonhq.com/products/javafx/"
    echo "   命令: java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar $JAR_FILE"
    echo ""
fi

# 方式3: 直接运行jar包（会失败，但用于演示）
echo "❌ 方式3: 直接运行jar包（不推荐）"
echo "   命令: java -jar $JAR_FILE"
echo "   结果: 会失败，缺少JavaFX运行时"
echo ""

echo "🎯 推荐运行方式:"
echo "   1. 开发环境: mvn javafx:run"
echo "   2. 生产环境: 安装JavaFX SDK后使用jar包运行"
echo ""

# 询问用户选择
echo "请选择运行方式:"
echo "1) 使用Maven运行（推荐）"
echo "2) 使用jar包运行（需要JavaFX SDK）"
echo "3) 直接运行jar包（会失败，仅用于演示）"
echo "4) 退出"
echo ""
read -p "请输入选择 (1-4): " choice

case $choice in
    1)
        echo "🚀 使用Maven启动应用程序..."
        echo "   注意: 正在从 simple-sql-ui 目录运行..."
        mvn javafx:run
        ;;
    2)
        if [ -n "$JAVAFX_SDK_PATH" ]; then
            echo "🚀 使用jar包启动应用程序..."
            java --module-path "$JAVAFX_SDK_PATH/lib" --add-modules javafx.controls,javafx.fxml -jar "$JAR_FILE"
        else
            echo "❌ 未检测到JavaFX SDK"
            echo "请下载并安装JavaFX SDK: https://gluonhq.com/products/javafx/"
            echo "然后重新运行此脚本"
        fi
        ;;
    3)
        echo "🚀 直接运行jar包（会失败）..."
        java -jar "$JAR_FILE"
        ;;
    4)
        echo "👋 退出"
        exit 0
        ;;
    *)
        echo "❌ 无效选择"
        exit 1
        ;;
esac 