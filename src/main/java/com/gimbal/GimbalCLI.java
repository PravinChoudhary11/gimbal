package com.gimbal;

import com.gimbal.GimbalValidator.CommandValidator;
import com.gimbal.GimbalValidator.GimbalCommand;
import com.gimbal.commands.InstallCommand;
import com.gimbal.logger.ExitCodes;
import com.gimbal.logger.GimbalStatus;
import com.gimbal.logger.GimbalStatusType;
import com.gimbal.logger.SystemLogger;
import com.gimbal.utils.*;


/**
 * Hello world!
 *
 */
public class GimbalCLI 
{
    public static String SYSTEM_VERSION = "0.0.1";
    public static void main( String[] args )
    {
        GimbalCommand cmd = CommandValidator.CheckCommand(args);
        boolean status = false;
        try{
            status = InstallCommand.execute(cmd.getToolNames(), cmd.getFlags());

        }catch(Exception e){
            SystemLogger.SystemLoggerDisplay(
                "Execution failed: " + e.getMessage(),
                GimbalStatusType.ERROR.getMessage(),
                ExitCodes.FAILURE.getcode()
            );
            e.printStackTrace();
        }
        if(status){
            SystemLogger.SystemLoggerDisplay(
                GimbalStatus.SUCCESS.getMessage(),
                GimbalStatusType.SUCCESS.getMessage(), 
                ExitCodes.SUCCESS.getcode()
            );
            
        }
    }
}
