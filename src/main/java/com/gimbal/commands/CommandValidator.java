package com.gimbal.commands;

import com.gimbal.logger.ExitCodes;
import com.gimbal.logger.GimbalMessage;
import com.gimbal.logger.GimbalMessageType;
import com.gimbal.logger.SystemLogger;

/**
 *  This is Gimbal Command Vaidation System .
 */

public class CommandValidator {

    public static void CheckCommand(String[] command) {

        for (int i = 0; i < command.length; i++) {
            String token = command[i].toLowerCase().trim();

            // 1. INVALID FLAG CHECK
            if (token.startsWith("-")) {
                if (!isGlobalFlag(token) && !isBooleanflag(token) && !isValueFlag(token)) {
              
                    SystemLogger.LogAndExitWithDisplayError(
                        GimbalMessage.INVALID_FLAG.getMessage(),
                        GimbalMessageType.ERROR.getMessage(),
                        token,
                        ExitCodes.INVALID_FLAG.getcode()
                    );
                }
                continue;
            }

            if (isTool(token)) continue;

            if (isTask(token)) continue;

            if(!isTask(token) && !isTool(token)){
                if(!isTask(token)){
                    SystemLogger.LogAndExitWithDisplayError(
                        GimbalMessage.UNKNOWN_TASK.getMessage(),
                        GimbalMessageType.ERROR.getMessage(), 
                        token, 
                        ExitCodes.UNKNOWN_TASK.getcode()
                    );
                }
                if(!isTool(token)){
                    SystemLogger.LogAndExitWithDisplayError(
                        GimbalMessage.INVALID_TOOL.getMessage(), 
                        GimbalMessageType.ERROR.getMessage(), 
                        token, 
                        ExitCodes.UNKNOWN_TOOL.getcode()
                    );
                }
                
            }

            SystemLogger.LogAndExit(
                GimbalMessage.INVALID_TOOL.getMessage(),
                GimbalMessageType.FAILURE.getMessage(),
                ExitCodes.INVALID_USAGE.getcode()
            );
        }

    }

    private static boolean isTool(String tool) {
        if (tool == null || tool.isEmpty()) return false;

        switch (tool.toLowerCase()) {
            case "java":
            case "jdk":
            case "jre":

            case "maven":
            case "mvn":

            case "git":

            case "node":
            case "npm":
            case "npx":
            case "pnpm":
            case "yarn":

            case "python":
            case "py":

            case "docker":
            case "compose":
            case "docker-compose":

            case "gradle":

            case "go":

            case "rust":
            case "cargo":

            case "kotlin":
            case "kotlinc":

                return true;

            default:
                return false;
        }
    }

     private static boolean isBooleanflag(String flag) {

        if (flag == null || flag.isEmpty()) return false;

        flag = flag.toLowerCase().trim();

        // Value flags always contain '=' → NOT boolean
        if (flag.contains("=")) return false;

        switch (flag) {

            // Common boolean flags
            case "-y":
            case "--yes":
                return true;

            case "-f":
            case "--force":
                return true;

            case "--check":
                return true;

            case "--repair":
                return true;

            // Python / Node tool flags
            case "--pip":
                return true;

            case "--venv":
                return true;

            case "--conda":
                return true;

            case "--global":
                return true;

            // Java / node-related boolean flags
            case "--jdk":
            case "--jre":
                return true;

            case "--lts":
                return true;

            // Logging / debugging (boolean)
            case "--debug":
            case "--dbg":
                return true;

            case "--quiet":
            case "-q":
                return true;

            case "--no-color":
                return true;

            default:
                return false;
        }
    }

    private static boolean isValueFlag(String flag) {
        if (flag == null || flag.isEmpty()) return false;

        flag = flag.toLowerCase().trim();

        // Inline format → definitely a value flag
        if (flag.contains("=")){
            String name = flag.substring(0, flag.indexOf("="));

            // MUST start with "--"
            if (name.startsWith("--")) {
                flag = name.substring(2); // remove "--"
            }
        }
        
        // Normal value flags where value comes next
        switch (flag) {
            case "path":
            case "p":
            case "vendor":
            case "config":
            case "env":
            case "profile":
            case "log-file":
            case "version":
            case "jdk-version":
            case "npm-version":
            case "python-version":
                return true;

            default:
                return false;
        }
    }

    private static boolean isGlobalFlag(String flag) {
        if (flag == null || flag.isEmpty()) return false;

        switch (flag.toLowerCase()) {

            // Global logging / verbosity flags
            case "--verbose":
            case "-x":
            case "--debug":
            case "--dbg":
            case "--quiet":
            case "-q":
            case "--no-color":

            // Help & version flags
            case "--help":
            case "-h":
            case "--version":

            // Global config flags
            case "--config":
            case "--env":
            case "--profile":

            // Global log file
            case "--log-file":

                return true;

            default:
                return false;
        }
    }

    private static boolean isTask(String task) {
        if (task == null || task.isEmpty()) return false;

        switch (task.toLowerCase()) {

            case "install":
            case "i":

            case "update":
            case "u":

            case "remove":
            case "uninstall":
            case "delete":
            case "r":

            case "repair":

            case "list":
            case "ls":

            case "check":

            case "info":

                return true;

            default:
                return false;
        }
    }
}
