package com.gimbal.commands;

import com.gimbal.logger.ExitCodes;
import com.gimbal.logger.GimbalMessage;
import com.gimbal.logger.GimbalMessageType;
import com.gimbal.logger.SystemLogger;

public class CommandExtractor {
    
    public static Command Extract(String []command){
        
        Command cmd = new Command();

        for(int i=0;i<command.length;i++){
            boolean isGFlag= isGlobalFlag(command[i].toLowerCase().trim());
            boolean istask=isTask(command[i].toLowerCase().trim());
            boolean istool = isTool(command[i].toLowerCase().trim());
            if(isGFlag){
                cmd.GlobalFlags.add(getGlobalFlag(command[i].toLowerCase().trim()));
            }
            if(istask && cmd.TaskName == null){
                cmd.TaskName = getTaskName(command[i].toLowerCase().trim());
            }
            if (istool) {

                Tools currTool = new Tools();
                currTool.ToolName = getToolName(command[i].toLowerCase().trim());

                int j = i + 1;

                while (j < command.length) {

                    String token = command[j].toLowerCase().trim();

             
                    if (isTool(token)) break;

                   
                    if (isTask(token)) break;

                    if (isGlobalFlag(token)){
                        SystemLogger.SystemLoggerDisplay(
                            GimbalMessage.GLOBAL_FLAG_IN_BETWEEN.getMessage(),
                            GimbalMessageType.INFO.getMessage(), 
                            ExitCodes.CONTINUE.getcode()
                        );                  
                    }

                    
                    if (isBooleanflag(token)) {
                        currTool.booleanFlags.add(getBooleanflag(token));
                        j++;
                        continue;
                    }

                    // VALUE FLAG
                    
                    if (isValueFlag(token)) {

                        // CASE 1: INLINE value → --flag=value
                        if (token.contains("=")) {
                            String name = getValueFlagName(token);
                            String value = getValueFlagValue(token);

                            if(value == null){
                                SystemLogger.LogAndExitWithDisplayError(
                                    GimbalMessage.MISSING_FLAG_VALUE.getMessage(), 
                                    GimbalMessageType.ERROR.getMessage(), 
                                    value, 
                                    ExitCodes.MISSING_FLAG_VALUE.getcode()
                                );
                            }
                            currTool.valueFlags.put(name, value);
                            
                            j++;
                            continue;
                        }

                        // CASE 2: Value in next argument → --flag value
                        if (j + 1 < command.length && !isTool(command[j + 1]) && !isTask(command[j + 1])) {
                            String name = getValueFlagName(token);
                            String value = command[j + 1];
                            currTool.valueFlags.put(name, value);
                            j += 2;
                            continue;
                        }

                        SystemLogger.LogAndExitWithDisplayError(
                            GimbalMessage.INVALID_FLAG_VALUE.getMessage(), 
                            GimbalMessageType.ERROR.getMessage(), 
                            token, 
                            ExitCodes.MISSING_FLAG_VALUE.getcode()
                        );
                        break; // malformed flag → stop processing flags for this tool
                        
                    }
                    SystemLogger.LogAndExitWithDisplayError(
                        GimbalMessage.INVALID_FLAG.getMessage(), 
                        GimbalMessageType.ERROR.getMessage(), 
                        token, 
                        ExitCodes.INVALID_FLAG.getcode()
                    );
                    break;  // token is not a flag → stop
                }
                cmd.ToolsAndFlags.add(currTool);
                i = j - 1;

            }
   
        }
        return cmd;
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

    private static String getGlobalFlag(String flag) {
        if (flag == null || flag.isEmpty()) return "Invalid";

        flag = flag.toLowerCase();

        // Handle value flags like: --config=dev, --env=prod, --log-file=path
        if (flag.startsWith("--config=")) return "config";
        if (flag.startsWith("--env=")) return "env";
        if (flag.startsWith("--profile=")) return "profile";
        if (flag.startsWith("--log-file=")) return "log-file";

        switch (flag) {

            // Global output / logging flags
            case "--verbose":
            case "-x":
                return "verbose";

            case "--debug":
            case "--dbg":
                return "debug";

            case "--quiet":
            case "-q":
                return "quiet";

            case "--no-color":
                return "no-color";


            // Help & version
            case "--help":
            case "-h":
                return "help";

            case "--version":
                return "version";


            // Config-style flags (no immediate value)
            case "--config":
                return "config";

            case "--env":
                return "env";

            case "--profile":
                return "profile";


            // Log file without inline value
            case "--log-file":
                return "log-file";


            default:
                return "Invalid";
        }
    }

    private static String getTaskName(String task) {

        if (task == null || task.isEmpty()) return "Invalid";

        switch (task.toLowerCase()) {

            // Install
            case "install":
            case "i":
                return "install";

            // Update
            case "update":
            case "u":
                return "update";

            // Remove / uninstall
            case "remove":
            case "uninstall":
            case "delete":
            case "del":
            case "r":
                return "remove";

            // Repair / fix
            case "repair":
            case "fix":
                return "repair";

            // List tools
            case "list":
            case "ls":
                return "list";

            // Check system
            case "check":
            case "verify":
                return "check";

            // Show tool info
            case "info":
                return "info";

            default:
                return "Invalid";
        }
    }

    private static String getToolName(String tool) {

        if (tool == null || tool.isEmpty()) return "Invalid";

        switch (tool.toLowerCase()) {

            // Java ecosystem
            case "java":
            case "jdk":
            case "jre":
                return "java";

            // Maven
            case "maven":
            case "mvn":
                return "maven";

            // Git
            case "git":
                return "git";

            // Node ecosystem
            case "node":
            case "nodejs":
                return "node";

            case "npm":
                return "npm";

            case "npx":
                return "npx";

            case "yarn":
                return "yarn";

            case "pnpm":
                return "pnpm";

            // Python
            case "python":
            case "py":
                return "python";

            // Docker
            case "docker":
                return "docker";

            case "docker-compose":
            case "compose":
                return "compose";

            // Gradle
            case "gradle":
                return "gradle";

            // Go Lang
            case "go":
            case "golang":
                return "go";

            // Rust
            case "rust":
            case "cargo":
                return "rust";

            // Kotlin
            case "kotlin":
            case "kotlinc":
                return "kotlin";

            default:
                return "Invalid";
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

    private static String getBooleanflag(String flag) {

        if (flag == null || flag.isEmpty()) return "Invalid";

        flag = flag.toLowerCase().trim();

        switch (flag) {

            // General yes/force flags
            case "-y":
            case "--yes":
                return "yes";

            case "-f":
            case "--force":
                return "force";

            // Tool-level boolean flags
            case "--pip":
                return "pip";

            case "--venv":
                return "venv";

            case "--conda":
                return "conda";

            case "--global":
                return "global";

            // Java-related boolean flags
            case "--jdk":
                return "jdk";

            case "--jre":
                return "jre";

            case "--lts":
                return "lts";

            // System repair/check flags
            case "--check":
                return "check";

            case "--repair":
                return "repair";

            // Boolean logging/debug flags
            case "--debug":
            case "--dbg":
                return "debug";

            case "--quiet":
            case "-q":
                return "quiet";

            case "--no-color":
                return "no-color";

            default:
                return "Invalid";
        }
    }

    private static boolean isValueFlag(String flag) {
        if (flag == null || flag.isEmpty()) return false;

        flag = flag.toLowerCase().trim();

        // Inline format → definitely a value flag
        if (flag.contains("=")) flag = flag.substring(0, flag.indexOf("="));
        

        // Normal value flags where value comes next
        switch (flag) {
            case "--path":
            case "-p":
            case "--vendor":
            case "--config":
            case "--env":
            case "--profile":
            case "--log-file":
            case "--version":
                return true;

            default:
                return false;
        }
    }

    private static String getValueFlagName(String flag) {

        if (flag == null || flag.isEmpty()) return "Invalid";

        flag = flag.toLowerCase().trim();

        // Format: --flag=value
        if (flag.contains("=")) flag = flag.substring(0, flag.indexOf("="));
        
        
        switch (flag) {
            case "--path":
            case "-p":
                return "path";

            case "--vendor":
                return "vendor";

            case "--config":
                return "config";

            case "--env":
                return "env";

            case "--profile":
                return "profile";

            case "--log-file":
                return "log-file";
            case "--version":
                return "version";

            default:
                return "Invalid";
        }
    }


    private static String getValueFlagValue(String flag) {

        if (flag == null || flag.isEmpty()) return "Invalid";

        flag = flag.trim();

        // Case 1: Flag must contain '='
        if (!flag.contains("=")) return "Invalid";

        // Split only once → everything after '=' is value
        String[] parts = flag.split("=", 2);

        // parts[0] = flag name
        // parts[1] = value part
        String value = parts[1].trim();

        // Remove surrounding quotes ("value") if any
        if ((value.startsWith("\"") && value.endsWith("\"")) ||
            (value.startsWith("'") && value.endsWith("'")))
        {
            value = value.substring(1, value.length() - 1);
        }

        // Value cannot be empty
        if (value.isEmpty()) return "Invalid";

        return value;
    }

}
