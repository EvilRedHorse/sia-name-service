package com.georgemcarlson.sianameservice.util;

import com.georgemcarlson.sianameservice.util.cacher.AddressCache;
import com.georgemcarlson.sianameservice.util.reader.TxOutput;
import com.sawwit.integration.util.Encoder;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.json.JSONArray;

public enum TxOutputEncoder {
    ;
    private static final int DATA_PER_TX = 24;

    public static List<TxOutput> encodeArbitraryData(
        List<String> pool,
        byte[] data,
        String registrant,
        int fee
    ) {
        String encodedData = new String(Encoder.BASE_8.encode(data));
        List<String> txAmounts = new ArrayList<>();
        for(int i=0; i<encodedData.length(); i+=DATA_PER_TX){
            String txAmount = "";
            for(int j=0; i+j<encodedData.length() && j<DATA_PER_TX; j++){
                txAmount += encodedData.charAt(i+j);
            }
            while(txAmount.length()<DATA_PER_TX){
                txAmount += "0";
            }
            txAmounts.add(txAmount);
        }

        Set<String> txAddresses = new TreeSet<>();
        txAddresses.add(registrant);
        JSONArray potentialTxAddresses = new JSONArray(
            AddressCache.getRandomAddresses(txAmounts.size()-txAddresses.size(), pool, txAddresses)
        );
        for(int i=0; i<potentialTxAddresses.length(); i++){
            txAddresses.add(potentialTxAddresses.get(i).toString());
        }
        
        List<TxOutput> outputs = new ArrayList<>();
        Iterator<String> txAddressIterator = txAddresses.iterator();
        Iterator<String> txAmountIterator = txAmounts.iterator();
        while(txAddressIterator.hasNext() && txAmountIterator.hasNext()){
            String txAddress = txAddressIterator.next();
            String txAmount = txAmountIterator.next();
            if(registrant.equals(txAddress)){
                while(txAmount.length() < DATA_PER_TX + fee){
                    txAmount = "1" + txAmount;
                }
            }
            outputs.add(TxOutput.getInstance(txAddress, txAmount));
        }
        Collections.shuffle(outputs);
        return outputs;
    }
    
    public static byte[] decodeArbitraryData(List<TxOutput> txOutputs) {
        Set<String> addresses = new TreeSet<>();
        Map<String, BigInteger> addressToAmount = new HashMap<>();
        for(TxOutput txOutput : txOutputs){
            String address = txOutput.getAddress();
            BigInteger amount = txOutput.getAmount();
            addresses.add(address);
            addressToAmount.put(address, amount);
        }
        
        StringBuilder encodedData = new StringBuilder();
        for(String address : addresses){
            encodedData.append(
                addressToAmount.get(address).toString().replace("0", "").replace("1", "")
            );
        }

        return Encoder.BASE_8.decode(encodedData.toString().getBytes());
    }

}
