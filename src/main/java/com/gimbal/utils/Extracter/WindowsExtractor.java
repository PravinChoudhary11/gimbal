package com.gimbal.utils.Extracter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.gimbal.logger.ExitCodes;
import com.gimbal.logger.GimbalMessage;
import com.gimbal.logger.GimbalMessageType;
import com.gimbal.logger.SystemLogger;

/**
 *  @author Pravin Choudhary 
 *  @since 1.0
 * 
  */ 

public final class WindowsExtractor {

    // no instance creation allowed this is a Utility Class
    private WindowsExtractor() {}

    public static boolean extract(String zipFilePath, String destinationDir){

        SystemLogger.SystemLoggerDisplay(
                    GimbalMessage.EXTRACTION_STARTED.getMessage() + " - " + zipFilePath,
                    GimbalMessageType.INFO.getMessage(),
                    ExitCodes.CONTINUE.getcode());
        try{
            // converted String to a Path instance that references the path of the folder
            Path destination = Paths.get(destinationDir);

            // if folder does not exist then it creates it
            if (!Files.exists(destination)) {
                Files.createDirectories(destination);
            }

            // now this ZipInputStream instance is created which reads all the raw bytes delivered by FileInputStream instance
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {

                // this ZipEntry instance is created to use in loop; it represents one single entry in the zip file
                // e.g., in java.zip it represents bin/java.exe
                ZipEntry entry;

                // in this loop we are using the entry instance to read entries of the zip file one by one
                // when finished i.e., entry becomes null, the loop exits
                while ((entry = zis.getNextEntry()) != null) {

                    // new Path instance that will hold the complete value with full path: destination + current zip entry
                    Path outputPath = destination.resolve(entry.getName());

                    // if entry is a simple directory (no file inside), just create the outputPath
                    if (entry.isDirectory()) {
                        // creation of outputPath
                        Files.createDirectories(outputPath);
                    } else {

                        // if entry has files, then first create the parent directory
                        // e.g., bin/java.exe → create bin/, then write java.exe
                        Path parent = outputPath.getParent();
                        if (parent != null && !Files.exists(parent)) {
                            // creation of bin
                            Files.createDirectories(parent);
                        }

                        // create an OutputStream instance used for writing the data from the zip to destination
                        // Files.newOutputStream() provides the file handle into which OutputStream writes bytes
                        try (OutputStream os = Files.newOutputStream(outputPath)) {
                            // now data is transferred to OutputStream from zis and it starts writing the bytes
                            zis.transferTo(os);
                        }
                    }
                    // closing the entry to prevent resource leak
                    zis.closeEntry();
                }
                
                SystemLogger.SystemLoggerDisplay(
                        GimbalMessage.EXTRACTION_COMPLETED.getMessage(),
                        GimbalMessageType.SUCCESS.getMessage(),
                        ExitCodes.CONTINUE.getcode());

            }catch(IOException e){
                SystemLogger.LogAndExit(
                        GimbalMessage.EXTRACT_FAILED.getMessage() + " - " + e.getMessage(),
                        GimbalMessageType.ERROR.getMessage(),
                        ExitCodes.EXTRACTION_FAILED.getcode());      
            }
            return true;
        }catch(IOException e){
            SystemLogger.LogAndExit(
                        GimbalMessage.EXTRACT_FAILED.getMessage() + " - " + e.getMessage(),
                        GimbalMessageType.ERROR.getMessage(),
                        ExitCodes.EXTRACTION_FAILED.getcode());  
        }
        return false;
    }
}
