package com.lingyuan.simplesql.common.util;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.lingyuan.simplesql.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtil {

    /**
     * 创建目录
     * @param dirPath 目录路径
     */
    public static void createDirectory(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                log.info("Directory created: {}", dirPath);
            } else {
                throw new BusinessException("Failed to create directory: " + dirPath);
            }
        } else {
            log.info("Directory already exists: {}", dirPath);
        }
    }

    /**
     * 获取应用数据根目录（跨平台）：
     * - Windows: %APPDATA%/simplesql
     * - macOS: ~/Library/Application Support/simplesql（符合 macOS 规范，避免权限问题）
     * - Linux: ~/.local/share/simplesql（遵循 XDG Base Directory 规范，适合线上部署）
     * - 其他: ~/.local/share/simplesql（若无则回退 ~/.simplesql）
     */
    public static String getAppDataDir() {
        String os = System.getProperty("os.name", "").toLowerCase();
        String home = System.getProperty("user.home");
        File baseDir;
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData == null || appData.isEmpty()) {
                baseDir = new File(home, "AppData" + File.separator + "Roaming");
            } else {
                baseDir = new File(appData);
            }
        } else if (os.contains("mac")) {
            // macOS 标准应用数据目录，符合 Apple 规范，避免权限问题
            baseDir = new File(home, "Library" + File.separator + "Application Support");
        } else {
            // Linux 及其他 Unix 系统：遵循 XDG Base Directory 规范
            String xdgDataHome = System.getenv("XDG_DATA_HOME");
            if (xdgDataHome != null && !xdgDataHome.isEmpty()) {
                baseDir = new File(xdgDataHome);
            } else {
                File localShare = new File(home, ".local" + File.separator + "share");
                if (localShare.exists() || localShare.getParentFile().exists()) {
                    baseDir = localShare;
                } else {
                    // 回退到用户主目录下的隐藏目录
                    baseDir = new File(home);
                }
            }
        }
        File appDir = new File(baseDir, "simplesql");
        createDirectory(appDir.getAbsolutePath());
        return appDir.getAbsolutePath();
    }

    /**
     * 获取默认输出文件路径（按天分组：{APP_DATA}/YYYYMMDD/HHmmss.sql）
     * @param fileName 自定义文件名
     * @return 输出文件路径
     */
    public static String getDefaultOutputFilePath(String fileName) {
        String appRoot = getAppDataDir();
        String day = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String time = new SimpleDateFormat("HHmmss").format(new Date());
        String dayDir = appRoot + File.separator + day;
        createDirectory(dayDir);
        return dayDir + File.separator + time + ";" + fileName + ".sql";
    }



    /**
     * 写入字符串到文件
     * @param content 字符串内容
     * @param outputFilePath 输出文件路径
     */
    public static void writeStringToFile(String content, String outputFilePath) {
        try (PrintWriter out = new PrintWriter(outputFilePath)) {
            out.println(content);
            log.info("Content has been written to: {}", outputFilePath);
        } catch (Exception e) {
            throw new BusinessException("Failed to write content to file: " + outputFilePath, e);
        }
    }
}
