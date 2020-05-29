package com.georgemcarlson.sianameservice.util;

import com.georgemcarlson.sianameservice.util.skynet.SkynetClient;
import com.georgemcarlson.sianameservice.util.skynet.SkynetClientPortal;
import com.georgemcarlson.sianameservice.util.skynet.SkynetClientSiaWallet;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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

    public static final int SKYLINK_LENGTH = 46;

    private static final String SETTINGS_FILE_PATH = "settings.json";
    public static final int PORT;
    public static final int FEE;
    public static final int GENESIS_BLOCK;
    public static final String WALLET_API_USER_AGENT;
    public static final String WALLET_API_PASSWORD;
    public static final int WALLET_API_PORT;
    public static final List<String> TLDS;
    public static final SkynetClient SKYNET_CLIENT;

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
        PORT = settings.optInt("port", 8080);
        FEE = settings.optInt("fee", 0);
        GENESIS_BLOCK = settings.optInt("genesis_block", 258549);
        WALLET_API_PORT = settings.optInt("wallet_api_port", 9980);
        WALLET_API_PASSWORD = settings.optString("wallet_api_password");
        WALLET_API_USER_AGENT = settings.optString("wallet_api_user_agent", "Sia-Agent");
        TLDS = optStrings(settings, "tlds");
        String portal = settings.optString("portal");
        if (portal == null || portal.equals("wallet")) {
            SKYNET_CLIENT = SkynetClientSiaWallet.getSingletonInstance();
        } else {
            SKYNET_CLIENT = SkynetClientPortal.getInstance(portal);
        }
    }

    public static List<String> optStrings(JSONObject jsonObject, String key) {
        List<String> values = new ArrayList<>();
        if (jsonObject != null && jsonObject.optJSONArray(key) != null) {
            for(int i = 0; i < jsonObject.getJSONArray(key).length(); i++) {
                String tld = jsonObject.getJSONArray(key).get(i).toString();
                values.add(tld);
            }
        }
        return Collections.unmodifiableList(values);
    }

}
