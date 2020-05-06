package com.georgemcarlson.sianameservice.util.reader.user;

import com.georgemcarlson.sianameservice.util.cacher.AddressCache;
import com.georgemcarlson.sianameservice.util.reader.Transaction;
import com.georgemcarlson.sianameservice.util.reader.TxOutput;
import com.georgemcarlson.sianameservice.util.reader.Wallet;
import com.sawwit.integration.util.Logger;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.SystemUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class ThickClientUser extends User {
    private static final Logger LOGGER = Logger.getInstance();
    public static final String SIA_AUTHENTICATION;

    static {
        String filePath = null;
        File apiPasswordFile = new File("apipassword");
        if (apiPasswordFile.exists() && apiPasswordFile.isFile()) {
            filePath = apiPasswordFile.getPath();
        } else if (SystemUtils.IS_OS_MAC) {
            filePath = System.getProperty("user.home") + "/Library/Application Support/Sia/apipassword";
        } else if (SystemUtils.IS_OS_LINUX) {
            filePath = System.getProperty("user.home") + "/.sia/apipassword";
        }
        ByteArrayOutputStream apiPassword = new ByteArrayOutputStream();
        if (filePath != null) {
            try (InputStream is = new FileInputStream(filePath)) {
                int i;
                while ((i = is.read()) != -1) {
                    if (i == '\n') {
                        break;
                    }
                    apiPassword.write(i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SIA_AUTHENTICATION = new String(apiPassword.toByteArray());
    }

    protected ThickClientUser(){
        super();
    }

    public static ThickClientUser getInstance(){
        return new ThickClientUser();
    }

    @Override
    public Transaction postTransaction(List<TxOutput> txOutputs){
        Collections.shuffle(txOutputs);
        long blockHeight = Long.parseLong(Wallet.getInstance().get(Wallet.BLOCKCHAIN_HEIGHT));

        Transaction transaction = null;
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
                String credential = Credentials.basic("", SIA_AUTHENTICATION);
                return response.request().newBuilder().header("Authorization", credential).build();
            });

            FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("outputs", outputs.toString(0));

            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://localhost:9980/wallet/siacoins");
            requestBuilder.header("User-Agent", "Sia-Agent");
            requestBuilder.post(formBuilder.build());


            Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
            String rawResponse = response.body().string();
            JSONArray transactionIds = new JSONObject(rawResponse).getJSONArray("transactionids");
            String transactionId = transactionIds.get(transactionIds.length()-1).toString();
            transaction = Transaction.getInstance(transactionId, blockHeight, txOutputs);
        } catch(Exception e){
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return transaction;
    }

    @Override
    public String getCurrentAddress() {
        if(currentAddress==null) {
            return getNewAddress();
        } else {
            return currentAddress;
        }
    }

    @Override
    public List<String> getAddresses() {
        List<String> addresses = new ArrayList<>();
        try {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://localhost:9980/wallet/seedaddrs");
            requestBuilder.header("User-Agent", "Sia-Agent");
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

    @Override
    public String getNewAddress() {
        List<String> addresses = getAddresses();
        if (!addresses.isEmpty()) {
            currentAddress = addresses.get((int)(Math.random() * addresses.size()));
            return currentAddress;
        }
        int randomAddressIndex = (int)(Math.random() * AddressCache.getDevFundAddresses().length);
        currentAddress = AddressCache.getDevFundAddresses()[randomAddressIndex];
        return currentAddress;
    }

}
