package com.lingyuan.simplesql.ui.controller;

import com.lingyuan.simplesql.domain.dto.SqlGeneratorParam;
import com.lingyuan.simplesql.domain.enums.SQLTypeEnum;
import com.lingyuan.simplesql.server.impl.SqlGeneratorFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.TextField;

import java.awt.Desktop;
import java.io.File;

public class ExcelToSQLViewController {

    @FXML private Button uploadBtn;
    @FXML private Label fileLabel;
    @FXML private Button generateBtn;
    @FXML private Label outputPathLabel;
    @FXML private Button openFolderBtn;
    @FXML private Button openFileBtn;
    @FXML private TextField tableNameField;
    @FXML private ComboBox<String> sqlTypeComboBox;
    @FXML private TextField conditionColumnCountField;

    private File selectedFile;
    private File outputFile;

    @FXML
    public void initialize() {
        generateBtn.setDisable(true);
        openFolderBtn.setDisable(true);
        openFileBtn.setDisable(true);
        conditionColumnCountField.setDisable(true);
        sqlTypeComboBox.getSelectionModel().selectFirst(); // 默认选中第一个类型

        // SQL类型下拉框事件处理
        sqlTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // 如果是 UPDATE 或 DELETE 类型，启用条件列数输入框
                if (SQLTypeEnum.UPDATE.getName().equalsIgnoreCase(newVal) || SQLTypeEnum.DELETE.getName().equalsIgnoreCase(newVal)) {
                    conditionColumnCountField.setDisable(false);
                } else {
                    conditionColumnCountField.setDisable(true);
                    conditionColumnCountField.clear();
                }
            }
        });

        // 上传按钮事件处理
        uploadBtn.setOnAction(e -> {
            Window window = uploadBtn.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择Excel文件");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel文件", "*.xls", "*.xlsx")
            );
            File file = fileChooser.showOpenDialog(window);
            if (file != null) {
                selectedFile = file;
                fileLabel.setText("已选择: " + file.getAbsolutePath());
                generateBtn.setDisable(false);
            }
        });

        // 生成按钮事件处理
        generateBtn.setOnAction(e -> {
            try {
                if (selectedFile == null || !selectedFile.exists()) {
                    showAlert(Alert.AlertType.ERROR, "文件未选择", "请先选择一个有效的Excel文件！", null);
                    return;
                }
                if (tableNameField.getText().trim().isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "表名未填写", "请填写表名！", null);
                    return;
                }

                SqlGeneratorParam param = new SqlGeneratorParam();
                param.setExcelPath(selectedFile.getAbsolutePath());
                param.setTableName(tableNameField.getText().trim());
                param.setSqlType(sqlTypeComboBox.getValue());
                param.setType("EXCEL");

                if (SQLTypeEnum.UPDATE.getName().equalsIgnoreCase(sqlTypeComboBox.getValue()) || SQLTypeEnum.DELETE.getName().equalsIgnoreCase(sqlTypeComboBox.getValue())) {
                    // 如果是 UPDATE 或 DELETE 类型，条件列数不能为空
                    if (conditionColumnCountField.getText().trim().isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "条件列数未填写", "请填写条件列数！", null);
                        return;
                    } else {
                        param.setWhereColumnCount(Integer.parseInt(conditionColumnCountField.getText().trim()));
                    }
                }

                Object outputPath = SqlGeneratorFactory.getGenerator(param.getType()).generate(param);

                outputFile = new File(outputPath.toString());
                outputPathLabel.setText("输出路径：" + outputFile.getAbsolutePath());
                outputPathLabel.setTooltip(new Tooltip("输出路径：" + outputFile.getAbsolutePath()));
                openFolderBtn.setDisable(false);
                openFileBtn.setDisable(false);
                showAlert(Alert.AlertType.INFORMATION, "生成成功", "SQL文件已生成！", outputFile.getAbsolutePath());
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "生成失败", "SQL文件生成失败！", ex.getMessage());
            }
        });

        // 打开所在文件夹事件处理
        openFolderBtn.setOnAction(e -> {
            if (outputFile != null && outputFile.exists()) {
                try {
                    Desktop.getDesktop().open(outputFile.getParentFile());
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "无法打开文件夹", null, ex.getMessage());
                }
            }
        });

        // 打开文件按钮事件处理
        openFileBtn.setOnAction(e -> {
            if (outputFile != null && outputFile.exists()) {
                try {
                    Desktop.getDesktop().open(outputFile);
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "无法打开文件", null, ex.getMessage());
                }
            }
        });

        // 只允许条件列数输入数字
        conditionColumnCountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                conditionColumnCountField.setText(newVal.replaceAll("\\D", ""));
            }
        });
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

}
