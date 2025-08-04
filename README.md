# Simple SQL

一个简单易用的 SQL 文件生成器。

## 功能特性

### 通过 Excel 生成 SQL

- **支持多种SQL类型**：SELECT、INSERT、UPDATE、DELETE
- **智能进度显示**：生成SQL时显示进度弹窗，避免界面卡顿
- **用户友好界面**：现代化的JavaFX界面，操作简单直观
- **批量处理**：支持Excel文件批量转换为SQL语句
- **条件配置**：UPDATE和DELETE操作支持自定义条件列数

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
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar simple-sql-ui/target/simple-sql-1.1.0.jar

# Windows
java --module-path C:\path\to\javafx-sdk\lib --add-modules javafx.controls,javafx.fxml -jar simple-sql-ui\target\simple-sql-1.1.0.jar
```

#### 方法四：直接运行jar包（会失败）

```bash
java -jar simple-sql-ui/target/simple-sql-1.1.0.jar
# 结果: 错误: 缺少 JavaFX 运行时组件
```

## 使用说明

### Excel转SQL功能

1. **启动应用程序**后，选择"Excel转SQL"功能
2. **填写表名**：输入目标数据库表名
3. **选择SQL类型**：
   - SELECT：查询语句
   - INSERT：插入语句
   - UPDATE：更新语句（需设置条件列数）
   - DELETE：删除语句（需设置条件列数）
4. **上传Excel文件**：点击"上传Excel文件"按钮选择文件
5. **生成SQL**：点击"咻～～～（生成SQL）"按钮
6. **查看结果**：生成完成后可以打开文件或所在文件夹

### 进度弹窗功能

- ✅ **智能进度显示**：生成过程中会显示进度弹窗
- ✅ **防止重复操作**：生成按钮在过程中会被禁用
- ✅ **后台处理**：不会导致界面卡顿
- ✅ **操作反馈**：成功或失败都会显示相应提示

## 许可证

MIT License
