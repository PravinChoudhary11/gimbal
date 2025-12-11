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
import com.gimbal.utils.ENVSetter;
import com.gimbal.utils.Extracter.WindowsExtractor;
import com.gimbal.utils.OSUtils;

/**
 * @author Pravin Choudhary
 * @since 1.0
 */

public class JavaInstaller {

    // spesified the base url
    private static final String TEMURIN_BASE_URL = "https://api.adoptium.net/v3/binary/latest/";
    // spesified the defualt Dir which is c:/User/UserNAME/.gimbal/java
    private static final String DEFAULT_INSTALL_DIR = System.getProperty("user.home") + File.separator + ".gimbal"
            + File.separator + "java";

    // installing the java
    public static void InstallJava(Map<String, String> valueFlags, List<String> booleanFlags) {

        // log for the system
        SystemLogger.SystemLoggerDisplay(
                GimbalMessage.INSTALL_JAVA.getMessage(),
                GimbalMessageType.INFO.getMessage(),
                ExitCodes.CONTINUE.getcode());

        // get the required details version , vendor , path , -f or --force
        String version = valueFlags.getOrDefault("version", "17");
        String vendor = valueFlags.getOrDefault("vendor", "temurin");
        String installPath = valueFlags.getOrDefault("path", DEFAULT_INSTALL_DIR);
        boolean forceInstall = booleanFlags.contains("force");

        // if user enter compelete version like 17.0.1 some thing will extract 17 as
        // version to install
        version = normalizeVersion(version);

        // log for the system
        SystemLogger.SystemLoggerDisplay(
                "Installing Java " + version + " (" + vendor + ") to " + installPath,
                GimbalMessageType.INFO.getMessage(),
                ExitCodes.CONTINUE.getcode());

        // create the Path instance which basically if path on specified by user then
        // defaultpath/jdk-version
        Path targetPath = Paths.get(installPath, "jdk-" + version);

        // if installing path exist and it is not forece install then system is already
        // have java init
        if (Files.exists(targetPath) && !forceInstall) {
            // log for that java already installed in the system
            SystemLogger.LogAndExit(
                    GimbalMessage.INSTALL_ALREADY_EXISTS.getMessage() + " - " + targetPath.toString()
                            + ". Use -f to force reinstall.",
                    GimbalMessageType.WARNING.getMessage(),
                    ExitCodes.FILE_ALREADY_EXISTS.getcode());
        }

        // now here we provide version and vendor to get the compelte url
        // will return
        // https://api.adoptium.net/v3/binary/latest/version/ga/windows/arch/jdk/hotspot/normal/eclipse

        String downloadUrl = buildDownloadUrl(vendor, version);

        // now this getFileName gives us a filename for zip file
        // OpenJDKversion-windows-arch.zip
        String fileName = getFileName(version);

        // now this is where will store the zipfile before extraction which is
        // C:\Users\UserName\AppData\Local\Temp\\gimbal-downloads
        String downloadDir = System.getProperty("java.io.tmpdir") + File.separator + "gimbal-downloads";

        File DownDir = new File(downloadDir);
        if (!DownDir.exists()) {
            boolean created = DownDir.mkdirs();
            if (created) {
                SystemLogger.SystemLoggerDisplay(
                        "Dir Created For zip",
                        GimbalMessageType.INFO.getMessage(),
                        ExitCodes.CONTINUE.getcode());
            } else {
                SystemLogger.LogAndExit(
                        GimbalMessage.FAILURE.getMessage() + " : Failed to Create Temp Dir for Download",
                        GimbalMessageType.FAILURE.getMessage(),
                        ExitCodes.FAILURE.getcode());
            }
        }

        // log on the system which will Downloading
        SystemLogger.SystemLoggerDisplay(
                "Download URL: " + downloadUrl,
                GimbalMessageType.DEBUG.getMessage(),
                ExitCodes.CONTINUE.getcode());

        // Now here will provide Download url , with where to store the downloaded file
        // then filename
        /**
         * downloadurl =
         * https://api.adoptium.net/v3/binary/latest/version/ga/windows/arch/jdk/hotspot/normal/eclipse
         * downloadDir = C:\Users\UserName\AppData\Local\Temp\\gimbal-downloads
         * filename = OpenJDKversion-windows-arch.zip
         */
        if (!DownloadUtils.downloadFile(downloadUrl, downloadDir, fileName)) {
            SystemLogger.LogAndExit(
                    GimbalMessage.INSTALL_FAILED.getMessage(),
                    GimbalMessageType.ERROR.getMessage(),
                    ExitCodes.DOWNLOAD_FAILED.getcode());
        }

        // if download success then will get downloaded file at
        // C:\Users\UserName\AppData\Local\Temp\\gimbal-downloads\OpenJDK17-windows-x64.zip
        String downloadedFile = downloadDir + File.separator + fileName;



        // now here system will extract the
        // C:\Users\UserName\AppData\Local\Temp\\gimbal-downloads\OpenJDK17-windows-x64.zip
        // at installPath if user spesify the path system will extract it at that place
        // else at c:\User\UserName\.gimbal\java
        if (!WindowsExtractor.extract(downloadedFile, installPath)) {
            // if extraction fail then system will show log and it will exit the program
            SystemLogger.LogAndExit(
                    GimbalMessage.INSTALL_FAILED.getMessage(),
                    GimbalMessageType.ERROR.getMessage(),
                    ExitCodes.EXTRACTION_FAILED.getcode());
        }

        // after extraction if everything go fine then system will delete
        // C:\Users\UserName\AppData\Local\Temp\\gimbal-downloads\OpenJDK17-windows-x64.zip
        DownloadUtils.deleteFile(downloadedFile);

        // now here we store the location of JAVA_HOME path default
        // c:\User\UserName\.gimbal\java this else user provided
        // findJavaHome from this will get the path to Java.exe
        String JavaHome = findJavaHome(installPath);
        String JavabinValue = "%JAVA_HOME%\\bin";
        ENVSetter.setSystemEnv("JAVA_HOME", JavaHome);
        ENVSetter.addToSystemPath(JavaHome + File.separator + "bin");

        // After setting the Path we have now installed the java in the system
        SystemLogger.SystemLoggerDisplay(
                GimbalMessage.INSTALL_COMPLETED.getMessage(),
                GimbalMessageType.SUCCESS.getMessage(),
                ExitCodes.CONTINUE.getcode());

        // telling the user that where java is installed
        SystemLogger.SystemLoggerDisplay(
                "Java installed at: " + JavaHome,
                GimbalMessageType.SUCCESS.getMessage(),
                ExitCodes.CONTINUE.getcode());

        SystemLogger.SystemLoggerDisplay(
                GimbalMessage.INSTALL_COMPLETED.getMessage() + "  : Java Environment Value in PATH Configured as : " + JavabinValue,
                GimbalMessageType.SUCCESS.getMessage(),
                ExitCodes.CONTINUE.getcode());


        // telling the user that what Value is Provided to JAVA_HOME
        SystemLogger.SystemLoggerDisplay(
                "Set JAVA_HOME: " + JavaHome,
                GimbalMessageType.INFO.getMessage(),
                ExitCodes.SUCCESS.getcode());
    }

    // we build the url
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

    // creating the downlod file name
    private static String getFileName(String version) {
        String os = OSUtils.getOSType();
        String arch = OSUtils.getArch();
        return "OpenJDK" + version + "-" + os + "-" + arch + ".zip";
    }

    // normalizing the version for robust install
    private static String normalizeVersion(String version) {
        if (version.equals("lts"))
            return "21";
        if (version.equals("latest"))
            return "23";
        if (version.startsWith("1.8"))
            return "8";
        return version;
    }

    // now here we find the Path to Java.exe
    private static String findJavaHome(String installPath) {
        File installpath = new File(installPath);

        if (!installpath.exists() || !installpath.isDirectory()) {
            return installPath;
        }

        File[] files = installpath.listFiles();
        if (files == null) {
            return installPath;
        }
        for (File file : files) {

            if (file.isDirectory()) {
                File javaexe = new File(file, "bin\\java.exe");
                if (javaexe.exists()) {
                    return javaexe.getParentFile().getParentFile().getAbsolutePath();
                }
            }
        }

        return installPath;
    }
}
