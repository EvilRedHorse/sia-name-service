package com.georgemcarlson.sianameservice.util.reader;

import com.sawwit.integration.util.Logger;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.TimeZone;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class Wallet extends SiaApi {
    private static final Logger LOGGER = Logger.getInstance();
    public static final String ONLINE = "online";
    public static final String SYNCED = "synced";
    public static final String UNLOCKED = "unlocked";
    public static final String SAWWIT_ORIGIN_HEIGHT = "sawwit_origin_height";
    public static final String WALLET_HEIGHT = "wallet_height";
    public static final String BLOCKCHAIN_HEIGHT = "blockchain_height";
    public static final String CONFIRMED_BALANCE = "confirmed_balance";
    public static final String UNCONFIRMED_BALANCE = "unconfirmed_balance";
    public static final String CURRENT_WALLET_ADDRESS = "current_wallet_address";
    private static final int HASTINGS_IN_A_SIACOIN = 24;
    
    private Wallet(){
        
    }
    
    public static Wallet getInstance(){
        return new Wallet();
    }

    @Override
    protected JSONObject getData() {
        boolean online = false;
        boolean synced = false;
        long walletHeight = 0l;
        long originDate = getOriginBlockDate().getTimeInMillis();
        long currentDate = Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis();        
        long blockchainHeight = getOriginBlockHeight()+(currentDate-originDate)/(1000L)/(60L)/(10L);
        boolean unlocked = false;
        BigDecimal confirmedBalance = new BigDecimal("0");
        BigDecimal unconfirmedIncomingBalance = new BigDecimal("0");
        BigDecimal unconfirmedOutgoingBalance = new BigDecimal("0");
        String currentWalletAddress = "";
        try{
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://localhost:9980/consensus");
            requestBuilder.header("User-Agent", "Sia-Agent");

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
            JSONObject consensus = new JSONObject(response.body().string());
            if(consensus.has("synced")){
                online = true;
            }
            synced = consensus.getBoolean("synced");
            walletHeight = consensus.getLong("height");
            if(consensus.getBoolean("synced")){
                blockchainHeight = consensus.getLong("height");
            }
        } catch(Exception e){
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        try{
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://localhost:9980/wallet");
            requestBuilder.header("User-Agent", "Sia-Agent");

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
            JSONObject wallet = new JSONObject(response.body().string());
            if(wallet.has("unlocked")){
                unlocked = wallet.getBoolean("unlocked");
            }
            if(wallet.has("confirmedsiacoinbalance")){
                String confiemedBalanceInHastings = wallet.get("confirmedsiacoinbalance").toString();
                confirmedBalance = new BigDecimal(convertHastingsToSiacoins(confiemedBalanceInHastings));
            }
            if(wallet.has("unconfirmedoutgoingsiacoins")){
                String unconfirmedOutgoingBalanceInHastings = wallet.get("unconfirmedoutgoingsiacoins").toString();
                unconfirmedOutgoingBalance = new BigDecimal(convertHastingsToSiacoins(unconfirmedOutgoingBalanceInHastings));
            }
            if(wallet.has("unconfirmedincomingsiacoins")){
                String unconfirmedIncomingBalanceInHastings = wallet.get("unconfirmedincomingsiacoins").toString();
                unconfirmedIncomingBalance = new BigDecimal(convertHastingsToSiacoins(unconfirmedIncomingBalanceInHastings));
            }
        } catch(Exception e){
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        if(unlocked){
            try{
                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.url("http://localhost:9980/wallet/addresses");
                requestBuilder.header("User-Agent", "Sia-Agent");

                OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
                Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
                JSONObject wallet = new JSONObject(response.body().string());
                JSONArray addresses = wallet.getJSONArray("addresses");
                currentWalletAddress = addresses.get(addresses.length()-1).toString();
            } catch(Exception e){
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        }

        JSONObject data = new JSONObject();
        data.put(ONLINE, online);
        data.put(SYNCED, synced);
        data.put(UNLOCKED, unlocked);
        data.put(SAWWIT_ORIGIN_HEIGHT, getSawwitOriginBlockHeight());
        data.put(WALLET_HEIGHT, walletHeight);
        data.put(BLOCKCHAIN_HEIGHT, blockchainHeight);
        data.put(CONFIRMED_BALANCE, confirmedBalance.toString());
        data.put(UNCONFIRMED_BALANCE, confirmedBalance.add(unconfirmedIncomingBalance).subtract(unconfirmedOutgoingBalance).toString());
        data.put(CURRENT_WALLET_ADDRESS, currentWalletAddress);
        return data;
    }
    
    private static String convertHastingsToSiacoins(String hastings){
        while (hastings.length() < 25) {
            hastings = "0" + hastings;
        }
        return hastings.substring(0,hastings.length()-HASTINGS_IN_A_SIACOIN)+"."+hastings.substring(hastings.length()-HASTINGS_IN_A_SIACOIN,hastings.length()-(HASTINGS_IN_A_SIACOIN-3));
    }
    
    /**
     * This is a block height that we know is before the initial release of sawwit.
     * @return 
     */
    public static long getSawwitOriginBlockHeight(){
        return 171856;
    }
    
    private static long getOriginBlockHeight(){
        return 0;
    }
    
    private static Calendar getOriginBlockDate(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("US/Central"));
        cal.set(Calendar.YEAR, 2015);
        cal.set(Calendar.MONTH, Calendar.JUNE);
        cal.set(Calendar.DAY_OF_MONTH, 6);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 13);
        cal.set(Calendar.SECOND, 20);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

}
