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

### Windows（推荐）：使用 EXE 运行

1. 前往 [GitHub Releases](https://github.com/lingyjava/simple-sql/releases) 下载最新的 Windows 安装包或可执行文件（例如：`simple-sql-setup.exe` 或 `simple-sql.exe`）
2. 双击运行（安装包会引导完成安装；便携版 exe 可直接运行）
3. 无需安装 JDK 或 JavaFX，开箱即用

提示（首次运行可能遇到 SmartScreen 警告）:
- 点击“更多信息” -> “仍要运行” 即可

### macOS：使用 JAR + 智能启动脚本

1. 从 [GitHub Releases](https://github.com/lingyjava/simple-sql/releases) 下载：
   - `simple-sql.jar`
   - `start.sh`
2. 安装依赖：
   - JDK 17+
   - JavaFX SDK（下载地址：`https://gluonhq.com/products/javafx/`）
3. 配置（可选，推荐设置 JavaFX 目录）：
   ```bash
   export JAVAFX_HOME=/usr/local/javafx-sdk
   ```
4. 运行：
   ```bash
   chmod +x start.sh
   ./start.sh
   ```

智能启动脚本特性（macOS）：
- 自动检测 JavaFX SDK（支持环境变量与常见路径）
- 提供详细错误提示与安装指引

常见扫描路径（macOS）:
- `/Library/Java/JavaVirtualMachines/javafx-sdk`
- `/usr/local/javafx-sdk`
- `$HOME/javafx-sdk`

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
cd simple-sql-ui
mvn javafx:run
```

## 运行方式详解

### ✅ 运行命令（macOS/Linux，仅供手动方式参考）

```bash
# macOS/Linux
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar simple-sql.jar
```

### ❌ 不推荐：直接运行jar包

```bash
java -jar simple-sql.jar
# 结果: 错误: 缺少 JavaFX 运行时组件
```

**为什么不能直接运行 JAR（macOS/Linux）？**
- JavaFX 自 Java 9 起为模块化设计
- 需要提供 JavaFX 运行时组件的模块路径
- 因此需通过模块参数或启动脚本运行

## 常见问题

### Q: macOS 运行时提示"缺少 JavaFX 运行时组件"
**A**: 需要安装 JavaFX SDK，或通过 `start.sh` 自动检测与提示。

### Q: Windows 首次运行 EXE 提示 SmartScreen 警告？
**A**: 属于常见的未签名应用提醒。点击“更多信息” -> “仍要运行” 即可。

### Q: 如何知道JavaFX SDK安装成功？
**A**: 运行启动脚本，如果检测到JavaFX SDK，会显示"✅ 检测到JavaFX SDK"

### Q: 从源码运行需要JavaFX SDK吗？
**A**: 不需要，Maven会自动处理JavaFX依赖。

## 许可证

MIT License
