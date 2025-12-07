package com.gimbal.commands;


public class InstallCommand {

    public static boolean execute(String []toolnames, String []flags){
        boolean status = true;

        for(String tool: toolnames){
            System.out.println("installing : "+tool);
        }
        for(String flag : flags){
            System.out.println("Flag used: "+flag);
        }
        return status;
    }
}
