package com.gimbal.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tools {
    public String ToolName;
    public final List<String> booleanFlags = new ArrayList<>();
    public final Map<String, String> valueFlags = new HashMap<>();

    public String getToolName() { 
        return ToolName; 
    }

    public List<String> getBooleanFlags(){ 
        return booleanFlags; 
    }

    public Map<String, String> getValueFlags() { 
        return valueFlags; 
    }


    @Override
    public String toString() {
        return "{ ToolName=" + ToolName +
            ", booleanFlags=" + booleanFlags +
            ", valueFlags=" + valueFlags + " }";
    }

}

