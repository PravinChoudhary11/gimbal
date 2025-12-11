package com.gimbal;

import com.gimbal.commands.Command;
import com.gimbal.commands.CommandExtractor;
import com.gimbal.commands.CommandValidator;
import com.gimbal.commands.FlagsValidator;
import com.gimbal.commands.GlobalflagsRunner;
import com.gimbal.installers.InstallCommand;
import com.gimbal.logger.ExitCodes;
import com.gimbal.logger.GimbalMessageType;
import com.gimbal.logger.SystemLogger;
import com.gimbal.utils.IsAdmin;

/**
 *  WelCome To Heart Of GimbalCLI System
 *
 */

public class GimbalCLI 
{
    public static String SYSTEM_VERSION = "0.0.3";
    public static void main( String[] command )
    {
        if (!IsAdmin.check()) {
            SystemLogger.LogAndExit(
                "Administrator privileges are required to modify system environment variables.",
                GimbalMessageType.ERROR.getMessage(),
                ExitCodes.IS_NOT_ADMIN.getcode()
            );
        }

        CommandValidator.CheckCommand(command);
        Command cmd =  CommandExtractor.Extract(command);
        FlagsValidator.validate(cmd.getToolsAndFlags());
        GlobalflagsRunner.Runner(cmd.getGlobalFlags());
        InstallCommand.execute(cmd.getToolsAndFlags());
    }
}
