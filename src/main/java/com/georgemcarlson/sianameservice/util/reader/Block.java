package com.georgemcarlson.sianameservice.util.reader;

import com.georgemcarlson.sianameservice.util.Settings;
import com.sawwit.integration.util.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class Block extends SiaApi{
    private static final Logger LOGGER = Logger.getInstance();
    public static final String TRANSACTIONS = "transactions";
    public static final String BLOCK_HEIGHT = "block_height";
    private final long blockHeight;
    
    private Block(long blockHeight){
        this.blockHeight = blockHeight;
    }
    
    public static Block getInstance(long blockHeight){
        return new Block(blockHeight);
    }
    
    @Override
    public String get(String key){
        if(null==key){
            return null;
        } else if (TRANSACTIONS.equals(key)) {
            return getData().getJSONObject(key).toString(2);
        } else {
            return getData().get(key).toString();
        }
    }
    
    /**
     * Generates a serialized json string representation of the object
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @return a printable, displayable, portable, transmittable representation of the object
     */
    @Override
    public String toString(int indentFactor){
        return getData().toString(indentFactor);
    }
    
    @Override
    protected JSONObject getData() {
        JSONObject transactions = new JSONObject();
        for(Transaction transaction : getTransactions()){
            String transactionId = transaction.get(Transaction.TRANSACTION_ID);
            transactions.put(transactionId, new JSONObject(transaction.toString(0)));
        }
        
        JSONObject txOutput = new JSONObject();
        txOutput.put(TRANSACTIONS, transactions);
        txOutput.put(BLOCK_HEIGHT, blockHeight);
        return txOutput;
    }
    
    public List<Transaction> getTransactions(){
        JSONObject block;
        try {
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url("http://localhost:" + Settings.WALLET_API_PORT + "/consensus/blocks?height="+blockHeight);
            requestBuilder.header("User-Agent", Settings.WALLET_API_USER_AGENT);
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
            if (response.body() == null) {
                block = null;
            } else {
                block = new JSONObject(response.body().string());
            }
        } catch (Exception e) {
            LOGGER.info("Block " + blockHeight + " is not indexed.");
            block = null;
        }
        if (block == null || !block.has("transactions")) {
            return Collections.emptyList();
        }
        List<Transaction> transactions = new ArrayList<>();
        JSONArray potentialTransactions = block.getJSONArray("transactions");
        for(int i=0; i<potentialTransactions.length(); i++){
            JSONObject potentialTransaction = potentialTransactions.getJSONObject(i);
            String transactionId = potentialTransaction.get("id").toString();
            List<TxOutput> txOutputs = new ArrayList<>();
            JSONArray potentialTxOutputs = potentialTransaction.getJSONArray("siacoinoutputs");
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
            Transaction transaction = Transaction.getInstance(transactionId, blockHeight, txOutputs);
            transactions.add(transaction);
        }
        return transactions;
    }

}
