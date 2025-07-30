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
        stage.setTitle("\uD835\uDC7A\uD835\uDC8A\uD835\uDC8E\uD835\uDC91\uD835\uDC8D\uD835\uDC86 \uD835\uDC7A\uD835\uDC78\uD835\uDC73");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/logo/logo.png")))); // 设置窗口图标
        stage.setScene(scene);
        stage.show();
    }
}
