package com.gimbal.logger;

import com.gimbal.GimbalCLI;
import com.gimbal.commands.ToolVersionValidator;
import com.gimbal.commands.GlobalflagsRunner.GlobalflagsConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SystemLogger {

    private static final String SYSTEM_NAME = "Gimbal";
    private static final boolean ENABLE_COLOR = !System.getProperty("os.name").toLowerCase().contains("win") ||
            System.getenv("WT_SESSION") != null ||
            System.getenv("TERM_PROGRAM") != null;
    
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ANSI Color codes
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String GRAY = "\u001B[90m";
    private static final String BOLD = "\u001B[1m";



    public static void SystemLoggerDisplay(String message, String type, int exitcode) {
        printFormattedLog(message, type, exitcode, false);

        if (exitcode != ExitCodes.CONTINUE.getcode()) {
            System.exit(exitcode);
        }
    }

    
    public static void LogAndExitWithDisplayError(String message, String type, String argu , int exitcode){

        if(message == GimbalMessage.INVALID_FLAG.getMessage()){
            System.out.println("[ "+SYSTEM_NAME+" ]"+" [ "+type+" ] "+message+" : INVALID_FLAG = "+argu);
            printFormattedLog(message, type, exitcode, ENABLE_COLOR);
            printSupportedFlags();
        }
        if(exitcode == ExitCodes.UNKNOWN_TOOL.getcode()){
            System.out.println("[ "+SYSTEM_NAME+" ]"+" [ "+type+" ] "+message+" : UNKNOWN_TOOL = "+argu);
            printFormattedLog(message, type, exitcode, ENABLE_COLOR);
            printSupportedTools();
        }
        if(exitcode == ExitCodes.UNKNOWN_TASK.getcode()){
            System.out.println("[ "+SYSTEM_NAME+" ]"+" [ "+type+" ] "+message+" : UNKNOWN_TASK = "+argu);
            printFormattedLog(message, type, exitcode, ENABLE_COLOR);
            printSupportedTask();
            
        }
        if(exitcode == ExitCodes.MISSING_FLAG_VALUE.getcode()){
            System.out.println("[ "+SYSTEM_NAME+" ]"+" [ "+type+" ] "+message+" : FLAG_VALUE_MISSING =  "+argu);
            printFormattedLog(message, type, exitcode, ENABLE_COLOR);
            printValueFlagSyntax();
        }
        if(exitcode == ExitCodes.INVALID_FLAG_VALUE.getcode()){
            System.out.println("[ "+SYSTEM_NAME+" ]"+" [ "+type+" ] "+message+" :");
            System.out.println(" INVALID_FLAG_VALUE_FOUND =   ----->  "+argu.substring(0,argu.indexOf(":")) + "  <--------");
            String toolname = argu.substring(argu.indexOf(":")+1);
            System.out.println(ToolVersionValidator.getSupportedVersionsMessage(toolname));
            System.out.println("                                                        ");
            System.out.println("                                                        ");
            System.out.println("                                                        ");
            printFormattedLog(message, type, exitcode, ENABLE_COLOR);
        }
        System.exit(exitcode);

    }
    public static void LogAndExit(String message, String type, int exitcode) {
        printFormattedLog(message, type, exitcode, true);

        // Print contextual help based on exit code
        if (exitcode == ExitCodes.PARTIAL_COMMAND_FOUND.getcode() ||
                exitcode == ExitCodes.INVALID_USAGE.getcode() ||
                exitcode == ExitCodes.PROVIDE_HELP.getcode()) {
            printHelpHint();
        }

        if (exitcode == ExitCodes.INFO_VERSION.getcode()) {
            printSystemVersion();
        }

        if(exitcode == ExitCodes.INVALID_FLAG.getcode()){
            printSupportedFlags();
        }

        if (exitcode == ExitCodes.UNKNOWN_TOOL.getcode()) {
            printSupportedTools();
        }

        if (isErrorCode(exitcode)) {
            printTroubleshootingHint(exitcode);
        }
        if(exitcode == ExitCodes.IS_NOT_ADMIN.getcode()){
            printAdminPrivilegesRequired();
        }


        System.exit(exitcode);
    }

    /**
     * Print formatted log with enhanced details
     */

    private static void printFormattedLog(String message, String type, int exitcode, boolean isTerminating) {
        StringBuilder logBuilder = new StringBuilder();

        // Add timestamp if verbose mode
        if (GlobalflagsConfig.Isvarbose()) {
            String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
            logBuilder.append(colorize(GRAY, "[" + timestamp + "] "));
        }

        // Add system name with color
        logBuilder.append(colorize(CYAN + BOLD, "[" + SYSTEM_NAME + "]"));
        logBuilder.append(" ");

        // Add type with icon and color
        String typeFormatted = formatType(type);
        logBuilder.append(typeFormatted);
        logBuilder.append(" ");

        // Add message
        logBuilder.append(colorize(getMessageColor(type), message));

        // Add exit code details if verbose or terminating
        if (GlobalflagsConfig.Isvarbose() || isTerminating) {
            String exitCodeInfo = formatExitCode(exitcode);
            logBuilder.append(" ");
            logBuilder.append(colorize(GRAY, exitCodeInfo));
        }

        System.out.println(logBuilder.toString());

        // Add extra newline for better readability on critical messages
        if (isTerminating && isErrorCode(exitcode)) {
            System.out.println();
        }
    }

    /**
     * Format type with icon and color
     */
    private static String formatType(String type) {
        String icon = "";
        String color = "";

        switch (type.toUpperCase()) {
            case "INFO":
                icon = "ℹ";
                color = BLUE;
                break;
            case "SUCCESS":
                icon = "✓";
                color = GREEN;
                break;
            case "WARNING":
                icon = "⚠";
                color = YELLOW;
                break;
            case "ERROR":
            case "FAILURE":
                icon = "✗";
                color = RED;
                break;
            case "DEBUG":
                icon = "⚙";
                color = MAGENTA;
                break;
            case "PROGRESS":
                icon = "→";
                color = CYAN;
                break;
            case "SYSTEM":
                icon = "◆";
                color = CYAN;
                break;
            default:
                icon = "•";
                color = RESET;
        }

        return colorize(color + BOLD, "[" + icon + " " + type.toUpperCase() + "]");
    }

    /**
     * Get message color based on type
     */
    private static String getMessageColor(String type) {
        switch (type.toUpperCase()) {
            case "ERROR":
            case "FAILURE":
                return RED;
            case "SUCCESS":
                return GREEN;
            case "WARNING":
                return YELLOW;
            case "INFO":
                return RESET;
            default:
                return RESET;
        }
    }

    /**
     * Format exit code with description
     */
    private static String formatExitCode(int code) {
        String description = getExitCodeDescription(code);
        return "(Code: " + code + (description != null ? " - " + description : "") + ")";
    }

    /**
     * Get human-readable description for exit code
     */
    private static String getExitCodeDescription(int code) {
        for (ExitCodes exitCode : ExitCodes.values()) {
            if (exitCode.getcode() == code) {
                return exitCode.getDescription();
            }
        }
        return null;
    }

    /**
     * Check if exit code indicates an error
     */
    private static boolean isErrorCode(int code) {
        return code != ExitCodes.SUCCESS.getcode() &&
                code != ExitCodes.CONTINUE.getcode() &&
                code != ExitCodes.INFO_VERSION.getcode() &&
                code != ExitCodes.INFO_HELP.getcode();
    }

    private static void printAdminPrivilegesRequired() {
        System.out.println();
        System.out.println(colorize(BOLD, "Administrator Privileges Required:"));
        System.out.println("  This operation requires elevated system permissions to modify");
        System.out.println("  global configuration settings and system environment variables.");
        System.out.println();

        System.out.println(colorize(BOLD, "Why This Is Needed:"));
        System.out.println("  • System-level changes cannot be applied without administrator access.");
        System.out.println("  • Certain environment variables and registry values can only be updated");
        System.out.println("    when running with elevated privileges.");
        System.out.println();

        System.out.println(colorize(BOLD, "How to Run Your Terminal as Administrator:"));
        System.out.println("  • Close your current terminal or VS Code session.");
        System.out.println("  • Right-click your terminal or VS Code icon.");
        System.out.println("  • Choose " + colorize(CYAN, "\"Run as administrator\"") + ".");
        System.out.println();

        System.out.println(colorize(BOLD, "Examples:"));
        System.out.println("  " + colorize(GREEN, "Windows Terminal / PowerShell →"));
        System.out.println("    Right-click the app icon → Run as administrator");
        System.out.println();

        System.out.println(colorize(BOLD, "After Restarting With Administrator Rights:"));
        System.out.println("  Re-run the command you attempted earlier.");
        System.out.println();
    }


    private static void printValueFlagSyntax() {
        System.out.println("\n=== Value Flag Syntax ===");
        System.out.println("Value flags require an associated value. You may provide it in two formats:\n");

        System.out.println("1. Inline Assignment:");
        System.out.println("   --flag=value");
        System.out.println("   Example:");
        System.out.println("     --path=/usr/local/java");
        System.out.println("     --jdk-version=17\n");

        System.out.println("2. Separate Token Assignment:");
        System.out.println("   --flag <value>");
        System.out.println("   Example:");
        System.out.println("     --path \"C:\\Java\"");
        System.out.println("     --python-version 3.12\n");

        System.out.println("Supported Value Flags:");
        System.out.println("   --path <value>, -p <value>        Specify installation directory");
        System.out.println("   --vendor <name>                   Choose vendor (e.g., oracle, temurin)");
        System.out.println("   --config <value>                  Specify configuration profile");
        System.out.println("   --env <value>                     Specify environment name");
        System.out.println("   --profile <value>                 Execution profile");
        System.out.println("   --log-file <path>                 Write logs to a file");
        System.out.println("   --version <version>               Specify Tool version");

        System.out.println("Notes:");
        System.out.println("  • Value must not be empty.");
        System.out.println("  • Inline values must not include spaces unless quoted.");
        System.out.println("  • Using incorrect syntax will trigger INVALID_FLAG or MALFORMED_VALUE errors.");
    }

    private static void printSupportedTask() {

        System.out.println();
        System.out.println("Supported Tasks:");
        System.out.println();

        System.out.println("=== Task Commands ===");
        System.out.println("  install, i            Install one or more tools");
        System.out.println("  update, u             Update existing tools");
        System.out.println("  remove, uninstall, r  Remove or uninstall tools");
        System.out.println("  repair                Repair tool installation");
        System.out.println("  list, ls              List installed or available tools");
        System.out.println("  check                 Validate installed tools");
        System.out.println("  info                  Display tool information");

        System.out.println();
        System.out.println("Usage:");
        System.out.println("  gimbal <task> <tool> [flags...]");
        System.out.println();
    }


    private static void printSupportedFlags() {

        String message =
            "\nSupported Flags:\n" +
            "\n=== Global Flags ===\n" +
            "  --verbose, -x              Enable verbose output\n" +
            "  --debug, --dbg             Enable debug/diagnostic logging\n" +
            "  --quiet, -q                Suppress non-essential output\n" +
            "  --no-color                 Disable colored output\n" +
            "  --help, -h                 Display help information\n" +
            "  --version                  Display Gimbal version\n" +
            "  --config <value>           Specify a configuration\n" +
            "  --env <value>              Set target environment\n" +
            "  --profile <value>          Specify execution profile\n" +
            "  --log-file <path>          Write logs to the given file\n" +
            "\n=== Boolean Flags ===\n" +
            "  -y, --yes                  Automatically approve all prompts\n" +
            "  -f, --force                Force execution (ignore warnings)\n" +
            "  --check                    Validate tool installation\n" +
            "  --repair                   Attempt tool repair\n" +
            "  --pip                      Use pip for Python\n" +
            "  --venv                     Use venv for Python\n" +
            "  --conda                    Use Conda environment\n" +
            "  --global                   Perform global installation\n" +
            "  --jdk                      Install Java JDK\n" +
            "  --jre                      Install Java JRE\n" +
            "  --lts                      Install LTS version\n" +
            "\n=== Value Flags ===\n" +
            "  --path <value>, -p <value> Specify installation directory\n" +
            "  --vendor <value>           Choose vendor\n" +
            "  --config=<value>           Inline config assignment\n" +
            "  --env=<value>              Inline environment assignment\n" +
            "  --profile=<value>          Inline profile assignment\n" +
            "  --log-file=<path>          Inline log file assignment\n" +
            "  --version <version>        Specify Tool version\n" +
         
            "\nUsage:\n" +
            "  gimbal <task> <tool> [flags...]\n";

        System.out.println(message);
    }


    /**
     * Apply color to text if colors are enabled
     */
    private static String colorize(String color, String text) {
        if (ENABLE_COLOR) {
            return color + text + RESET;
        }
        return text;
    }

    /**
     * Print help hint with examples
     */
    private static void printHelpHint() {
        System.out.println();
        System.out.println(colorize(BOLD, "Usage:"));
        System.out.println("  gimbal [global-flags] <task> <tool> [tool-flags]");
        System.out.println();
        System.out.println(colorize(BOLD, "Available Tasks:"));
        System.out.println("  install, i        Install one or more development tools");
        System.out.println("  update, u         Update installed tools to latest version");
        System.out.println("  remove, r         Remove installed tools");
        System.out.println("  list, ls          List all installed tools");
        System.out.println("  check             Check system and tool status");
        System.out.println("  info              Display detailed tool information");
        System.out.println();
        System.out.println(colorize(BOLD, "Examples:"));
        System.out.println("  " + colorize(GREEN, "gimbal install java"));
        System.out.println("  " + colorize(GREEN, "gimbal install java git maven"));
        System.out.println("  " + colorize(GREEN, "gimbal install java --version=17 --path=/usr/local/java"));
        System.out.println("  " + colorize(GREEN, "gimbal --verbose install python node"));
        System.out.println();
        System.out.println("Run " + colorize(CYAN, "gimbal --help") + " for detailed documentation.");
        System.out.println();
    }

    /**
     * Print supported tools
     */
    private static void printSupportedTools() {
        System.out.println();
        System.out.println(colorize(BOLD, "Supported Development Tools:"));
        System.out.println();
        System.out.println("  " + colorize(GREEN, "Java Ecosystem:") + "  java, jdk, jre, maven, gradle");
        System.out.println("  " + colorize(GREEN, "Node.js:       ") + "  node, npm, yarn, pnpm");
        System.out.println("  " + colorize(GREEN, "Python:        ") + "  python, py");
        System.out.println("  " + colorize(GREEN, "Containers:    ") + "  docker, docker-compose");
        System.out.println("  " + colorize(GREEN, "Version Control:") + " git");
        System.out.println("  " + colorize(GREEN, "Other:         ") + "  go, rust, cargo, kotlin");
        System.out.println();
    }

    /**
     * Print troubleshooting hints based on error code
     */
    private static void printTroubleshootingHint(int exitcode) {
        System.out.println(colorize(YELLOW + BOLD, "Troubleshooting:"));

        if (exitcode == ExitCodes.NETWORK_ERROR.getcode() ||
                exitcode == ExitCodes.DOWNLOAD_FAILED.getcode() ||
                exitcode == ExitCodes.CONNECTION_REFUSED.getcode()) {
            System.out.println("  • Check your internet connection");
            System.out.println("  • Verify proxy settings if behind a corporate firewall");
            System.out.println("  • Try again later as the download server may be temporarily unavailable");
        } else if (exitcode == ExitCodes.PERMISSION_DENIED.getcode()) {
            System.out.println("  • Run Gimbal with administrator/root privileges");
            System.out.println("  • Check file and directory permissions");
            System.out.println("  • Ensure the installation path is writable");
        } else if (exitcode == ExitCodes.INSTALL_FAILED.getcode()) {
            System.out.println("  • Verify sufficient disk space is available");
            System.out.println("  • Check installation logs for detailed error messages");
            System.out.println("  • Ensure all dependencies are installed");
        } else if (exitcode == ExitCodes.UNKNOWN_TOOL.getcode()) {
            System.out.println("  • Check the tool name for typos");
            System.out.println("  • Run 'gimbal list' to see supported tools");
            System.out.println("  • Visit documentation for complete tool list");
        }

        System.out.println();
        System.out.println("For more help, visit: " + colorize(CYAN, "https://github.com/gimbal-cli/gimbal"));
        System.out.println();
    }

    /**
     * Print system version with ASCII art
     */
    private static void printSystemVersion() {
        System.out.println();
        System.out.println(colorize(CYAN + BOLD, "   ██████╗ ██╗███╗   ███╗██████╗  █████╗ ██╗     "));
        System.out.println(colorize(CYAN + BOLD, "  ██╔════╝ ██║████╗ ████║██╔══██╗██╔══██╗██║      "));
        System.out.println(colorize(CYAN + BOLD, "  ██║  ███╗██║██╔████╔██║██████╔╝███████║██║      "));
        System.out.println(colorize(CYAN + BOLD, "  ██║   ██║██║██║╚██╔╝██║██╔══██╗██╔══██║██║      "));
        System.out.println(colorize(CYAN + BOLD, "  ╚██████╔╝██║██║ ╚═╝ ██║██████╔╝██║  ██║███████╗ "));
        System.out.println(colorize(CYAN + BOLD, "   ╚═════╝ ╚═╝╚═╝     ╚═╝╚═════╝ ╚═╝  ╚═╝╚══════╝ "));
        System.out.println();
        System.out.println("     " + colorize(BOLD, "Gimbal CLI") + " - Version "
                + colorize(GREEN + BOLD, GimbalCLI.SYSTEM_VERSION));
        System.out.println("     " + colorize(GRAY, "Development Tool Manager"));
        System.out.println();
        System.out.println("     Repository: " + colorize(CYAN, "https://github.com/PravinChoudhary11/gimbal"));
        System.out.println("     License:    " + colorize(GRAY, "MIT"));
        System.out.println();
    }
}