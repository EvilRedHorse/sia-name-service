package com.georgemcarlson.sianameservice.util.cacher;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.Sleep;
import com.georgemcarlson.sianameservice.util.reader.TxOutput;
import com.georgemcarlson.sianameservice.util.reader.Wallet;
import com.georgemcarlson.sianameservice.util.wallet.Block;
import com.georgemcarlson.sianameservice.util.wallet.TPool;
import java.util.List;
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
            long blockHeight = lastBlockScanned + 1;
            cache(Block.getInstance(blockHeight).getHostRegistrations(), blockHeight);
            cacheScannedBlock(blockHeight);
            Sleep.block(400, TimeUnit.MILLISECONDS);
        } else {
            cache(TPool.getInstance().getHostRegistrations(), -1);
            Sleep.block(1, TimeUnit.SECONDS);
        }
    }

    private void cache(List<HostRegistration> hostRegistrations, long blockId) {
        for (HostRegistration hostRegistration : hostRegistrations) {
            if (hostRegistration.isValid()) {
                writeHost(
                    hostRegistration.getHost(),
                    hostRegistration.getSkyLink(),
                    hostRegistration.getRegistrant(),
                    blockId,
                    hostRegistration.getBlockSeconds()
                );
            }
        }
    }

    private void writeHost(String host, String skylink, TxOutput registrant, long block, int seconds) {
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
            hostFile.put("block", block);
            hostFile.put("seconds", seconds);
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
        if (block != -1 && block < hostFile.getInt("block")) {
            return;
        }
        if (block == hostFile.getLong("block") && seconds < hostFile.getInt("seconds")) {
            return;
        }
        hostFile.put("skylink", skylink);
        hostFile.put("fee", fee);
        hostFile.put("block", block);
        hostFile.put("seconds", seconds);
        super.writeFile(TOP_FOLDER, host, hostFile.toString(2));
    }

}
