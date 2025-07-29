package com.lingyuan.simplesql.ui.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

public abstract class BaseController {

    Parent loadView(String fxmlPath) throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
    }
}
