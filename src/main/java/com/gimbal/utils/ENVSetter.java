package com.gimbal.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.gimbal.logger.ExitCodes;
import com.gimbal.logger.GimbalMessageType;
import com.gimbal.logger.SystemLogger;

public final class ENVSetter {

    private ENVSetter() {
    }

    public static boolean setSystemEnv(String variableName, String variableValue) {
        if (variableName == null || variableName.trim().isEmpty()) {
            SystemLogger.LogAndExit(
                    "Environment variable name cannot be empty",
                    GimbalMessageType.ERROR.getMessage(),
                    ExitCodes.FAILURE.getcode());
            return false;
        }

        if (variableValue == null) {
            variableValue = "";
        }

        try {
            String registryPath = "HKLM\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment";

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "reg", "add",
                    registryPath,
                    "/v", variableName,
                    "/t", "REG_EXPAND_SZ",
                    "/d", variableValue,
                    "/f");

            processBuilder.inheritIO();
            Process process = processBuilder.start();

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                broadcastEnvChange();
                SystemLogger.SystemLoggerDisplay(
                        "Successfully set environment variable: " + variableName,
                        GimbalMessageType.SUCCESS.getMessage(),
                        ExitCodes.SUCCESS.getcode());
                return true;
            } else {
                SystemLogger.LogAndExit(
                        "Failed to set environment variable: " + variableName + " (Exit code: " + exitCode + ")",
                        GimbalMessageType.ERROR.getMessage(),
                        ExitCodes.FAILURE.getcode());
                return false;
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            SystemLogger.LogAndExit(
                    "Environment variable setting was interrupted: " + variableName,
                    GimbalMessageType.ERROR.getMessage(),
                    ExitCodes.INTERRUPTED.getcode());
            return false;
        } catch (Exception e) {
            SystemLogger.LogAndExit(
                    "Failed to set system environment variable: " + variableName + " - " + e.getMessage(),
                    GimbalMessageType.ERROR.getMessage(),
                    ExitCodes.FAILURE.getcode());
            return false;
        }
    }

    public static boolean addToSystemPath(String newPathEntry) {
        if (newPathEntry == null || newPathEntry.trim().isEmpty()) {
            SystemLogger.LogAndExit(
                    "PATH entry cannot be empty",
                    GimbalMessageType.ERROR.getMessage(),
                    ExitCodes.FAILURE.getcode());
            return false;
        }

        newPathEntry = newPathEntry.trim();

        try {
            String registryPath = "HKLM\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment";

            ProcessBuilder queryBuilder = new ProcessBuilder(
                    "reg", "query",
                    registryPath,
                    "/v", "Path");

            Process queryProcess = queryBuilder.start();
            int queryExitCode = queryProcess.waitFor();

            if (queryExitCode != 0) {
                SystemLogger.LogAndExit(
                        "Failed to read system PATH from registry",
                        GimbalMessageType.ERROR.getMessage(),
                        ExitCodes.FAILURE.getcode());
                return false;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(queryProcess.getInputStream()));

            String currentPath = "";
            String line;
            boolean pathFound = false;

            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("Path")) {
                    int regTypeIndex = line.indexOf("REG_");
                    if (regTypeIndex != -1) {
                        String pathLine = line.substring(regTypeIndex);
                        pathLine = pathLine.replace("REG_EXPAND_SZ", "").trim();
                        pathLine = pathLine.replace("REG_SZ", "").trim();
                        currentPath = pathLine;
                        pathFound = true;
                        break;
                    }
                }
            }
            reader.close();

            if (!pathFound) {
                currentPath = "";
            }

            String currentPathLower = currentPath.toLowerCase();
            String newPathLower = newPathEntry.toLowerCase();

            if (currentPathLower.contains(newPathLower)) {
                SystemLogger.SystemLoggerDisplay(
                        "System PATH already contains: " + newPathEntry,
                        GimbalMessageType.INFO.getMessage(),
                        ExitCodes.CONTINUE.getcode());
                return true;
            }

            String updatedPath;
            if (currentPath.isEmpty()) {
                updatedPath = newPathEntry;
            } else if (currentPath.endsWith(";")) {
                updatedPath = currentPath + newPathEntry;
            } else {
                updatedPath = currentPath + ";" + newPathEntry;
            }

            ProcessBuilder updateBuilder = new ProcessBuilder(
                    "reg", "add",
                    registryPath,
                    "/v", "Path",
                    "/t", "REG_EXPAND_SZ",
                    "/d", updatedPath,
                    "/f");

            Process updateProcess = updateBuilder.start();
            int updateExitCode = updateProcess.waitFor();

            if (updateExitCode == 0) {
                broadcastEnvChange();
                SystemLogger.SystemLoggerDisplay(
                        "Successfully added to system PATH: " + newPathEntry,
                        GimbalMessageType.SUCCESS.getMessage(),
                        ExitCodes.SUCCESS.getcode());
                return true;
            } else {
                SystemLogger.LogAndExit(
                        "Failed to update system PATH (Exit code: " + updateExitCode + ")",
                        GimbalMessageType.ERROR.getMessage(),
                        ExitCodes.FAILURE.getcode());
                return false;
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            SystemLogger.LogAndExit(
                    "PATH update was interrupted",
                    GimbalMessageType.ERROR.getMessage(),
                    ExitCodes.INTERRUPTED.getcode());
            return false;
        } catch (Exception e) {
            SystemLogger.LogAndExit(
                    "Failed to update system PATH: " + e.getMessage(),
                    GimbalMessageType.ERROR.getMessage(),
                    ExitCodes.FAILURE.getcode());
            return false;
        }
    }

    private static void broadcastEnvChange() {
        try {
            ProcessBuilder broadcastBuilder = new ProcessBuilder(
                    "cmd", "/c",
                    "RUNDLL32.EXE USER32.DLL,SendMessageA HWND_BROADCAST WM_SETTINGCHANGE 0 \"Environment\"");

            Process broadcastProcess = broadcastBuilder.start();
            broadcastProcess.waitFor();

            SystemLogger.SystemLoggerDisplay(
                    "Environment changes broadcasted to system",
                    GimbalMessageType.DEBUG.getMessage(),
                    ExitCodes.SUCCESS.getcode());

        } catch (Exception e) {
            SystemLogger.SystemLoggerDisplay(
                    "Warning: Failed to broadcast environment changes. Restart may be required.",
                    GimbalMessageType.WARNING.getMessage(),
                    ExitCodes.CONTINUE.getcode());
        }
    }
}
