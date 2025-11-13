package com.lingyuan.simplesql.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;


@SpringBootApplication
@ComponentScan(basePackages = "com.lingyuan.simplesql.web")
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
        // 启动后自动打开浏览器访问主页
        openHomeInBrowser("http://localhost:8091");
    }

    private static void openHomeInBrowser(String url) {
        // 首选使用 Desktop API（在非 headless 且支持 BROWSE 时生效）
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return;
            }
        } catch (IOException | URISyntaxException ignored) {}

        // 跨平台回退方案：mac -> open；win -> rundll32；linux -> xdg-open
        String os = System.getProperty("os.name", "").toLowerCase();
        try {
            if (os.contains("mac")) {
                new ProcessBuilder("open", url).start();
            } else if (os.contains("win")) {
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
            } else {
                new ProcessBuilder("xdg-open", url).start();
            }
        } catch (Exception ignored) {}
    }
}