package com.gimbal.logger;

import com.gimbal.GimbalCLI;

public class SystemLogger {

    private static final String SYSTEM_NAME = "Gimbal";
    public static void SystemLoggerDisplay(String message, String Type,int exitcode){
        System.out.println(FormatLogger(message, Type, exitcode));
        System.exit(exitcode);

    }

    public static void LogAndExit(String message,String Type,int exitcode){

        System.out.println(FormatLogger(message, Type, exitcode));

        if(exitcode == ExitCodes.PARTIAL_COMMAND_FOUND.getcode() || exitcode == ExitCodes.INVALID_USAGE.getcode() || exitcode == ExitCodes.PROVIDE_HELP.getcode()){
            printHelpHint();
        }
        if(exitcode == ExitCodes.INFO_VERSION.getcode()){
            printSystemVersion();
        }
        System.exit(exitcode);
    }

    private static String FormatLogger(String message,String Type,int exitcode){
        return(
            "[ " + SYSTEM_NAME + " ]"
                + " [" + Type + "]"
                + " : " + message
                + " (Exit Code: " + exitcode + ")"
            );
    }

    private static void printHelpHint() {
        System.out.println();
        System.out.println("Correct Usage:");
        System.out.println("  gimbal install <tool1> <tool2> ...");
        System.out.println("Examples:");
        System.out.println("  gimbal install java");
        System.out.println("  gimbal install java git maven");
        System.out.println();
        System.out.println("Run `gimbal --help` for more information.");
    }

    private static void printSystemVersion() {
        System.out.println();
        System.out.println("   ██████╗ ██╗███╗   ███╗██████╗  █████╗ ██╗     ");
        System.out.println("  ██╔════╝ ██║████╗ ████║██╔══██╗██╔══██╗██║      ");
        System.out.println("  ██║  ███╗██║██╔████╔██║██████╔╝███████║██║      ");
        System.out.println("  ██║   ██║██║██║╚██╔╝██║██╔══██╗██╔══██║██║      ");
        System.out.println("  ╚██████╔╝██║██║ ╚═╝ ██║██████╔╝██║  ██║███████╗ ");
        System.out.println("   ╚═════╝ ╚═╝╚═╝     ╚═╝╚═════╝ ╚═╝  ╚═╝╚══════╝ ");
        System.out.println();
        System.out.println("     Gimbal CLI - Version " + GimbalCLI.SYSTEM_VERSION);
        System.out.println();
    }



}
