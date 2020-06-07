package com.georgemcarlson.sianameservice.util;

import com.georgemcarlson.sianameservice.util.skynet.SkynetClient;
import com.georgemcarlson.sianameservice.util.skynet.SkynetClientPortal;
import com.georgemcarlson.sianameservice.util.skynet.SkynetClientSiaWallet;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
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

    //application constants
    public static final String SETTINGS_FILE_PATH = "settings.json";
    public static final int SKYLINK_LENGTH = 46;

    //release constants
    public static final String FILE_NAME;
    public static final String GIT_SHA;
    public static final String RELEASE_DATE;
    public static final Date RELEASE;

    //settings.json configuration
    private static boolean isPublic;
    private static boolean enabledUpnp;
    private static int port;
    private static int fee;
    private static int genesisBlock;
    private static String walletApiUserAgent;
    private static String walletApiPassword;
    private static int walletApiPort;
    private static List<String> tlds;
    private static SkynetClient skynetClient;

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

        // Load settings into class to reduce disk io.
        JSONObject settings = getSettings(SETTINGS_FILE_PATH);
        isPublic = settings.optBoolean("public", false);
        enabledUpnp = settings.optBoolean("enabled_upnp", false);
        port = settings.optInt("port", 8080);
        fee = settings.optInt("fee", 0);
        genesisBlock = settings.optInt("genesis_block", 258549);
        walletApiPort = settings.optInt("wallet_api_port", 9980);
        walletApiPassword = settings.optString("wallet_api_password");
        walletApiUserAgent = settings.optString("wallet_api_user_agent", "Sia-Agent");
        tlds = optStrings(settings, "tlds");
        String portal = settings.optString("portal");
        if (portal == null || portal.equals("wallet")) {
            skynetClient = SkynetClientSiaWallet.getSingletonInstance();
        } else {
            skynetClient = SkynetClientPortal.getInstance(portal);
        }
    }

    public static JSONObject getSettings(String path) {
        JSONObject settings = new JSONObject();
        try {
            byte[] payload = Files.readAllBytes(new File(path).toPath());
            settings = new JSONObject(new String(payload));
        } catch (Exception e) {
            Exception unableToLoadSettings
                = new Exception("Unable to load settings: " + e.getLocalizedMessage());
            LOGGER.error(unableToLoadSettings.getLocalizedMessage(), unableToLoadSettings);
        }
        return settings;
    }

    public static void persistSettings(String path) {
        JSONObject settings = new JSONObject();
        settings.put("public", isPublic);
        settings.put("enabled_upnp", enabledUpnp);
        settings.put("port", port);
        settings.put("fee", fee);
        settings.put("genesis_block", genesisBlock);
        settings.put("wallet_api_port", walletApiPort);
        settings.put("wallet_api_password", walletApiPassword);
        settings.put("wallet_api_user_agent", walletApiUserAgent);
        settings.put("tlds", tlds);
        settings.put("portal", skynetClient.getName());

        try (Writer writer
                 = new BufferedWriter(
                     new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))
        ) {
            writer.write(settings.toString(2));
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
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

    public static boolean isPublic() {
        return isPublic;
    }

    public static void setPublic(boolean isPublic) {
        Settings.isPublic = isPublic;
    }

    public static boolean getEnabledUpnp() {
        return enabledUpnp;
    }

    public static void setEnabledUpnp(boolean enabledUpnp) {
        Settings.enabledUpnp = enabledUpnp;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        Settings.port = port;
    }

    public static int getFee() {
        return fee;
    }

    public static void setFee(int fee) {
        Settings.fee = fee;
    }

    public static int getGenesisBlock() {
        return genesisBlock;
    }

    public static void setGenesisBlock(int genesisBlock) {
        Settings.genesisBlock = genesisBlock;
    }

    public static String getWalletApiUserAgent() {
        return walletApiUserAgent;
    }

    public static void setWalletApiUserAgent(String walletApiUserAgent) {
        Settings.walletApiUserAgent = walletApiUserAgent;
    }

    public static String getWalletApiPassword() {
        return walletApiPassword;
    }

    public static void setWalletApiPassword(String walletApiPassword) {
        Settings.walletApiPassword = walletApiPassword;
    }

    public static int getWalletApiPort() {
        return walletApiPort;
    }

    public static void setWalletApiPort(int walletApiPort) {
        Settings.walletApiPort = walletApiPort;
    }

    public static List<String> getTlds() {
        return tlds;
    }

    public static void setTlds(List<String> tlds) {
        Settings.tlds = tlds;
    }

    public static SkynetClient getSkynetClient() {
        return skynetClient;
    }

    public static void setSkynetClient(SkynetClient skynetClient) {
        Settings.skynetClient = skynetClient;
    }

}
