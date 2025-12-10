package com.gimbal;

import com.gimbal.commands.Command;
import com.gimbal.commands.CommandExtractor;
import com.gimbal.commands.CommandValidator;
import com.gimbal.commands.FlagsValidator;
import com.gimbal.commands.GlobalflagsRunner;
import com.gimbal.installers.InstallCommand;

/**
 *  WelCome To Heart Of GimbalCLI System
 *
 */

public class GimbalCLI 
{
    public static String SYSTEM_VERSION = "0.0.1";
    public static void main( String[] command )
    {
        CommandValidator.CheckCommand(command);
        Command cmd =  CommandExtractor.Extract(command);
        FlagsValidator.validate(cmd.getToolsAndFlags());
        GlobalflagsRunner.Runner(cmd.getGlobalFlags());
        InstallCommand.execute(cmd.getToolsAndFlags());
    }
}
