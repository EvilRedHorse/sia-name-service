package com.georgemcarlson.sianameservice.util.wallet;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.TxOutputEncoder;
import com.georgemcarlson.sianameservice.util.cacher.HostRegistration;
import com.georgemcarlson.sianameservice.util.reader.TxOutput;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
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

    private static JSONObject getBlock(long blockHeight) {
        try {
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://localhost:" + Settings.WALLET_API_PORT + "/consensus/blocks?height="+blockHeight);
            requestBuilder.header("User-Agent", Settings.WALLET_API_USER_AGENT);
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
            return new JSONObject(response.body().string());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    public long getEpochSeconds() {
        return getBlock(blockHeight).getLong("timestamp");
    }

    public List<HostRegistration> getHostRegistrations() {
        List<HostRegistration> hostRegistrations = new ArrayList<>();
        JSONArray transactions = getBlock(blockHeight).optJSONArray("transactions");
        if (transactions == null) {
            return hostRegistrations;
        }
        for (int i = 0; i < transactions.length(); i++) {
            List<TxOutput> txOutputs = new ArrayList<>();
            JSONArray potentialTxOutputs = transactions.getJSONObject(i).getJSONArray("siacoinoutputs");
            if (potentialTxOutputs.length() < 6 || potentialTxOutputs.length() > 25) {
                continue;
            }
            for(int j=0; j<potentialTxOutputs.length(); j++){
                try{
                    JSONObject potentialTxOutput = potentialTxOutputs.getJSONObject(j);
                    String unlockhash = potentialTxOutput.get("unlockhash").toString();
                    String value = potentialTxOutput.get("value").toString();
                    TxOutput txOutput = TxOutput.getInstance(unlockhash, value);
                    txOutputs.add(txOutput);
                } catch(Exception e){
                    LOGGER.error(e.getLocalizedMessage(), e);
                }
            }
            try {
                HostRegistration hostRegistration = HostRegistration.getInstance(TxOutputEncoder.decodeArbitraryData(txOutputs), parseRegistrant(txOutputs));
                if (HostRegistration.getInvalidSingletonInstance() != hostRegistration) {
                    hostRegistrations.add(hostRegistration);
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        return hostRegistrations;
    }

    public TxOutput parseRegistrant(List<TxOutput> txOutputs) {
        if (txOutputs == null || txOutputs.isEmpty()) {
            return null;
        }
        TxOutput largest = null;
        for (TxOutput output : txOutputs) {
            if (largest == null) {
                largest = output;
                continue;
            }
            if (largest.getAmount().compareTo(output.getAmount()) < 0) {
                largest = output;
            }
        }
        return TxOutput.getInstance(largest.getAddress(), largest.getAmount().toString());
    }

}
