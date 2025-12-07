package com.gimbal.logger;

public enum ExitCodes {
    SUCCESS(0),
    GENERAL_ERROR(1),
    PROVIDE_HELP(2),
    INVALID_USAGE(2),
    PARTIAL_COMMAND_FOUND(2),
    UNKNOWN_TOOL(3),
    DOWNLOAD_FAILED(10),
    NETWORK_ERROR(11),
    EXTRACTION_FAILED(12),
    VERIFY_FAILED(13),
    INFO_VERSION(99),
    FAILURE(2),
    PERMISSION_DENIED(14),
    DEPENDENCY_MISSING(15),
    COMMAND_NOT_FOUND(127),
    INTERRUPTED(130);

    public final int code;
    ExitCodes(int code) { this.code = code; }

    public int getcode(){ return this.code; }
}

