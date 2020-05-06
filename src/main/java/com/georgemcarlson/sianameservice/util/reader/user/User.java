package com.georgemcarlson.sianameservice.util.reader.user;

import com.georgemcarlson.sianameservice.util.TxOutputEncoder;
import com.georgemcarlson.sianameservice.util.reader.Transaction;
import com.georgemcarlson.sianameservice.util.reader.TxOutput;
import com.sawwit.integration.util.Logger;
import java.util.List;

public abstract class User {
    private static final Logger LOGGER = Logger.getInstance();
    protected String currentAddress;
    
    protected User(){

    }

    public abstract String getCurrentAddress();
    
    public abstract String getNewAddress();

    public abstract List<String> getAddresses();
    
    /**
     * Post transaction, return transactionId
     * @param txOutputs
     * @return 
     */
    public abstract Transaction postTransaction(List<TxOutput> txOutputs);

    public Transaction postArbitraryData(byte[] data, String registrant, int fee){
        try{
            List<TxOutput> txOutputs = TxOutputEncoder.encodeArbitraryData(
                this.getAddresses(),
                data,
                registrant,
                fee
            );
            System.out.println("here");
            return postTransaction(txOutputs);
        } catch(Exception e){
            LOGGER.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

}
