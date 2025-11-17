<div align="center">

  <h1>Simple SQL</h1>

  <img src="./simple-sql-ui/src/main/resources/logo/logo.png" alt="Simple SQL Logo" width="160" height="120">

  <p><b>一个简单易用的 SQL 生成器，本地离线应用

</b></p>

</div>

---

## 功能特性

- **Excel转SQL - [使用手册](http://lingyuan.tech/project/simple-sql.html)**：通过 Excel 文件生成批量的 SQL 语句，支持 SELECT、INSERT、UPDATE、DELETE
- **回退脚本生成**：支持根据 INSERT 语句生成对应的回退脚本
- **表库字典管理**：支持添加、删除表名库名记录，支持搜索，支持快速填入表名和库名
- **用户友好界面**：支持现代化的 JavaFX 界面和 WEB 页面，操作简单直观
- **本地离线应用**：可本地离线使用，数据安全

## 代码结构

**simple-sql-web (web模块，建议使用)**：基于 Thymeleaf，使用浏览器访问，可多人同时操作。

**simple-sql-ui (ui模块)**：基于 JavaFX 开发的本地桌面应用，无需浏览器，直接运行于桌面。

## 开发&构建

- JDK 17+
- JavaFX 21+（ui模块）
- Maven 3.6+

## 快速开始

### 🔧 从源码构建运行

```bash
# 1. 克隆项目
git clone <repository-url>
cd simple-sql

# 2. 构建项目
mvn clean install

# 3. 启动 web 模块
cd simple-sql-web
mvn spring-boot:run
# 3.1 或启动 ui 模块
cd simple-sql-ui
mvn javafx:run
```

### ui 模块 jar 运行命令详解（手动方式）

```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar simple-sql.jar
```

## 许可证

MIT License
