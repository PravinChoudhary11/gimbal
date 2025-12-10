package com.gimbal.commands;

import java.util.List;
import java.util.Map;

import com.gimbal.logger.ExitCodes;
import com.gimbal.logger.GimbalMessage;
import com.gimbal.logger.GimbalMessageType;
import com.gimbal.logger.SystemLogger;

public class FlagsValidator {

    public static void validate(List<Tools> toolsAndFlags) {
        for (Tools tool : toolsAndFlags) {
            Map<String, String> valueFlags = tool.getValueFlags();

            for (Map.Entry<String, String> flag : valueFlags.entrySet()) {
                String flagName = flag.getKey();
                String flagValue = flag.getValue();

                ValidationResult result = validateKeyValue(flagName, flagValue, tool.ToolName);

                if (!result.isValid) {
                    SystemLogger.LogAndExitWithDisplayError(
                            result.errorMessage,
                            GimbalMessageType.ERROR.getMessage(),
                            flagValue,
                            result.exitCode);
                }
            }
        }
    }

    private static ValidationResult validateKeyValue(String flagName, String flagValue, String toolName) {
        switch (flagName) {
            case "path":
                if (!isValidPath(flagValue)) {
                    return ValidationResult.error(
                            GimbalMessage.INVALID_PATH.getMessage(),
                            ExitCodes.INVALID_PATH.getcode());
                }
                return ValidationResult.success();

            case "version":
                if (!isValidVersion(flagValue)) {
                    return ValidationResult.error(
                            GimbalMessage.INVALID_VERSION_FORMAT.getMessage(),
                            ExitCodes.INVALID_VERSION.getcode());
                }

                if (!ToolVersionValidator.isValidVersionForTool(toolName, flagValue)) {
                    String supportedVersions = ToolVersionValidator.getSupportedVersionsMessage(toolName);
                    return ValidationResult.error(
                            GimbalMessage.UNSUPPORTED_VERSION.getMessage() + ". " + supportedVersions,
                            ExitCodes.UNSUPPORTED_VERSION.getcode());
                }
                return ValidationResult.success();

            case "vendor":
                if (!isValidVendor(flagValue, toolName)) {
                    return ValidationResult.error(
                            GimbalMessage.INVALID_VENDOR.getMessage(),
                            ExitCodes.INVALID_VENDOR.getcode());
                }
                return ValidationResult.success();

            case "env":
                if (!isValidEnv(flagValue)) {
                    return ValidationResult.error(
                            GimbalMessage.INVALID_ENV.getMessage(),
                            ExitCodes.INVALID_FLAG_VALUE.getcode());
                }
                return ValidationResult.success();

            case "profile":
                if (!isValidProfile(flagValue)) {
                    return ValidationResult.error(
                            GimbalMessage.INVALID_PROFILE.getMessage(),
                            ExitCodes.INVALID_FLAG_VALUE.getcode());
                }
                return ValidationResult.success();

            case "log-file":
                if (!isValidLogFile(flagValue)) {
                    return ValidationResult.error(
                            GimbalMessage.INVALID_LOG_FILE.getMessage(),
                            ExitCodes.INVALID_FLAG_VALUE.getcode());
                }
                return ValidationResult.success();

            case "config":
                if (!isValidConfig(flagValue)) {
                    return ValidationResult.error(
                            GimbalMessage.INVALID_CONFIG.getMessage(),
                            ExitCodes.INVALID_FLAG_VALUE.getcode());
                }
                return ValidationResult.success();

            default:
                return ValidationResult.success();
        }
    }

    private static boolean isValidVendor(String vendor, String toolName) {
        vendor = vendor.toLowerCase();

        switch (toolName) {
            case "java":
                return vendor.equals("temurin") || vendor.equals("oracle") ||
                        vendor.equals("corretto") || vendor.equals("openjdk") ||
                        vendor.equals("adoptium");

            case "python":
                return vendor.equals("cpython") || vendor.equals("anaconda") ||
                        vendor.equals("miniconda");

            case "node":
                return vendor.equals("nodejs") || vendor.equals("nvm") ||
                        vendor.equals("pnpm") || vendor.equals("yarn");

            default:
                return true;
        }
    }

    private static boolean isValidPath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }

        path = removeQuotes(path);

        boolean isWindowsPath = path.matches("^[a-zA-Z]:[/\\\\](?:[^:*?\"<>|\\r\\n]+[/\\\\])*[^:*?\"<>|\\r\\n]*$");
        boolean isUnixPath = path.matches("^/.*$");

        return isWindowsPath || isUnixPath;
    }

    private static boolean isValidVersion(String version) {
        if (version == null || version.isEmpty()) {
            return false;
        }

        version = removeQuotes(version.trim());

        boolean isSemanticVersion = version.matches("^\\d+(\\.\\d+)?(\\.\\d+)?(-[a-zA-Z0-9.-]+)?(\\+[a-zA-Z0-9.-]+)?$");
        boolean isKeyword = version.toLowerCase().matches("^(latest|lts|stable|current)$");

        return isSemanticVersion || isKeyword;
    }

    private static boolean isValidEnv(String env) {
        if (env == null || env.isEmpty()) {
            return false;
        }

        env = removeQuotes(env.trim());
        return env.matches("^[a-zA-Z0-9_-]+$");
    }

    private static boolean isValidProfile(String profile) {
        if (profile == null || profile.isEmpty()) {
            return false;
        }

        profile = removeQuotes(profile.trim());
        return profile.matches("^[a-zA-Z0-9._-]+$");
    }

    private static boolean isValidLogFile(String logFile) {
        if (logFile == null || logFile.isEmpty()) {
            return false;
        }

        logFile = removeQuotes(logFile.trim());

        boolean isWindowsLog = logFile
                .matches("^[a-zA-Z]:\\\\(?:[^\\\\:*?\"<>|\\r\\n]+\\\\)*[^\\\\:*?\"<>|\\r\\n]+\\.(log|txt|out)$");
        boolean isUnixLog = logFile.matches("^/[^\\0]+\\.(log|txt|out)$");
        boolean isRelativeLog = logFile.matches("^(?!.*[\\\\/:*?\"<>|]).*\\.(log|txt|out)$");

        return isWindowsLog || isUnixLog || isRelativeLog;
    }

    private static boolean isValidConfig(String config) {
        if (config == null || config.isEmpty()) {
            return false;
        }

        config = removeQuotes(config.trim());

        boolean isConfigName = config.matches("^[a-zA-Z0-9._-]+$");
        boolean isConfigFile = config.matches(".*\\.(json|yaml|yml|xml|properties|toml|conf)$");

        return isConfigName || isConfigFile;
    }

    private static String removeQuotes(String text) {
        boolean hasDoubleQuotes = text.startsWith("\"") && text.endsWith("\"");
        boolean hasSingleQuotes = text.startsWith("'") && text.endsWith("'");

        if (hasDoubleQuotes || hasSingleQuotes) {
            return text.substring(1, text.length() - 1);
        }

        return text;
    }

    private static class ValidationResult {
        final boolean isValid;
        final String errorMessage;
        final int exitCode;

        private ValidationResult(boolean isValid, String errorMessage, int exitCode) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
            this.exitCode = exitCode;
        }

        static ValidationResult success() {
            return new ValidationResult(true, null, 0);
        }

        static ValidationResult error(String message, int code) {
            return new ValidationResult(false, message, code);
        }
    }
}
