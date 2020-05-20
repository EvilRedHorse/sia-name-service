package com.georgemcarlson.sianameservice.util.cacher;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.TxOutputEncoder;
import com.georgemcarlson.sianameservice.util.reader.TxOutput;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class HostRegistrations {

  private static final Logger LOGGER = Logger.getInstance();

  public static List<HostRegistration> parse(String jsonResponse) {
    List<HostRegistration> hostRegistrations = new ArrayList<>();
    JSONArray transactions;
    try {
      transactions = new JSONObject(jsonResponse).optJSONArray("transactions");
    } catch (Exception e) {
      LOGGER.error(e);
      return hostRegistrations;
    }
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

  public static TxOutput parseRegistrant(List<TxOutput> txOutputs) {
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
