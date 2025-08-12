<div align="center">

  <h1>Simple SQL</h1>

  <img src="./simple-sql-ui/src/main/resources/logo/logo.png" alt="Simple SQL Logo" width="100" height="100">

  <p><b>一个简单易用的 SQL 生成器，基于 JavaFx 实现的本地离线应用

</b></p>

</div>

---

## 功能特性

- **Excel转SQL - [使用手册](http://lingyuan.tech/project/simple-sql.html)**：通过 Excel 文件生成批量的 SQL 语句，支持 SELECT、INSERT、UPDATE、DELETE
- **回退脚本生成**：支持根据 INSERT 语句生成对应的回退脚本
- **表名库名管理**：支持添加、删除表名库名记录，支持搜索，支持使用选择按钮快速填入表名和库名
- **用户友好界面**：现代化的 JavaFX 界面，操作简单直观
- **本地离线应用**：可本地离线使用

## 开发&构建

- JDK 17+
- JavaFX 21+（或需与 JDK 匹配）
- Maven 3.6+

## 快速开始

### 🚀 从GitHub下载jar包运行（推荐）

#### 1. 下载jar包
从 [GitHub Releases](https://github.com/lingyjava/simple-sql/releases) 下载最新版本的jar包：
- `simple-sql.jar`

#### 2. 下载启动脚本
同时下载对应系统的启动脚本：
- **Linux/macOS**: `start.sh`
- **Windows**: `start.bat`

#### 3. 安装JavaFX SDK
由于JavaFX从Java 9开始采用模块化设计，必须安装JavaFX SDK：

**下载地址**: https://gluonhq.com/products/javafx/

**安装步骤**:
1. 下载对应系统的JavaFX SDK
2. 解压到指定目录（如: `/usr/local/javafx-sdk` 或 `C:\javafx-sdk`）
3. 可选：设置环境变量
   ```bash
   # Linux/macOS
   export JAVAFX_HOME=/usr/local/javafx-sdk
   
   # Windows
   set JAVAFX_HOME=C:\javafx-sdk
   ```

#### 4. 运行应用程序

**Linux/macOS:**
```bash
# 确保jar包和启动脚本在同一目录
chmod +x start.sh
./start.sh
```

**Windows:**
```cmd
# 确保jar包和启动脚本在同一目录
start.bat
```

**智能启动脚本特性**:
- 🔍 自动检测JavaFX SDK（支持环境变量和常见路径）
- 🎯 提供多种运行方式选择
- 📋 显示详细的运行说明
- 💡 自动指导JavaFX SDK安装


**自动扫描路径**:

环境变量:
- `JAVAFX_HOME`
- `JAVAFX_SDK_HOME` 

常见安装路径:

**Windows:**
- `C:\Program Files\Java\javafx-sdk`
- `C:\Program Files (x86)\Java\javafx-sdk` 
- `%USERPROFILE%\javafx-sdk`
- `C:\javafx-sdk`

**Linux:**
- `/opt/javafx-sdk`
- `/usr/local/javafx-sdk`
- `$HOME/javafx-sdk`

**macOS:**
- `/Library/Java/JavaVirtualMachines/javafx-sdk`
- `/usr/local/javafx-sdk`
- `$HOME/javafx-sdk`

启动脚本会自动扫描以上路径,无需手动配置。如果JavaFX SDK安装在其他位置,建议通过环境变量指定路径。

### 🔧 从源码构建运行

#### 环境要求
- JDK 17+
- Maven 3.6+

#### 构建步骤
```bash
# 1. 克隆项目
git clone <repository-url>
cd simple-sql

# 2. 构建项目
mvn clean install

# 3. 启动应用程序
mvn javafx:run
```

## 运行方式详解

### ✅ 推荐方式：使用jar包运行（需要JavaFX SDK）

```bash
# Linux/macOS
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar simple-sql.jar

# Windows
java --module-path C:\path\to\javafx-sdk\lib --add-modules javafx.controls,javafx.fxml -jar simple-sql.jar
```

### ❌ 不推荐：直接运行jar包

```bash
java -jar simple-sql.jar
# 结果: 错误: 缺少 JavaFX 运行时组件
```

**为什么不能直接运行？**
- JavaFX从Java 9开始采用模块化设计
- 需要JavaFX运行时组件，无法打包到jar中
- 必须使用模块路径或Maven插件运行

## 常见问题

### Q: 运行时提示"缺少JavaFX运行时组件"
**A**: 需要安装JavaFX SDK，详见[安装步骤](#3-安装javafx-sdk)

### Q: 如何知道JavaFX SDK安装成功？
**A**: 运行启动脚本，如果检测到JavaFX SDK，会显示"✅ 检测到JavaFX SDK"

### Q: 从源码运行需要JavaFX SDK吗？
**A**: 不需要，Maven会自动处理JavaFX依赖。

## 许可证

MIT License
