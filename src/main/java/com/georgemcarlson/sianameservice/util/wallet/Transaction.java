package com.georgemcarlson.sianameservice.util.wallet;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import java.util.HashSet;
import java.util.Set;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class Transaction {
    private static final Logger LOGGER = Logger.getInstance();
    private final long blockHeight;
    private long epochSeconds = -1;
    private Set<String> transactionIds = new HashSet<>();

    private Transaction(long blockHeight){
        this.blockHeight = blockHeight;
    }
    
    public static Transaction getInstance(long blockHeight){
        return new Transaction(blockHeight);
    }
    
    public Transaction execute() {
        try {
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://localhost:" + Settings.WALLET_API_PORT + "/consensus/blocks?height="+blockHeight);
            requestBuilder.header("User-Agent", Settings.WALLET_API_USER_AGENT);
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
            JSONObject block = new JSONObject(response.body());
            this.epochSeconds = block.getLong("timestamp");
            JSONArray transactions = block.optJSONArray("transactions");
            if (transactions != null) {
                for (int i = 0; i < transactions.length(); i++) {
                    String transactionId = transactions.getJSONObject(i).optString("id");
                    if (transactionId != null) {
                        transactionIds.add(transactionId);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return this;
    }

    public long getEpochSeconds() {
        return epochSeconds;
    }

    public Set<String> getTransactionIds() {
        return transactionIds;
    }

}
