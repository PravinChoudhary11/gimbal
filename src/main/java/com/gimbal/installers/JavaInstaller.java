package com.gimbal.installers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import com.gimbal.logger.ExitCodes;
import com.gimbal.logger.GimbalMessage;
import com.gimbal.logger.GimbalMessageType;
import com.gimbal.logger.SystemLogger;
import com.gimbal.utils.DownloadUtils;
import com.gimbal.utils.Extracter.WindowsExtractor;
import com.gimbal.utils.OSUtils;

public class JavaInstaller {

    private static final String TEMURIN_BASE_URL = "https://api.adoptium.net/v3/binary/latest/";
    private static final String DEFAULT_INSTALL_DIR = System.getProperty("user.home") + File.separator + ".gimbal"
            + File.separator + "java";

    public static void InstallJava(Map<String, String> valueFlags, List<String> booleanFlags) {

        SystemLogger.SystemLoggerDisplay(
                GimbalMessage.INSTALL_JAVA.getMessage(),
                GimbalMessageType.INFO.getMessage(),
                ExitCodes.CONTINUE.getcode());

        String version = valueFlags.getOrDefault("version", "17");
        String vendor = valueFlags.getOrDefault("vendor", "temurin");
        String installPath = valueFlags.getOrDefault("path", DEFAULT_INSTALL_DIR);
        boolean forceInstall = booleanFlags.contains("force");

        version = normalizeVersion(version);

        SystemLogger.SystemLoggerDisplay(
                "Installing Java " + version + " (" + vendor + ") to " + installPath,
                GimbalMessageType.INFO.getMessage(),
                ExitCodes.CONTINUE.getcode());

        Path targetPath = Paths.get(installPath, "jdk-" + version);
        if (Files.exists(targetPath) && !forceInstall) {
            SystemLogger.LogAndExit(
                    GimbalMessage.INSTALL_ALREADY_EXISTS.getMessage() + " - " + targetPath.toString()
                            + ". Use -f to force reinstall.",
                    GimbalMessageType.WARNING.getMessage(),
                    ExitCodes.FILE_ALREADY_EXISTS.getcode());
        }

        String downloadUrl = buildDownloadUrl(vendor, version);
        String fileName = getFileName(version);
        String downloadDir = System.getProperty("java.io.tmpdir") + File.separator + "gimbal-downloads";

        SystemLogger.SystemLoggerDisplay(
                "Download URL: " + downloadUrl,
                GimbalMessageType.DEBUG.getMessage(),
                ExitCodes.CONTINUE.getcode());

        if (!DownloadUtils.downloadFile(downloadUrl, downloadDir, fileName)) {
            SystemLogger.LogAndExit(
                    GimbalMessage.INSTALL_FAILED.getMessage(),
                    GimbalMessageType.ERROR.getMessage(),
                    ExitCodes.DOWNLOAD_FAILED.getcode());
        }

        String downloadedFile = downloadDir + File.separator + fileName;

        if (!WindowsExtractor.extract(downloadedFile, installPath)) {
            SystemLogger.LogAndExit(
                    GimbalMessage.INSTALL_FAILED.getMessage(),
                    GimbalMessageType.ERROR.getMessage(),
                    ExitCodes.EXTRACTION_FAILED.getcode());
        }

        DownloadUtils.deleteFile(downloadedFile);

        String javaHome = findJavaHome(installPath);

        SystemLogger.SystemLoggerDisplay(
                GimbalMessage.INSTALL_COMPLETED.getMessage(),
                GimbalMessageType.SUCCESS.getMessage(),
                ExitCodes.SUCCESS.getcode());

        SystemLogger.SystemLoggerDisplay(
                "Java installed at: " + javaHome,
                GimbalMessageType.SUCCESS.getMessage(),
                ExitCodes.SUCCESS.getcode());

        SystemLogger.SystemLoggerDisplay(
                "Set JAVA_HOME: " + javaHome,
                GimbalMessageType.INFO.getMessage(),
                ExitCodes.SUCCESS.getcode());
    }

    private static String buildDownloadUrl(String vendor, String version) {
        String os = OSUtils.getOSType();
        String arch = OSUtils.getArch();
        String imageType = "jdk";

        if (vendor.equalsIgnoreCase("temurin")) {
            return TEMURIN_BASE_URL + version + "/ga/" + os + "/" + arch + "/" + imageType + "/hotspot/normal/eclipse";
        }

        SystemLogger.LogAndExit(
                "Vendor '" + vendor + "' is not yet supported. Currently only 'temurin' is available.",
                GimbalMessageType.ERROR.getMessage(),
                ExitCodes.UNSUPPORTED_FEATURE.getcode());
        return null;
    }

    private static String getFileName(String version) {
        String os = OSUtils.getOSType();
        String arch = OSUtils.getArch();
        return "OpenJDK" + version + "-" + os + "-" + arch + ".zip";
    }

    private static String normalizeVersion(String version) {
        if (version.equals("lts"))
            return "21";
        if (version.equals("latest"))
            return "23";
        if (version.startsWith("1.8"))
            return "8";
        return version;
    }

    private static String findJavaHome(String installPath) {
        try {
            File installDir = new File(installPath);
            File[] files = installDir.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && file.getName().startsWith("jdk")) {
                        return file.getAbsolutePath();
                    }
                }
            }

            return installPath;
        } catch (Exception e) {
            return installPath;
        }
    }
}
