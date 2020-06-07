package com.georgemcarlson.sianameservice.util.wallet;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.cacher.HostRegistration;
import com.georgemcarlson.sianameservice.util.cacher.HostRegistrations;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TPool {
    private static final Logger LOGGER = Logger.getInstance();

    private TPool(){

    }
    
    public static TPool getInstance(){
        return new TPool();
    }

    private static String getTPool() {
        try {
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://localhost:" + Settings.getWalletApiPort() + "/tpool/transactions");
            requestBuilder.header("User-Agent", Settings.getWalletApiUserAgent());
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
            return response.body().string();
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    public List<HostRegistration> getHostRegistrations() {
        return HostRegistrations.parse(getTPool());
    }

}
