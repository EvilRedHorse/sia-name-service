package com.georgemcarlson.sianameservice.util.wallet;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class Consensus {
    private static final Logger LOGGER = Logger.getInstance();
    private boolean online = false;
    private boolean synced = false;
    private long height = -1L;

    private Consensus(){
        
    }
    
    public static Consensus getInstance(){
        return new Consensus();
    }

    public Consensus execute() {
        try{
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://localhost:" + Settings.getWalletApiPort() + "/consensus");
            requestBuilder.header("User-Agent", Settings.getWalletApiUserAgent());

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
            JSONObject consensus = new JSONObject(response.body().string());
            if(consensus.has("synced")){
                this.online = true;
            }
            this.synced = consensus.getBoolean("synced");
            this.height = consensus.getLong("height");
        } catch(Exception e){
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return this;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

}
