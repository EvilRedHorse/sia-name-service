package com.georgemcarlson.sianameservice.util.cacher;

import com.georgemcarlson.sianameservice.util.Settings;
import com.sawwit.integration.util.Logger;
import com.sawwit.integration.util.Sleep;
import com.georgemcarlson.sianameservice.util.reader.Block;
import com.georgemcarlson.sianameservice.util.reader.Transaction;
import com.georgemcarlson.sianameservice.util.reader.TxOutput;
import com.georgemcarlson.sianameservice.util.reader.Wallet;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class SiaHostScanner extends SiaHostScannerCache implements Runnable {
    private static final Logger LOGGER = Logger.getInstance();
    private static final String FILE_NAME_EXTENSION = ".json";
    private static final String FILE_NAME = "scanner";
    private boolean running = true;

    private SiaHostScanner(){

    }
    
    public static SiaHostScanner getInstance(){
        return new SiaHostScanner();
    }

    public void cacheScannedBlock(long blockId) {
        JSONObject scanner = new JSONObject();
        scanner.put("block", blockId);
        super.writeFile(TOP_FOLDER, FILE_NAME + FILE_NAME_EXTENSION, scanner.toString(2));
    }

    public long getLastBlockScanned() {
        String rawScanner = super.readFile(TOP_FOLDER, FILE_NAME + FILE_NAME_EXTENSION);
        JSONObject scanner;
        if (rawScanner == null) {
            scanner = new JSONObject();
            scanner.put("block", Settings.GELESIS_BLOCK);
            super.writeFile(TOP_FOLDER, FILE_NAME + FILE_NAME_EXTENSION, scanner.toString(2));
        } else {
            scanner = new JSONObject(rawScanner);
        }
        return scanner.getLong("block");
    }

    public void terminate() {
        running = false;
        LOGGER.info("Sia host scanner is shutting down.");
    }

    @Override
    public void run() {
        LOGGER.info("Sia host scanner is starting.");
        while(running){
            Wallet wallet = Wallet.getInstance();
            if (Boolean.parseBoolean(wallet.get(Wallet.ONLINE))) {
                run(Long.parseLong(wallet.get(Wallet.BLOCKCHAIN_HEIGHT)));
            } else {
                Sleep.block(1, TimeUnit.MINUTES);
            }
        }
        LOGGER.info("Sia host scanner has been shut down.");
    }

    public void run(long height) {
        long lastBlockScanned = getLastBlockScanned();
        if (lastBlockScanned < height) {
            cache(lastBlockScanned + 1);
            cacheScannedBlock(lastBlockScanned + 1);
            Sleep.block(400, TimeUnit.MILLISECONDS);
        } else {
            Sleep.block(1, TimeUnit.MINUTES);
        }
    }

    private void cache(long blockId) {
        Block potentialBlock = Block.getInstance(blockId);
        for(Transaction t : potentialBlock.getTransactions()){
            byte[] arbitraryData = t.getArbitraryData();
            if (isArbitraryDataSiaTld(arbitraryData)) {
                writeHost(parseHost(arbitraryData), parseSkyLink(arbitraryData), parseFee(t));
            }
        }
    }

    public static boolean isArbitraryDataSiaTld(byte[] arbitraryData) {
        int skylinkLength = 46;
        if (arbitraryData == null || arbitraryData.length < skylinkLength) {
            return false;
        }
        for (String tld : Settings.TLDS) {
            if (Arrays.equals(
                    ("." + tld).getBytes(),
                    Arrays.copyOfRange(
                        arbitraryData,
                        arbitraryData.length - skylinkLength - ("." + tld + " ").length(),
                        arbitraryData.length - skylinkLength - " ".length()
                    )
                )
            ) {
                return true;
            }
        }
        return false;
    }

    public static String parseSkyLink(byte[] arbitraryData) {
        if (!isArbitraryDataSiaTld(arbitraryData)) {
            return null;
        }
        int skylinkLength = 46;
        return new String(
            Arrays.copyOfRange(
                arbitraryData,
                arbitraryData.length - skylinkLength,
                arbitraryData.length
            )
        );
    }

    public static String parseHost(byte[] arbitraryData) {
        if (!isArbitraryDataSiaTld(arbitraryData)) {
            return null;
        }
        int skylinkLength = 46;
        return new String(
            Arrays.copyOfRange(
                arbitraryData,
                0,
                arbitraryData.length - skylinkLength - " ".length()
            )
        );
    }

    public TxOutput parseFee(Transaction t) {
        if (t.getOutputs() == null || t.getOutputs().isEmpty()) {
            return null;
        }
        TxOutput largest = null;
        for (TxOutput output : t.getOutputs()) {
            if (largest == null) {
                largest = output;
                continue;
            }
            if (largest.getAmount().compareTo(output.getAmount()) < 0) {
                largest = output;
            }
        }
        return largest;
    }

    private void writeHost(String host, String skylink, TxOutput registrant) {
        if (host == null || skylink == null || registrant == null) {
            return;
        }
        String data = super.readFile(TOP_FOLDER, host);
        int fee = registrant.getAmount().toString().length() - 24;
        if (data == null) {
            JSONObject hostFile = new JSONObject();
            hostFile.put("host", host);
            hostFile.put("skylink", skylink);
            hostFile.put("registrant", registrant.getAddress());
            hostFile.put("fee", fee);
            super.writeFile(TOP_FOLDER, host, hostFile.toString(2));
            return;
        }
        JSONObject hostFile = new JSONObject(data);
        if (!registrant.getAddress().equals(hostFile.getString("registrant"))) {
            return;
        }
        if (fee < hostFile.getInt("fee")) {
            return;
        }
        hostFile.put("skylink", skylink);
        hostFile.put("fee", fee);
        super.writeFile(TOP_FOLDER, host, hostFile.toString(2));
    }

}
