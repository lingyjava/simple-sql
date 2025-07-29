package com.lingyuan.simplesql.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class MainViewController extends BaseController {

    @FXML
    private StackPane contentPane;

    @FXML
    public void showView(String path) throws IOException {
        try {
            Node view = loadView(path);
            contentPane.getChildren().setAll(view); // 切换页面
        } catch (Exception e) {
            log.error("Error loading view: {}", path, e);
            throw new IOException("Failed to load view: " + path, e);
        }
    }

    @FXML
    public void showExcelToSQLView(ActionEvent event) throws IOException {
        showView("/views/ExcelToSQLView.fxml");
    }
    @FXML
    public void showImportTableView(ActionEvent event) throws IOException {
        showView("/views/ImportTableView.fxml");
    }
}
