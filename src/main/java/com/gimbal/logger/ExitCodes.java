package com.gimbal.logger;

public enum ExitCodes {
    // Success codes
    SUCCESS(0),

    // Informational codes (non-error)
    INFO_VERSION(99),
    INFO_HELP(98),
    CONTINUE(100),

    // Usage and command errors (2-9)
    GENERAL_ERROR(1),
    INVALID_USAGE(2),
    PROVIDE_HELP(2),
    PARTIAL_COMMAND_FOUND(2),
    INVALID_FLAG_VALUE(2),
    FAILURE(2),
    UNKNOWN_TOOL(3),
    UNKNOWN_TASK(4),
    INVALID_FLAG(5),
    MISSING_FLAG_VALUE(6),
    CONFLICTING_FLAGS(7),
    INVALID_ARGUMENT(8),
    MISSING_REQUIRED_ARGUMENT(9),
    UNSUPPORTED_VERSION(13),
    INVALID_VERSION(14),
    INVALID_VENDOR(15),
    INVALID_PATH(16),

    // Download and network errors (10-19)
    DOWNLOAD_FAILED(10),
    NETWORK_ERROR(11),
    EXTRACTION_FAILED(12),
    VERIFY_FAILED(13),
    PERMISSION_DENIED(14),
    DEPENDENCY_MISSING(15),
    CHECKSUM_FAILED(16),
    TIMEOUT_ERROR(17),
    CONNECTION_REFUSED(18),

    // Installation errors (20-29)
    INSTALL_FAILED(20),
    INSTALL_PATH_INVALID(21),
    INSTALL_ALREADY_EXISTS(22),
    FILE_ALREADY_EXISTS(22),
    INSTALL_SPACE_INSUFFICIENT(23),
    INSTALL_CORRUPTED(24),
    UNSUPPORTED_FEATURE(25),

    // Configuration errors (30-39)
    CONFIG_ERROR(30),
    CONFIG_NOT_FOUND(31),
    CONFIG_PARSE_ERROR(32),
    CONFIG_INVALID_VALUE(33),

    // System errors (40-49)
    SYSTEM_ERROR(40),
    UNSUPPORTED_OS(41),
    UNSUPPORTED_ARCHITECTURE(42),
    ENVIRONMENT_ERROR(43),
    PATH_ERROR(44),

    // Tool-specific errors (50-59)
    TOOL_NOT_FOUND(50),
    TOOL_VERSION_MISMATCH(51),
    TOOL_EXECUTION_FAILED(52),
    TOOL_NOT_COMPATIBLE(53),

    // Standard system codes
    COMMAND_NOT_FOUND(127),
    INTERRUPTED(130);

    private final int code;
    private final String description;

    ExitCodes(int code) {
        this.code = code;
        this.description = generateDescription();
    }

    public int getcode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    private String generateDescription() {
        String name = this.name().toLowerCase().replace('_', ' ');
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
