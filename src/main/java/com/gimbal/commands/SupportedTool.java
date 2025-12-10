package com.gimbal.commands;

public enum SupportedTool {
    JAVA("java"),
    MAVEN("maven"),
    GIT("git"),
    NODE("node"),
    NPM("npm"),
    PYTHON("python"),
    DOCKER("docker"),
    GRADLE("gradle"),
    GO("go"),
    RUST("rust"),
    KOTLIN("kotlin");

    private final String toolName;

    SupportedTool(String toolName) {
        this.toolName = toolName;
    }

    public String getToolName() {
        return toolName;
    }

    public static boolean isSupported(String tool) {
        if (tool == null)
            return false;
        tool = tool.toLowerCase();
        for (SupportedTool supportedTool : values()) {
            if (supportedTool.toolName.equals(tool)) {
                return true;
            }
        }
        return false;
    }

    public static SupportedTool fromString(String tool) {
        if (tool == null)
            return null;
        tool = tool.toLowerCase();
        for (SupportedTool supportedTool : values()) {
            if (supportedTool.toolName.equals(tool)) {
                return supportedTool;
            }
        }
        return null;
    }
}
