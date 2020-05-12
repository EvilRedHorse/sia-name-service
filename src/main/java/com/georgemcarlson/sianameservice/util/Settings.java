package com.georgemcarlson.sianameservice.util;

import com.sawwit.integration.util.Logger;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.json.JSONObject;

public class Settings {
    private static final Logger LOGGER = Logger.getInstance();
    public static final String FILE_NAME;
    public static final String GIT_SHA;
    public static final String RELEASE_DATE;
    public static final Date RELEASE;

    static {
        Properties applicationProperties = new Properties();
        try{
            applicationProperties.load(
                Settings.class.getClassLoader().getResourceAsStream("application.properties")
            );
        } catch(Exception e){
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        FILE_NAME = applicationProperties.getProperty("file.name");
        GIT_SHA = applicationProperties.getProperty("git.sha");
        RELEASE_DATE = applicationProperties.getProperty("release.date");
        Date potentialRelease;
        try{
            potentialRelease = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyy").parse(applicationProperties.getProperty("release.date"));
        } catch(Exception e){
            potentialRelease = null;
        }
        RELEASE = potentialRelease;
    }

    private static final String SETTINGS_FILE_PATH = "/settings.json";
    public static final int FEE;

    static {
        JSONObject settings = new JSONObject();
        try {
            byte[] payload = Files.readAllBytes(new File(SETTINGS_FILE_PATH).toPath());
            settings = new JSONObject(new String(payload));
        } catch (Exception e) {
            Exception unableToLoadSettings
                = new Exception("Unable to load settings: " + e.getLocalizedMessage());
            LOGGER.error(unableToLoadSettings.getLocalizedMessage(), unableToLoadSettings);
        }
        FEE = settings.optInt("fee", 0);
    }

}
