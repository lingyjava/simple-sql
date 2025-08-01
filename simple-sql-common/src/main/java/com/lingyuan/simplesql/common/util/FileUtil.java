package com.lingyuan.simplesql.common.util;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

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
     * 获取默认输出文件路径（当前目录下output-prefix-年月日时分秒-6位随机数.sql）
     * @param prefix 前缀
     * @return 输出文件路径
     */
    public static String getDefaultOutputFilePath(String prefix) {
        String dir = System.getProperty("user.dir") + File.separator + "output";
        createDirectory(dir);
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int rand = 100000 + new Random().nextInt(900000);
        return dir + File.separator + prefix + time + "-" + rand + ".sql";
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
