package com.georgemcarlson.sianameservice.util.cacher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public abstract class SiaHostScannerCache {
    public static final String TOP_FOLDER = "hosts";
    public static final String FILE_NAME_EXTENSION = ".json";

    protected String readFile(String filePath, String fileName){
        if(!filePath.endsWith("/")){
            filePath += "/";
        }
        try{
            CacheLockRegistry.INSTANCE.acquire(filePath+fileName, CacheLockRegistry.LockType.READ);
            byte[] payload = Files.readAllBytes(new File(filePath+fileName).toPath());
            return new String(payload);
        } catch(Exception e){
            return null;
        } finally{
            CacheLockRegistry.INSTANCE.release(filePath+fileName, CacheLockRegistry.LockType.READ);
        }
    }
    
    protected void writeFile(String filePath, String fileName, String data){
        if(!filePath.endsWith("/")){
            filePath += "/";
        }
        createDirectoryIfNotExists(filePath);
        Writer writer = null;
        try {
            CacheLockRegistry.INSTANCE.acquire(filePath+fileName, CacheLockRegistry.LockType.WRITE);
            writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(filePath+fileName),
                    StandardCharsets.UTF_8
                )
            );
            writer.write(data);
        } catch (IOException ex) {
            // Report
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                // Swallow
            }
            CacheLockRegistry.INSTANCE.release(filePath+fileName, CacheLockRegistry.LockType.WRITE);
        }
    }
    
    private void createDirectoryIfNotExists(String directoryPath){
        File directory = new File(directoryPath);
        if (!directory.exists()){
            directory.mkdirs();
        }
    }

}
