# Simple SQL

<div align="center">
  <img src="./simple-sql-ui/src/main/resources/logo/logo.png" alt="Simple SQL Logo" width="100" height="100">

  <p><b>一个简单易用的 SQL 脚本生成器</b></p>
</div>

## 功能特性

### 通过 Excel 生成 SQL

- **支持多种SQL类型**：SELECT、INSERT、UPDATE、DELETE
- **智能进度显示**：生成 SQL 时显示进度弹窗，避免界面卡顿
- **用户友好界面**：现代化的 JavaFX 界面，操作简单直观
- **批量处理**：支持 Excel 文件批量转换为 SQL 语句

### 回退SQL脚本生成

- **回退脚本生成**：支持根据 INSERT 语句生成对应的回退脚本
- **批量生成**：支持批量生成多条 SQL 语句的回退脚本
- **智能解析**：自动解析 SQL 语句中的表名、条件等信息

### 表名库名管理

- **数据管理**：支持添加、删除表名库名记录
- **数据持久化**：使用 SQLite 数据库存储表名和库名信息
- **搜索功能**：支持按表名和库名搜索历史记录
- **快速选择**：在 Excel 转 SQL 时可通过选择按钮快速填入表名和库名

## 开发&构建

- JDK 17+
- JavaFX 21+（或需与 JDK 匹配）
- Maven 3.6+

## 快速开始

### 源代码运行

```bash
# 1. 克隆项目
git clone <repository-url>
cd simple-sql

# 2. 构建项目
mvn clean install

# 3. 启动应用程序
cd simple-sql-ui
mvn javafx:run
```

### jar包运行

#### ⚠️ 重要说明

**JavaFX应用程序无法通过普通jar包直接运行**，因为：

- JavaFX从Java 9开始采用模块化设计
- 需要JavaFX运行时组件，无法打包到jar中
- 必须使用模块路径或Maven插件运行

#### 方法一：使用智能启动脚本（推荐）

**Linux/macOS:**

```bash
# 1. 构建项目
mvn clean package

# 2. 运行智能启动脚本
cd simple-sql-ui
./start.sh
```

**Windows:**

```cmd
# 1. 构建项目
mvn clean package

# 2. 运行智能启动脚本
cd simple-sql-ui
start.bat
```

**智能启动脚本特性：**

- 🔍 自动检测JavaFX SDK（支持环境变量和常见路径）
- 🎯 提供多种运行方式选择
- 📋 显示详细的运行说明
- ⚡ 自动构建项目（如果jar包不存在）
- 📃 [JavaFX SDK 环境变量设置](./simple-sql-ui/JAVAFX_SETUP.md)

#### 方法二：使用Maven运行（开发推荐）

```bash
cd simple-sql-ui
mvn javafx:run
```

#### 方法三：手动运行jar包（需要JavaFX SDK）

1. 下载并安装 [JavaFX SDK](https://gluonhq.com/products/javafx/)
2. 构建项目：`mvn clean package`
3. 运行jar包：

```bash
# Linux/macOS
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar simple-sql-ui/target/simple-sql-1.2.0.jar

# Windows
java --module-path C:\path\to\javafx-sdk\lib --add-modules javafx.controls,javafx.fxml -jar simple-sql-ui\target\simple-sql-1.2.0.jar
```

#### 方法四：直接运行jar包（会失败）

```bash
java -jar simple-sql-ui/target/simple-sql-1.2.0.jar
# 结果: 错误: 缺少 JavaFX 运行时组件
```

## 许可证

MIT License
