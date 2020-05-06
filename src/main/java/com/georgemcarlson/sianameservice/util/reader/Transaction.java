package com.georgemcarlson.sianameservice.util.reader;

import com.sawwit.integration.util.Logger;
import com.georgemcarlson.sianameservice.util.TxOutputEncoder;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class Transaction {
    private static final Logger LOGGER = Logger.getInstance();
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String BLOCK_HEIGHT = "block_height";
    public static final String OUTPUTS = "outputs";
    public static final String PREMIUM_OUTPUTS = "premium_outputs";
    private final String transactionId;
    private final long blockHeight;
    private final List<TxOutput> outputs;

    private Transaction(String transactionId, long blockHeight, List<TxOutput> outputs){
        this.transactionId = transactionId;
        this.blockHeight = blockHeight;
        this.outputs = outputs;
    }
    
    public static Transaction getInstance(String transactionId, long blockHeight, TxOutput[] outputs){
        return new Transaction(transactionId, blockHeight, Arrays.asList(outputs));
    }
    
    public static Transaction getInstance(String transactionId, long blockHeight, List<TxOutput> outputs){
        return new Transaction(transactionId, blockHeight, outputs);
    }
    
    public String get(String key){
        if(null==key){
            return null;
        } else switch (key) {
            case OUTPUTS:
            case PREMIUM_OUTPUTS:
                return getData().getJSONArray(key).toString(2);
            default:
                return getData().get(key).toString();
        }
    }
    
    /**
     * Generates a serialized json string representation of the object
     * @param indentFactor
     * @return 
     */
    public String toString(int indentFactor){
        return getData().toString(indentFactor);
    }
    
    private JSONObject getData(){
        JSONObject txOutput = new JSONObject();
        txOutput.put(TRANSACTION_ID, transactionId);
        txOutput.put(BLOCK_HEIGHT, blockHeight);
        JSONArray outputs = new JSONArray();
        for(TxOutput potentialTxOutput : getOutputs()){
            outputs.put(new JSONObject(potentialTxOutput.toString(0)));
        }
        txOutput.put(OUTPUTS, outputs);
        return txOutput;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public byte[] getArbitraryData() {
        byte[] arbitraryData = null;
        try {
            arbitraryData = TxOutputEncoder.decodeArbitraryData(outputs);
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return arbitraryData != null ? arbitraryData : "".getBytes();
    }
    
    public List<TxOutput> getOutputs() {
        return outputs;
    }

}
