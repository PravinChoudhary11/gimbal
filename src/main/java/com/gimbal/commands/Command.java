package com.gimbal.commands;

import java.util.ArrayList;
import java.util.List;

public class Command {
    String TaskName = null;
    List<String> GlobalFlags = new ArrayList<>();        // flags applied to entire program
    List<Tools> ToolsAndFlags = new ArrayList<>();       // tools with their own flags

    public String getTaskName(){
        return this.TaskName;
    }

    public List<String> getGlobalFlags(){
        return this.GlobalFlags;
    }
    
    public List<Tools> getToolsAndFlags(){
        return this.ToolsAndFlags;
    }

    @Override
    public String toString() {
        return "TaskName = " + TaskName +
            "\nGlobalFlags = " + GlobalFlags.toString() +
            "\nToolsAndFlags = " + ToolsAndFlags.toString();
    }

}

