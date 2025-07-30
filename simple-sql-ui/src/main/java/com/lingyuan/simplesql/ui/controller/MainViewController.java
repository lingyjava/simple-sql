package com.lingyuan.simplesql.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class MainViewController extends BaseController {

    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab excelToSQLTab;
    @FXML
    private Tab importTableTab;

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
                } else if (newTab == importTableTab && importTableTab.getContent() == null) {
                    importTableTab.setContent(loadView("/views/ImportTableView.fxml"));
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
                        软件版本：v1.0.0
                        Copyright © 2025 LingYuan
                        希望能帮助你更轻松地处理SQL相关任务，按时下班！
                        """
        );
        alert.initOwner(mainTabPane.getScene().getWindow());
        alert.showAndWait();
    }
}
