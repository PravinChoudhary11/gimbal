package com.gimbal;

import com.gimbal.commands.Command;
import com.gimbal.commands.CommandExtractor;
import com.gimbal.commands.CommandValidator;

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
        System.out.println(cmd);
    }
}
