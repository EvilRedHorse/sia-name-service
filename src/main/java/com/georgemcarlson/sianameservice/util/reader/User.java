package com.georgemcarlson.sianameservice.util.reader;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.TxOutputEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class User {
    private static final Logger LOGGER = Logger.getInstance();
    private static final User SINGLETON = new User();

    private User(){

    }

    public static User getSingletonInstance(){
        return SINGLETON;
    }

    public List<String> getAddresses() {
        List<String> addresses = new ArrayList<>();
        try {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://localhost:" + Settings.WALLET_API_PORT + "/wallet/seedaddrs");
            requestBuilder.header("User-Agent", Settings.WALLET_API_USER_AGENT);
            requestBuilder.get();

            Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
            JSONArray addressArray
                = new JSONObject(response.body().string()).getJSONArray("addresses");
            for (int i = 0; i < addressArray.length(); i++) {
                addresses.add(addressArray.getString(i));
            }
        } catch (Exception e) {
            //swallow
        }
        return addresses;
    }
    
    /**
     * Post transaction, return transactionId
     * @param txOutputs
     * @return 
     */
    public boolean postTransaction(List<TxOutput> txOutputs){
        Collections.shuffle(txOutputs);
        try{
            JSONArray outputs = new JSONArray();
            for(TxOutput txOutput : txOutputs){
                String address = txOutput.get(TxOutput.ADDRESS);
                String amount = txOutput.get(TxOutput.AMOUNT);
                JSONObject output = new JSONObject();
                output.put("unlockhash", address);
                output.put("value", amount);
                outputs.put(output);
            }
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

            clientBuilder.authenticator((route, response) -> {
                String credential = Credentials.basic("", Settings.WALLET_API_PASSWORD);
                return response.request().newBuilder().header("Authorization", credential).build();
            });

            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("outputs", outputs.toString(0));

            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://localhost:" + Settings.WALLET_API_PORT + "/wallet/siacoins");
            requestBuilder.header("User-Agent", Settings.WALLET_API_USER_AGENT);
            requestBuilder.post(formBuilder.build());


            Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
            return response.code() == 200;
        } catch(Exception e){
            LOGGER.error(e.getLocalizedMessage(), e);
            return false;
        }
    }

    public boolean postArbitraryData(byte[] data, String registrant, int fee) {
        try{
            List<TxOutput> txOutputs = TxOutputEncoder.encodeArbitraryData(
                this.getAddresses(),
                data,
                registrant,
                fee
            );
            return postTransaction(txOutputs);
        } catch(Exception e){
            LOGGER.error(e.getLocalizedMessage(), e);
            return false;
        }
    }

}
