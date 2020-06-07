package com.georgemcarlson.sianameservice.util.wallet;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.cacher.HostRegistration;
import com.georgemcarlson.sianameservice.util.cacher.HostRegistrations;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

public class Block {
    private static final Logger LOGGER = Logger.getInstance();
    private final long blockHeight;

    private Block(long blockHeight){
        this.blockHeight = blockHeight;
    }
    
    public static Block getInstance(long blockHeight){
        return new Block(blockHeight);
    }

    private static String getBlock(long blockHeight) {
        try {
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://localhost:" + Settings.getWalletApiPort() + "/consensus/blocks?height="+blockHeight);
            requestBuilder.header("User-Agent", Settings.getWalletApiUserAgent());
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
            return response.body().string();
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    public long getEpochSeconds() {
        return new JSONObject(getBlock(blockHeight)).getLong("timestamp");
    }

    public List<HostRegistration> getHostRegistrations() {
        return HostRegistrations.parse(getBlock(blockHeight));
    }

}
