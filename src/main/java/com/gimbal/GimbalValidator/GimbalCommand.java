package com.gimbal.GimbalValidator;

public class GimbalCommand {
    String TaskName;
    String []ToolName;
    String []Flags;
    boolean status;

    public String getTaskName(){
        return this.TaskName;
    }

    public String[] getToolNames(){
        return this.ToolName;
    }
    public boolean getStatus(){
        return this.status;
    }
    public String[] getFlags(){
        return this.Flags;
    }
}
