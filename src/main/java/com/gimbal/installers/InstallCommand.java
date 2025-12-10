package com.gimbal.installers;

import java.util.List;
import java.util.Map;

import com.gimbal.commands.Tools;

public class InstallCommand {

    public static boolean execute(List<Tools> tools) {
        for (Tools tool : tools) {
            String toolName = tool.ToolName.toLowerCase();
            Map<String, String> valueFlags = tool.valueFlags;
            List<String> booleanFlags = tool.booleanFlags;

            switch (toolName) {
                case "java":
                    JavaInstaller.InstallJava(valueFlags, booleanFlags);
                    break;

                case "maven":
                    System.out.println("Maven installation coming soon...");
                    break;

                case "python":
                    PythonInstaller.InstallPython(valueFlags,booleanFlags);
                    break;

                case "node":
                    System.out.println("Node.js installation coming soon...");
                    break;

                case "git":
                    System.out.println("Git installation coming soon...");
                    break;

                default:
                    System.out.println("Installation not yet implemented for: " + tool.ToolName);
                    break;
            }
        }
        return true;
    }
}
