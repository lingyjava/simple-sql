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
     * - macOS: ~/Documents/simplesql（Finder 显示为“文稿”）
     * - 其他: ~/Documents/simplesql（若无 Documents 则回退 ~）
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
            baseDir = new File(home, "Documents"); // 本地化名称“文稿”，实际路径为 Documents
        } else {
            File docs = new File(home, "Documents");
            baseDir = docs.exists() ? docs : new File(home);
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
        return dayDir + File.separator + time + fileName + ".sql";
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
