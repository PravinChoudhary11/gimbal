package com.gimbal.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class IsAdmin {

    private IsAdmin() {}

    /**
     * Checks if the current process is running with Administrator privileges.
     * Works reliably on all modern Windows versions.
     *
     * @return true if admin, false otherwise
     */
    public static boolean check() {
        try {
            // net session requires admin privileges
            Process process = new ProcessBuilder("cmd.exe", "/c", "net session").start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream())
            );

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            StringBuilder error = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                error.append(line);
            }

            int exitCode = process.waitFor();

            // If the command succeeds → Admin
            // If it fails with "Access is denied." → Not admin
            if (exitCode == 0) {
                return true;
            }

            String errorMsg = error.toString().toLowerCase();
            if (errorMsg.contains("access is denied")) {
                return false;
            }

            return false;

        } catch (Exception e) {
            return false; // safest fallback
        }
    }
}
