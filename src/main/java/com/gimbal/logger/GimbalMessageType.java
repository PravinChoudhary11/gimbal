package com.gimbal.logger;

public enum GimbalMessageType {

    INFO("INFO", "ℹ"),
    SUCCESS("SUCCESS", "✓"),
    WARNING("WARNING", "⚠"),
    ERROR("ERROR", "✗"),
    FAILURE("FAILURE", "✗"),
    DEBUG("DEBUG", "⚙"),
    PROGRESS("PROGRESS", "→"),
    SYSTEM("SYSTEM", "◆");

    private final String message;
    private final String icon;

    GimbalMessageType(String message, String icon) {
        this.message = message;
        this.icon = icon;
    }

    public String getMessage() {
        return this.message;
    }

    public String getIcon() {
        return this.icon;
    }
}
