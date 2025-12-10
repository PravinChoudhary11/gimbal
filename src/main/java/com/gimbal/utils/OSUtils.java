package com.gimbal.utils;

public class OSUtils {

    public static boolean isWindows() {
        return true;
    }

    public static String getOS() {
        return System.getProperty("os.name");
    }

    public static String getArch() {
        String arch = System.getProperty("os.arch").toLowerCase();
        if (arch.contains("amd64") || arch.contains("x86_64")) {
            return "x64";
        } else if (arch.contains("x86")) {
            return "x86";
        }
        return "x64";
    }

    public static String getOSType() {
        return "windows";
    }
}
