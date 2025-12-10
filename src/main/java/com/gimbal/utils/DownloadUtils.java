package com.gimbal.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.gimbal.logger.ExitCodes;
import com.gimbal.logger.GimbalMessage;
import com.gimbal.logger.GimbalMessageType;
import com.gimbal.logger.SystemLogger;

public class DownloadUtils {

    public static boolean downloadFile(String fileURL, String saveDir, String fileName) {
        try {
            URL url = new URL(fileURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Gimbal-CLI/1.0");

            int responseCode = connection.getResponseCode();
            boolean isSuccess = (responseCode == HttpURLConnection.HTTP_OK);

            if (isSuccess) {
                long fileSize = connection.getContentLengthLong();
                String fileSizeFormatted = formatFileSize(fileSize);

                SystemLogger.SystemLoggerDisplay(
                        GimbalMessage.DOWNLOAD_STARTED.getMessage() + " - " + fileName + " (" + fileSizeFormatted + ")",
                        GimbalMessageType.INFO.getMessage(),
                        ExitCodes.CONTINUE.getcode());

                Path savePath = Paths.get(saveDir);
                if (!Files.exists(savePath)) {
                    Files.createDirectories(savePath);
                }

                String saveFilePath = saveDir + File.separator + fileName;

                try (InputStream inputStream = connection.getInputStream();
                        FileOutputStream outputStream = new FileOutputStream(saveFilePath)) {

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalBytesRead = 0;
                    int lastProgress = 0;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        int currentProgress = (int) ((totalBytesRead * 100) / fileSize);
                        if (currentProgress >= lastProgress + 10) {
                            System.out.print("\rDownload progress: " + currentProgress + "%");
                            lastProgress = currentProgress;
                        }
                    }
                    System.out.println("\rDownload progress: 100%");
                }

                SystemLogger.SystemLoggerDisplay(
                        GimbalMessage.DOWNLOAD_COMPLETED.getMessage() + " - " + saveFilePath,
                        GimbalMessageType.SUCCESS.getMessage(),
                        ExitCodes.SUCCESS.getcode());

                connection.disconnect();
                return true;
            } else {
                SystemLogger.LogAndExit(
                        GimbalMessage.DOWNLOAD_FAILED.getMessage() + " - HTTP " + responseCode,
                        GimbalMessageType.ERROR.getMessage(),
                        ExitCodes.DOWNLOAD_FAILED.getcode());
            }
        } catch (IOException e) {
            SystemLogger.LogAndExit(
                    GimbalMessage.DOWNLOAD_FAILED.getMessage() + " - " + e.getMessage(),
                    GimbalMessageType.ERROR.getMessage(),
                    ExitCodes.DOWNLOAD_FAILED.getcode());
        }
        return false;
    }


    private static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        }

        if (sizeInBytes < 1024 * 1024) {
            double sizeInKB = sizeInBytes / 1024.0;
            return String.format("%.2f KB", sizeInKB);
        }

        if (sizeInBytes < 1024 * 1024 * 1024) {
            double sizeInMB = sizeInBytes / (1024.0 * 1024.0);
            return String.format("%.2f MB", sizeInMB);
        }

        double sizeInGB = sizeInBytes / (1024.0 * 1024.0 * 1024.0);
        return String.format("%.2f GB", sizeInGB);
    }

    public static boolean deleteFile(String filePath) {
        try {
            boolean deleted = Files.deleteIfExists(Paths.get(filePath));
            return deleted;
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + e.getMessage());
            return false;
        }
    }
}
