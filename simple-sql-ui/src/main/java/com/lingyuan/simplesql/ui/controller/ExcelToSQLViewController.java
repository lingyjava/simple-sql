package com.lingyuan.simplesql.ui.controller;

import com.lingyuan.simplesql.domain.dto.SqlGeneratorParam;
import com.lingyuan.simplesql.domain.enums.SQLTypeEnum;
import com.lingyuan.simplesql.server.impl.SqlGeneratorFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.TextField;

import java.awt.Desktop;
import java.io.File;

public class ExcelToSQLViewController extends BaseController {

    @FXML private Button uploadBtn;
    @FXML private Label fileLabel;
    @FXML private Button generateBtn;
    @FXML private Label outputPathLabel;
    @FXML private Button openFolderBtn;
    @FXML private Button openFileBtn;
    @FXML private TextField tableNameField;
    @FXML private ComboBox<String> sqlTypeComboBox;
    @FXML private TextField conditionColumnCountField;
    @FXML private TextField databaseNameField;

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
                param.setFilePath(selectedFile.getAbsolutePath());
                param.setTableName(tableNameField.getText().trim());
                param.setDatabaseName(databaseNameField.getText().trim());
                param.setSqlType(sqlTypeComboBox.getValue());
                param.setType("EXCEL_TO_SQL");

                if (SQLTypeEnum.UPDATE.getName().equalsIgnoreCase(sqlTypeComboBox.getValue()) || SQLTypeEnum.DELETE.getName().equalsIgnoreCase(sqlTypeComboBox.getValue())) {
                    // 如果是 UPDATE 或 DELETE 类型，条件列数不能为空
                    if (conditionColumnCountField.getText().trim().isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "条件列数未填写", "请填写条件列数！", null);
                        return;
                    } else {
                        param.setWhereColumnCount(Integer.parseInt(conditionColumnCountField.getText().trim()));
                    }
                }

                // 创建自定义进度对话框
                Dialog<Void> progressDialog = new Dialog<>();
                progressDialog.setTitle("生成SQL");
                progressDialog.setHeaderText("正在生成SQL文件...");
                progressDialog.setResizable(false);
                
                // 设置对话框内容
                VBox content = new VBox(10);
                content.setPadding(new javafx.geometry.Insets(20));
                
                Label statusLabel = new Label("正在处理Excel数据，请稍候...");
                ProgressBar progressBar = new ProgressBar();
                progressBar.setProgress(-1); // 不确定进度
                progressBar.setPrefWidth(300);
                
                content.getChildren().addAll(statusLabel, progressBar);
                progressDialog.getDialogPane().setContent(content);
                
                // 添加取消按钮
                ButtonType cancelButtonType = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
                progressDialog.getDialogPane().getButtonTypes().add(cancelButtonType);
                
                // 禁用生成按钮，防止重复点击
                generateBtn.setDisable(true);

                // 创建后台任务
                Task<Object> task = new Task<Object>() {
                    @Override
                    protected Object call() throws Exception {
                        // 在后台线程中执行SQL生成
                        return SqlGeneratorFactory.getGenerator(param.getType()).generate(param);
                    }
                };

                // 任务成功完成时的处理
                task.setOnSucceeded(event -> {
                    Platform.runLater(() -> {
                        progressDialog.close();
                        Object outputPath = task.getValue();
                        outputFile = new File(outputPath.toString());
                        outputPathLabel.setText("输出路径：" + outputFile.getAbsolutePath());
                        outputPathLabel.setTooltip(new Tooltip("输出路径：" + outputFile.getAbsolutePath()));
                        openFolderBtn.setDisable(false);
                        openFileBtn.setDisable(false);
                        generateBtn.setDisable(false); // 重新启用生成按钮
                        showAlert(Alert.AlertType.INFORMATION, "生成成功", "SQL文件已生成！", outputFile.getAbsolutePath());
                    });
                });

                // 任务失败时的处理
                task.setOnFailed(event -> {
                    Platform.runLater(() -> {
                        progressDialog.close();
                        Throwable exception = task.getException();
                        generateBtn.setDisable(false); // 重新启用生成按钮
                        showAlert(Alert.AlertType.ERROR, "生成失败", "SQL文件生成失败！", exception.getMessage());
                    });
                });

                // 启动后台任务
                new Thread(task).start();
                
                // 显示进度对话框
                progressDialog.showAndWait();

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

}
