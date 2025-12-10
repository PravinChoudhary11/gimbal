package com.gimbal.commands;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ToolVersionValidator {

    private static final Set<String> JAVA_VERSIONS = new HashSet<>(Arrays.asList(
            "8", "11", "17", "21", "22", "23",
            "1.8", "latest", "lts"));

    private static final Set<String> PYTHON_VERSIONS = new HashSet<>(Arrays.asList(
            "3.8", "3.9", "3.10", "3.11", "3.12", "3.13",
            "2.7", "latest"));

    private static final Set<String> NODE_VERSIONS = new HashSet<>(Arrays.asList(
            "16", "18", "20", "22", "23",
            "16.0.0", "18.0.0", "20.0.0", "22.0.0", "23.0.0",
            "latest", "lts"));

    private static final Set<String> MAVEN_VERSIONS = new HashSet<>(Arrays.asList(
            "3.6", "3.8", "3.9", "3.10", "latest"));

    private static final Set<String> GRADLE_VERSIONS = new HashSet<>(Arrays.asList(
            "7.0", "7.5", "7.6", "8.0", "8.5", "8.6", "8.7", "8.8", "8.9", "8.10",
            "latest"));

    private static final Set<String> GO_VERSIONS = new HashSet<>(Arrays.asList(
            "1.19", "1.20", "1.21", "1.22", "1.23", "latest"));

    private static final Set<String> RUST_VERSIONS = new HashSet<>(Arrays.asList(
            "1.70", "1.75", "1.80", "1.81", "1.82", "1.83",
            "stable", "latest"));

    private static final Set<String> KOTLIN_VERSIONS = new HashSet<>(Arrays.asList(
            "1.8", "1.9", "2.0", "2.1", "latest"));

    private static final Set<String> GIT_VERSIONS = new HashSet<>(Arrays.asList(
            "2.40", "2.41", "2.42", "2.43", "2.44", "2.45", "2.46", "2.47",
            "latest"));

    private static final Set<String> DOCKER_VERSIONS = new HashSet<>(Arrays.asList(
            "24.0", "25.0", "26.0", "27.0", "latest"));

    public static boolean isValidVersionForTool(String toolName, String version) {
        if (toolName == null || version == null)
            return false;

        SupportedTool tool = SupportedTool.fromString(toolName);
        if (tool == null)
            return true;

        version = version.trim().toLowerCase();
        String majorMinor = extractMajorMinor(version);

        switch (tool) {
            case JAVA:
                return JAVA_VERSIONS.contains(version) || JAVA_VERSIONS.contains(majorMinor);
            case PYTHON:
                return PYTHON_VERSIONS.contains(version) || PYTHON_VERSIONS.contains(majorMinor);
            case NODE:
            case NPM:
                return NODE_VERSIONS.contains(version) || NODE_VERSIONS.contains(majorMinor);
            case MAVEN:
                return MAVEN_VERSIONS.contains(version) || MAVEN_VERSIONS.contains(majorMinor);
            case GRADLE:
                return GRADLE_VERSIONS.contains(version) || GRADLE_VERSIONS.contains(majorMinor);
            case GO:
                return GO_VERSIONS.contains(version) || GO_VERSIONS.contains(majorMinor);
            case RUST:
                return RUST_VERSIONS.contains(version) || RUST_VERSIONS.contains(majorMinor);
            case KOTLIN:
                return KOTLIN_VERSIONS.contains(version) || KOTLIN_VERSIONS.contains(majorMinor);
            case GIT:
                return GIT_VERSIONS.contains(version) || GIT_VERSIONS.contains(majorMinor);
            case DOCKER:
                return DOCKER_VERSIONS.contains(version) || DOCKER_VERSIONS.contains(majorMinor);
            default:
                return true;
        }
    }

    private static String extractMajorMinor(String version) {
        if (version.matches("^\\d+\\.\\d+\\.\\d+.*")) {
            String[] parts = version.split("\\.");
            return parts[0] + "." + parts[1];
        }
        if (version.matches("^\\d+\\.\\d+.*")) {
            return version.split("\\.")[0] + "." + version.split("\\.")[1];
        }
        return version;
    }

    public static String getSupportedVersionsMessage(String toolName) {
        SupportedTool tool = SupportedTool.fromString(toolName);
        if (tool == null)
            return "Unknown tool: " + toolName;

        switch (tool) {
            case JAVA:
                return "Supported Java versions: 8, 11, 17, 21, 22, 23 (or 'latest', 'lts')";
            case PYTHON:
                return "Supported Python versions: 3.8, 3.9, 3.10, 3.11, 3.12, 3.13 (or 'latest')";
            case NODE:
            case NPM:
                return "Supported Node versions: 16, 18, 20, 22, 23 (or 'latest', 'lts')";
            case MAVEN:
                return "Supported Maven versions: 3.6, 3.8, 3.9, 3.10 (or 'latest')";
            case GRADLE:
                return "Supported Gradle versions: 7.x, 8.x (or 'latest')";
            case GO:
                return "Supported Go versions: 1.19, 1.20, 1.21, 1.22, 1.23 (or 'latest')";
            case RUST:
                return "Supported Rust versions: 1.70+, 'stable', 'latest'";
            case KOTLIN:
                return "Supported Kotlin versions: 1.8, 1.9, 2.0, 2.1 (or 'latest')";
            case GIT:
                return "Supported Git versions: 2.40+ (or 'latest')";
            case DOCKER:
                return "Supported Docker versions: 24.0+ (or 'latest')";
            default:
                return "No specific version constraints for " + toolName;
        }
    }
}
