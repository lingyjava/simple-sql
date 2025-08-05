package com.lingyuan.simplesql.ui.controller;

import com.lingyuan.simplesql.domain.dto.SqlGeneratorParam;
import com.lingyuan.simplesql.server.impl.SqlGeneratorFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.awt.Desktop;
import java.io.File;

/**
 * SQL回退脚本生成界面控制器
 */
public class RollbackSQLViewController extends BaseController {

    @FXML private Button selectFileBtn;
    @FXML private Label fileInfoLabel;
    @FXML private Button generateBtn;
    @FXML private Label outputPathLabel;
    @FXML private Button openFileBtn;
    @FXML private Button openFolderBtn;

    private File selectedFile;
    private File outputFile;

    @FXML
    public void initialize() {
        // 初始化界面状态
        fileInfoLabel.setText("输入文件：未选择文件");
        outputPathLabel.setText("输出路径：未生成");
        generateBtn.setDisable(true);
        openFileBtn.setDisable(true);
        openFolderBtn.setDisable(true);
        
        // 文件选择按钮事件
        selectFileBtn.setOnAction(e -> selectSqlFile());
        
        // 生成按钮事件
        generateBtn.setOnAction(e -> generateRollbackScript());
        
        // 打开文件按钮事件
        openFileBtn.setOnAction(e -> {
            if (outputFile != null && outputFile.exists()) {
                try {
                    Desktop.getDesktop().open(outputFile);
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "无法打开文件", null, ex.getMessage());
                }
            }
        });
        
        // 打开目录按钮事件
        openFolderBtn.setOnAction(e -> {
            if (outputFile != null && outputFile.exists()) {
                try {
                    Desktop.getDesktop().open(outputFile.getParentFile());
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "无法打开目录", null, ex.getMessage());
                }
            }
        });
    }

    /**
     * 选择SQL文件
     */
    private void selectSqlFile() {
        Window window = selectFileBtn.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择SQL文件");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("SQL文件", "*.sql")
        );
        
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            selectedFile = file;
            fileInfoLabel.setText("输入文件：已选择 " + file.getName() + " (" + file.length() + " 字节)");
            generateBtn.setDisable(false);
            
            // 清空之前的结果
            outputPathLabel.setText("输出路径：未生成");
            openFileBtn.setDisable(true);
            openFolderBtn.setDisable(true);
        }
    }

    /**
     * 生成回退脚本
     */
    private void generateRollbackScript() {
        try {
            if (selectedFile == null || !selectedFile.exists()) {
                showAlert(Alert.AlertType.ERROR, "文件未选择", "请先选择一个有效的SQL文件！", null);
                return;
            }

            // 创建参数
            SqlGeneratorParam param = new SqlGeneratorParam();
            param.setType("ROLLBACK");
            param.setFilePath(selectedFile.getAbsolutePath());

            // 创建进度对话框
            Dialog<Void> progressDialog = new Dialog<>();
            progressDialog.setTitle("生成回退脚本");
            progressDialog.setHeaderText("正在生成SQL回退脚本...");
            progressDialog.setResizable(false);
            
            // 设置对话框内容
            VBox content = new VBox(10);
            content.setPadding(new javafx.geometry.Insets(20));
            
            Label statusLabel = new Label("正在解析SQL文件，请稍候...");
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
                    // 在后台线程中执行回退脚本生成
                    return SqlGeneratorFactory.getGenerator(param.getType()).generate(param);
                }
            };

            // 任务成功完成时的处理
            task.setOnSucceeded(event -> {
                Platform.runLater(() -> {
                    progressDialog.close();
                    Object outputPath = task.getValue();
                    outputFile = new File(outputPath.toString());
                    
                    // 显示结果
                    outputPathLabel.setText("输出路径：" + outputFile.getAbsolutePath());
                    outputPathLabel.setTooltip(new Tooltip("输出路径：" + outputFile.getAbsolutePath()));
                    
                    openFileBtn.setDisable(false);
                    openFolderBtn.setDisable(false);
                    generateBtn.setDisable(false); // 重新启用生成按钮
                    
                    showAlert(Alert.AlertType.INFORMATION, "生成成功", 
                            "SQL回退脚本已生成！", outputFile.getAbsolutePath());
                });
            });

            // 任务失败时的处理
            task.setOnFailed(event -> {
                Platform.runLater(() -> {
                    progressDialog.close();
                    Throwable exception = task.getException();
                    generateBtn.setDisable(false); // 重新启用生成按钮
                    
                    outputPathLabel.setText("输出路径：生成失败");
                    showAlert(Alert.AlertType.ERROR, "生成失败", 
                            "SQL回退脚本生成失败！", exception.getMessage());
                });
            });

            // 启动后台任务
            new Thread(task).start();
            
            // 显示进度对话框
            progressDialog.showAndWait();

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "生成失败", 
                    "SQL回退脚本生成失败！", ex.getMessage());
        }
    }
    
    
} 