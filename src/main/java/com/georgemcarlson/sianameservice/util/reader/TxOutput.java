package com.georgemcarlson.sianameservice.util.reader;

import java.math.BigInteger;
import org.json.JSONObject;

public class TxOutput {
    public static String ADDRESS = "address";
    public static String AMOUNT = "amount";
    private String address;
    private String amount;
    
    private TxOutput(){
        
    }
    
    public static TxOutput getInstance(){
        return new TxOutput();
    }
    
    public static TxOutput getInstance(String address, String amount){
        TxOutput txOutput = new TxOutput();
        txOutput.setAddress(address);
        txOutput.setAmount(amount);
        return txOutput;
    }
    
    public static TxOutput getInstance(String address, BigInteger amount){
        TxOutput txOutput = new TxOutput();
        txOutput.setAddress(address);
        txOutput.setAmount(amount);
        return txOutput;
    }
    
    public String get(String key){
        return getData().get(key).toString();
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
        txOutput.put(ADDRESS, address);
        txOutput.put(AMOUNT, amount);
        return txOutput;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigInteger getAmount() {
        return new BigInteger(amount);
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount.toString();
    }

}
