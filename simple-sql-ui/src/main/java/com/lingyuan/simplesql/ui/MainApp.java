package com.lingyuan.simplesql.ui;

import com.lingyuan.simplesql.server.SqlGenerator;
import com.lingyuan.simplesql.server.impl.SqlGeneratorExcel;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.Map;

public class MainApp extends Application {

    private File selectedFile;
    private final SqlGenerator sqlGenerator = new SqlGeneratorExcel();
    private File outputFile;

    @Override
    public void start(Stage primaryStage) {
        Button uploadBtn = new Button("上传Excel文件");
        Label fileLabel = new Label("未选择文件");
        Button generateBtn = new Button("咻～～～（生成SQL）");
        generateBtn.setDisable(true);

        Label outputPathLabel = new Label("输出路径：未生成");
        outputPathLabel.setWrapText(true);
        outputPathLabel.setTooltip(new Tooltip("输出路径：未生成"));

        Button openFolderBtn = new Button("打开所在文件夹");
        Button openFileBtn = new Button("打开文件");
        openFolderBtn.setDisable(true);
        openFileBtn.setDisable(true);

        uploadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择Excel文件");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Excel文件", "*.xls", "*.xlsx")
            );
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedFile = file;
                fileLabel.setText("已选择: " + file.getAbsolutePath());
                generateBtn.setDisable(false);
            }
        });

        generateBtn.setOnAction(e -> {
            try {
                Map<String, Object> params = Map.of("inputFilePath", selectedFile.getAbsolutePath());
                Object outputPath = sqlGenerator.generateSql(params);
                outputFile = new File(outputPath.toString());

                outputPathLabel.setText("输出路径：" + outputFile.getAbsolutePath());
                outputPathLabel.setTooltip(new Tooltip("输出路径：" + outputFile.getAbsolutePath()));
                openFolderBtn.setDisable(false);
                openFileBtn.setDisable(false);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("生成成功");
                alert.setHeaderText("SQL文件已生成！");
                alert.setContentText("文件路径: " + outputFile.getAbsolutePath());
                alert.getDialogPane().setExpandableContent(new Label(outputFile.getAbsolutePath()));
                alert.showAndWait();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("生成失败");
                alert.setHeaderText("SQL文件生成失败！");
                alert.setContentText("错误原因: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        openFolderBtn.setOnAction(e -> {
            if (outputFile != null && outputFile.exists()) {
                try {
                    Desktop.getDesktop().open(outputFile.getParentFile());
                } catch (Exception ex) {
                    showError("无法打开文件夹", ex.getMessage());
                }
            }
        });

        openFileBtn.setOnAction(e -> {
            if (outputFile != null && outputFile.exists()) {
                try {
                    Desktop.getDesktop().open(outputFile);
                } catch (Exception ex) {
                    showError("无法打开文件", ex.getMessage());
                }
            }
        });

        // 第一行：只显示输出路径
        HBox pathBox = new HBox(10, outputPathLabel);
        pathBox.setPrefWidth(500); // 可根据需要调整宽度

        // 第二行：按钮
        HBox btnBox = new HBox(10, openFolderBtn, openFileBtn);

        VBox root = new VBox(15, uploadBtn, fileLabel, generateBtn, pathBox, btnBox);
        root.setPadding(new Insets(30));
        root.setPrefWidth(600);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Simple SQL - Excel转SQL生成器");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
