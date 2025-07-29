package com.lingyuan.simplesql.ui;

import com.lingyuan.simplesql.server.SqlGenerator;
import com.lingyuan.simplesql.server.impl.SqlGeneratorExcel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.Map;
import java.util.Objects;

public class MainApp extends Application {

    private File selectedFile;
    private final SqlGenerator sqlGenerator = new SqlGeneratorExcel();
    private File outputFile;

    // @Override
    public void start1(Stage primaryStage) {
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

    // @Override
    public void start2(Stage primaryStage) {
        // 创建 TabPane 用于多功能页面切换
        TabPane tabPane = new TabPane();
        // 设置标签页不可关闭（不显示关闭按钮）
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // 功能1：Excel转SQL
        // 垂直布局，间距10
        VBox excelPage = new VBox(10); // 垂直布局，间距10
        excelPage.setPadding(new Insets(20)); // 页面内边距
        excelPage.getChildren().add(new Label("Excel转SQL页面")); // 页面标题
        excelPage.getChildren().add(new Button("上传Excel")); // 上传按钮
        Tab tab1 = new Tab("Excel转SQL", excelPage); // 创建标签页

        // 功能2：表单生成SELECT页面
        VBox selectPage = new VBox(10);
        selectPage.setPadding(new Insets(20));
        selectPage.getChildren().add(new Label("表单生成SELECT页面"));
        selectPage.getChildren().add(new TextField("输入表名")); // 输入表名
        selectPage.getChildren().add(new Button("生成SELECT")); // 生成按钮
        Tab tab2 = new Tab("表单生成SELECT", selectPage);

        // 功能3：建表语句导入页面
        VBox importPage = new VBox(10);
        importPage.setPadding(new Insets(20));
        importPage.getChildren().add(new Label("建表语句导入页面"));
        importPage.getChildren().add(new TextArea("粘贴建表SQL")); // 粘贴建表语句
        importPage.getChildren().add(new Button("导入表结构")); // 导入按钮
        Tab tab3 = new Tab("建表导入", importPage);

        // 将所有标签页添加到 TabPane
        tabPane.getTabs().addAll(tab1, tab2, tab3);

        // 创建场景，设置窗口大小
        Scene scene = new Scene(tabPane, 600, 400);
        primaryStage.setTitle("Simple SQL 多功能页面"); // 设置窗口标题
        primaryStage.setScene(scene); // 设置场景
        primaryStage.show(); // 显示窗口
    }

    @Override
    public void start(Stage stage) throws Exception {
        // 加载主界面FXML
        Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/MainView.fxml"))));
        stage.setTitle("Simple SQL 多功能页面");
        stage.setScene(scene);
        stage.show();
    }
}
