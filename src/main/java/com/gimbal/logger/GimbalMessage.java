package com.gimbal.logger;

public enum GimbalMessage {

    // General system messages
    SUCCESS("Operation completed successfully"),
    FAILURE("Operation failed"),
    SYSTEM_INITIALIZED("Gimbal system initialized and ready"),
    SYSTEM_RUNNING("Gimbal is running"),


    // Command validation messages
    ERROR_INVALID_COMMAND("Invalid command syntax. Please check your input"),
    MISSING_COMMAND("Incomplete command detected. Tool name or additional arguments required"),
    INVALID_TOOL("The specified tool is not recognized or supported by Gimbal"),
    UNKNOWN_TASK("The specified task is not recognized. Use 'install', 'update', 'remove', 'list', etc."),
    FLAG_NOT_SUPPORTED("The specified flag is not supported for this command"),
    INVALID_FLAG_VALUE("Invalid value provided for flag"),
    MISSING_FLAG_VALUE("Flag requires a value but none was provided"),
    CONFLICTING_FLAGS("Conflicting flags detected in command"),
    INCOMPLETE_COMMAND_FOUND("No task or tools provided. Command is incomplete."),
    INVALID_FLAG("One or more flags are invalid or malformed."),

    // Help and version messages
    PROVIDE_HELP("Displaying help information"),
    PROVIDE_VERSION("Displaying Gimbal version information"),

    // Global flag messages
    GLOBAL_FLAG_IN_BETWEEN("Warning: Global flags should be placed at the beginning of the command. Flags placed between tools will be ignored"),
    GLOBAL_FLAG_PROCESSED("Global flag processed successfully"),

    // Installation messages
    INSTALL_STARTED("Installation process initiated"),
    INSTALL_IN_PROGRESS("Installation in progress, please wait..."),
    INSTALL_COMPLETED("Installation completed successfully"),
    INSTALL_FAILED("Installation failed. Please check the error details above"),
    INSTALL_ALREADY_EXISTS("Tool already exists at the specified location"),
    INSTALL_CANCELLED("Installation cancelled by user"),

    // Tool-specific installation messages
    INSTALL_JAVA("Initializing Java JDK installation"),
    INSTALL_MAVEN("Initializing Apache Maven installation"),
    INSTALL_GIT("Initializing Git installation"),
    INSTALL_PYTHON("Initializing Python installation"),
    INSTALL_NODE("Initializing Node.js installation"),
    INSTALL_DOCKER("Initializing Docker installation"),
    INSTALL_GRADLE("Initializing Gradle installation"),

    // Download messages
    DOWNLOAD_STARTED("Download started"),
    DOWNLOAD_IN_PROGRESS("Downloading..."),
    DOWNLOAD_COMPLETED("Download completed successfully"),
    DOWNLOAD_FAILED("Download failed. Please check your network connection"),
    DOWNLOAD_RETRY("Retrying download"),

    // Verification messages
    VERIFY_STARTED("Verifying downloaded files"),
    VERIFY_COMPLETED("Verification completed successfully"),
    VERIFY_FAILED("Verification failed. File may be corrupted"),
    CHECKSUM_MISMATCH("Checksum verification failed. Downloaded file may be corrupted"),

    // Extraction messages
    EXTRACT_STARTED("Extracting files"),
    EXTRACT_IN_PROGRESS("Extraction in progress..."),
    EXTRACT_COMPLETED("Extraction completed successfully"),
    EXTRACT_FAILED("Extraction failed. Archive may be corrupted"),

    // Network and connectivity messages
    NETWORK_ERROR("Network error detected. Please check your internet connection"),
    CONNECTION_TIMEOUT("Connection timeout. Please check your network settings"),
    CONNECTION_REFUSED("Connection refused by remote server"),

    // Permission and access messages
    PERMISSION_DENIED("Permission denied. Please run Gimbal with administrator/root privileges"),
    PATH_NOT_ACCESSIBLE("The specified path is not accessible"),
    PATH_INVALID("Invalid path specified"),
    INSUFFICIENT_SPACE("Insufficient disk space for installation"),

    // Configuration messages
    CONFIG_LOADED("Configuration loaded successfully"),
    CONFIG_ERROR("Configuration error detected"),
    CONFIG_NOT_FOUND("Configuration file not found. Using default settings"),

    // Update messages
    UPDATE_AVAILABLE("Update available for the specified tool"),
    UPDATE_STARTED("Update process initiated"),
    UPDATE_COMPLETED("Update completed successfully"),
    UPDATE_FAILED("Update failed"),

    // Removal messages
    REMOVE_STARTED("Removal process initiated"),
    REMOVE_COMPLETED("Removal completed successfully"),
    REMOVE_FAILED("Removal failed"),
    TOOL_NOT_FOUND("The specified tool was not found on the system"),

    // List and check messages
    LIST_TOOLS("Listing installed tools"),
    CHECK_SYSTEM("Checking system configuration"),
    CHECK_COMPLETED("System check completed"),

    // Error messages
    UNKNOWN_ERROR("An unexpected error occurred"),
    SYSTEM_ERROR("System error encountered"),
    UNSUPPORTED_OS("This operation is not supported on your operating system"),
    UNSUPPORTED_ARCHITECTURE("This tool is not available for your system architecture"),
    DEPENDENCY_MISSING("Required dependency is missing"),

    // Cleanup messages
    CLEANUP_STARTED("Cleanup process initiated"),
    CLEANUP_COMPLETED("Cleanup completed successfully"),

    // Validation messages
    VALIDATING_COMMAND("Validating command structure"),
    VALIDATION_SUCCESS("Command validation successful"),
    VALIDATION_FAILED("Command validation failed");

    private final String message;

    GimbalMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
