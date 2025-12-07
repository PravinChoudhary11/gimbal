package com.gimbal.GimbalValidator;

import com.gimbal.logger.ExitCodes;
import com.gimbal.logger.GimbalStatus;
import com.gimbal.logger.GimbalStatusType;
import com.gimbal.logger.SystemLogger;

/**
 * cmd = {
 * String TaskName ;
 * String []ToolName;
 * String []Flags;
 * boolean status;
 * }
 *
 */

/**
 *   this unit is Command Validation System of Gimbal :) 
 */

public class CommandValidator {

    public static GimbalCommand CheckCommand(String[] command) {
        GimbalCommand cmd = new GimbalCommand();

        if (command.length == 0) {
            cmd.TaskName = "Invalid";
            cmd.ToolName = new String[0];
            cmd.status = false;

            SystemLogger.LogAndExit(GimbalStatus.ERROR_INVALID_COMMAND.getMessage(),
                    GimbalStatusType.ERROR.getMessage(), ExitCodes.INVALID_USAGE.getcode());
            return cmd;
        }

        if(command.length == 1 && (command[0].equalsIgnoreCase("--version") || command[0].equalsIgnoreCase("-v"))){
            SystemLogger.LogAndExit(
                GimbalStatus.PROVIDE_VERSION.getMessage(), 
                GimbalStatusType.INFO.getMessage(), 
                ExitCodes.INFO_VERSION.getcode()
            );
        }
        if (command.length == 1 && command[0].equalsIgnoreCase("--help")) {
            SystemLogger.LogAndExit(
                GimbalStatus.PROVIDE_HELP.getMessage(),
                GimbalStatusType.INFO.getMessage(),
                ExitCodes.PROVIDE_HELP.getcode()
            );
        }

        cmd.TaskName = getTask(command[0].toLowerCase().trim());
        if (command.length == 1 && cmd.TaskName.equals("i")) {
            cmd.TaskName = "i";
            cmd.ToolName = new String[] { "" };
            cmd.status = true;
            SystemLogger.LogAndExit(
                GimbalStatus.MISSING_COMMAND.getMessage(), 
                GimbalStatusType.INFO.getMessage(),
                ExitCodes.INVALID_USAGE.getcode()
            );
            return cmd;
        }
        
        if (cmd.TaskName.equals("Invalid")) {
            SystemLogger.LogAndExit(
                    GimbalStatus.ERROR_INVALID_COMMAND.getMessage(),
                    GimbalStatusType.FAILURE.getMessage(),
                    ExitCodes.INVALID_USAGE.getcode());
        }
        if (command.length > 1 && cmd.TaskName.equals("i")) {
            cmd.ToolName = getTools(command);
            cmd.status = true;
            cmd.Flags = getFlags(command);
            return cmd;
        }
        return cmd;
    }

    private static String getTask(String task) {
        switch (task) {
            case "install":
            case "i":
                return "i";
            default:
                return "Invalid";
        }
    }

    private static String[] getTools(String[] toolnames) {
        int toolcount = toolnames.length - 1;
        String[] tools = new String[toolcount];
        int ToolNumber = 0;

        for (int i = 1; i < toolnames.length; i++) {

            String raw = toolnames[i].toLowerCase().trim();

            if (raw.startsWith("-")) {
                continue; 
            } else {
                String parsed = getToolname(toolnames[i].toLowerCase().trim());
                if (parsed.equals("Invalid")) {
                    SystemLogger.LogAndExit(
                            GimbalStatus.INVALID_TOOL.getMessage(),
                            GimbalStatusType.ERROR.getMessage(),
                            ExitCodes.UNKNOWN_TOOL.getcode());
                }

                if (parsed.equals("all")) {
                    return new String[] { "all" };
                }
                boolean exit = true;
                for (int j = 0; j < ToolNumber; j++) {
                    if (parsed.equals(tools[j])) {
                        exit = false;
                        break;
                    }
                }
                if (exit) {
                    tools[ToolNumber++] = parsed;
                }
            }

        }
        String[] FinalTools = new String[ToolNumber];
        System.arraycopy(tools, 0, FinalTools, 0, ToolNumber);
        return FinalTools;
    }

    private static String[] getFlags(String[] command) {
        int flagcount = command.length - 1;
        String[] Tempflag = new String[flagcount];
        int flagsize = 0;

        for (int i = 1; i < command.length; i++) {
            String raw = command[i].toLowerCase().trim();
            String parased = "";

            if (!raw.startsWith("-"))
                continue;

            if (raw.startsWith("-")) {
                parased = getFlagname(command[i].toLowerCase().trim());
                if (parased.equals("Invalid")) {
                    SystemLogger.LogAndExit(
                            GimbalStatus.FLAG_NOT_SUPPORTED.getMessage(),
                            GimbalStatusType.FAILURE.getMessage(),
                            ExitCodes.INVALID_USAGE.getcode());
                }

                boolean exit = true;
                for (int j = 0; j < flagsize; j++) {
                    if (Tempflag[j].equals(parased)) {
                        exit = false;
                    }
                }
                if (exit) {
                    Tempflag[flagsize++] = parased;
                }
            }
        }

        String[] flags = new String[flagsize];
        System.arraycopy(Tempflag, 0, flags, 0, flagsize);
        return flags;

    }

    private static String getToolname(String tool) {
        switch (tool) {
            case "java":
            case "jdk":
                return "jdk";
            case "maven":
            case "mvn":
                return "mvn";
            case "git":
                return "git";
            case "docker":
                return "docker";
            default:
                return "Invalid";
        }
    }

    private static String getFlagname(String flag) {

        switch (flag) {
            case "--help":
            case "-h":
                return "help";
            case "--version":
            case "-v":
                return "version";
            case "-y":
            case "--yes":
                return "yes";
            case "--force":
            case "-f":
                return "force";
            case "--verbose":
                return "verbose";
            case "--list":
            case "-l":
                return "list";
            case "--path":
            case "-p":
                return "path";
            case "--check":
                return "check";
            case "--repair":
                return "repair";
            case "--remove":
            case "--uninstall":
            case "--delete":
            case "-r":
                return "remove";
            case "--env":
                return "env";
            case "--exclude":
            case "-e":
                return "exclude";
            default:
                return "Invalid";
        }
    }
}
