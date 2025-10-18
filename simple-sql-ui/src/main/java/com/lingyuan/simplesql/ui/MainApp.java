package com.lingyuan.simplesql.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // 加载主界面FXML
        Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/MainView.fxml"))));
        stage.setTitle("Simple SQL");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/logo/logo.ico")))); // 设置窗口图标
        stage.setScene(scene);
        stage.show();
    }
}
