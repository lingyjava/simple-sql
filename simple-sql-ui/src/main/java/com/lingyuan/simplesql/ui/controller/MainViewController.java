package com.lingyuan.simplesql.ui.controller;

import com.lingyuan.simplesql.common.util.FileUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Slf4j
public class MainViewController extends BaseController {

    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab excelToSQLTab;
    @FXML
    private Tab tableDatabaseTab;
    @FXML
    private Tab sqlRollbackTab;

    @FXML
    public void initialize() {
        // 默认选中第一个 Tab
        mainTabPane.getSelectionModel().select(excelToSQLTab);

        // 加载第一个 Tab 内容
        if (excelToSQLTab.getContent() == null) {
            try {
                excelToSQLTab.setContent(loadView("/views/ExcelToSQLView.fxml"));
            } catch (IOException e) {
                log.error("Error loading view: {}", excelToSQLTab, e);
                throw new RuntimeException(e);
            }
        }

        // 监听 Tab 切换，动态加载内容
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            try {
                if (newTab == excelToSQLTab && excelToSQLTab.getContent() == null) {
                    excelToSQLTab.setContent(loadView("/views/ExcelToSQLView.fxml"));
                } else if (newTab == tableDatabaseTab && tableDatabaseTab.getContent() == null) {
                    tableDatabaseTab.setContent(loadView("/views/TableDictionaryView.fxml"));
                } else if (newTab == sqlRollbackTab && sqlRollbackTab.getContent() == null) {
                    sqlRollbackTab.setContent(loadView("/views/RollbackSQLView.fxml"));
                }
            } catch (IOException e) {
                log.error("Error loading view: {}", newTab, e);
                throw new RuntimeException(e);
            }
        });

        // 禁用关闭按钮
        for (Tab tab : mainTabPane.getTabs()) {
            tab.setClosable(false);
        }
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }

    @FXML
    private void openGithub() throws IOException, URISyntaxException {
        Desktop.getDesktop().browse(new URI("https://github.com/lingyjava/simple-sql"));
    }

    @FXML
    private void openManual() throws IOException, URISyntaxException {
        Desktop.getDesktop().browse(new URI("http://lingyuan.tech/project/simple-sql.html"));
    }

    @FXML
    private void openSubmitBug() throws IOException, URISyntaxException {
        Desktop.getDesktop().browse(new URI("https://github.com/lingyjava/simple-sql/issues"));
    }

    @FXML
    private void openAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("关于");
        alert.setHeaderText("Simple SQL");
        alert.setContentText(
                """
                        开发作者：LingYuan
                        软件版本：v1.2.0
                        Copyright © 2025 LingYuan
                        希望能帮助你更轻松地处理SQL相关任务，按时下班！
                        """
        );
        alert.initOwner(mainTabPane.getScene().getWindow());
        alert.showAndWait();
    }

    @FXML
    private void clearCache() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("确认清除缓存");
        confirm.setHeaderText(null);
        confirm.setContentText("确定要清除所有缓存文件吗？此操作不可恢复。");
        confirm.initOwner(mainTabPane.getScene().getWindow());

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return; // 用户取消
        }

        // 使用统一的应用数据目录，递归清理其中的 .sql 文件（保留 .db）
        String cacheDirPath = FileUtil.getAppDataDir();
        File cacheDir = new File(cacheDirPath);
        int deletedCount = deleteSqlFilesRecursively(cacheDir);
        // 删除清空后的日期子目录等，但保留根应用目录
        deleteEmptyDirsRecursively(cacheDir, true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("清除缓存");
        alert.setHeaderText(null);
        alert.setContentText("已清除 " + deletedCount + " 个缓存文件。");
        alert.initOwner(mainTabPane.getScene().getWindow());
        alert.showAndWait();
    }

    /**
     * 递归删除目录下的 .sql 文件，跳过 .db 等非 .sql 文件
     */
    private int deleteSqlFilesRecursively(File dir) {
        if (dir == null || !dir.exists()) {
            return 0;
        }
        int count = 0;
        if (dir.isFile()) {
            String nameLower = dir.getName().toLowerCase();
            if (nameLower.endsWith(".sql") && !nameLower.endsWith(".db")) {
                if (dir.delete()) {
                    return 1;
                }
            }
            return 0;
        }
        File[] children = dir.listFiles();
        if (children == null) {
            return 0;
        }
        for (File child : children) {
            count += deleteSqlFilesRecursively(child);
        }
        return count;
    }

    /**
     * 递归删除空目录
     * @param dir  目标目录
     * @param isRoot 是否为根目录（根目录不删除）
     */
    private void deleteEmptyDirsRecursively(File dir, boolean isRoot) {
        if (dir == null || !dir.isDirectory()) {
            return;
        }
        File[] children = dir.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            if (child.isDirectory()) {
                deleteEmptyDirsRecursively(child, false);
            }
        }
        // 再次检查目录是否为空，且不是根目录则删除
        File[] after = dir.listFiles();
        if (!isRoot && (after == null || after.length == 0)) {
            dir.delete();
        }
    }
}
