package com.gimbal.commands;

import java.util.List;

import com.gimbal.logger.ExitCodes;
import com.gimbal.logger.GimbalMessage;
import com.gimbal.logger.GimbalMessageType;
import com.gimbal.logger.SystemLogger;

public class GlobalflagsRunner {
    
    public static void Runner(List<String> flags){

        for(String flag:flags){
            enableflag(flag);
        }
        
    }

    private static void enableflag(String Flag){

        switch (Flag) {
            case "verbose":
                GlobalflagsConfig.enableVerbose();
                break;
            case "version":
                SystemLogger.LogAndExit(
                    GimbalMessage.CHECK_SYSTEM.getMessage(),
                    GimbalMessageType.INFO.getMessage(), 
                    ExitCodes.INFO_VERSION.getcode());
                    break;
            case "debug":
                
            default:
                break;
        }
    }

    public class GlobalflagsConfig{

        private static boolean verbose;

        public static boolean Isvarbose(){
            return verbose;
        }

        public static void enableVerbose(){
            verbose = true;
        }
        public static void DiableVerbose(){
            verbose = false;
        }
        public static void toggleVerbose(){
            verbose = !verbose;
        }
    }
}
