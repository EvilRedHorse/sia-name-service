package com.georgemcarlson.sianameservice.util.cacher;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.Sleep;
import com.georgemcarlson.sianameservice.util.persistence.Scanner;
import com.georgemcarlson.sianameservice.util.persistence.WhoIs;
import com.georgemcarlson.sianameservice.util.reader.TxOutput;
import com.georgemcarlson.sianameservice.util.reader.Wallet;
import com.georgemcarlson.sianameservice.util.wallet.Block;
import com.georgemcarlson.sianameservice.util.wallet.TPool;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SiaHostScanner implements Runnable {
    private static final Logger LOGGER = Logger.getInstance();
    private boolean running = true;
    private final Scanner scanner;

    private SiaHostScanner(Scanner scanner) {
        this.scanner = scanner;
    }
    
    public static SiaHostScanner getInstance() throws SQLException {
        Scanner scanner = Scanner.findHighest();
        if (scanner == null) {
            scanner = new Scanner();
            scanner.setBlock(Settings.getGenesisBlock());
            scanner.create();
        }
        return new SiaHostScanner(scanner);
    }

    public void cacheScannedBlock(int blockId) {
        scanner.setBlock(blockId);
        if (blockId % 6 == 0) {
            try {
                scanner.update();
            } catch (Exception e) {
                LOGGER.error("Unable to store block: " + e.getLocalizedMessage(), e);
            }
        }
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
        for (int i = scanner.getBlock(); i < height; i++) {
            int blockHeight = i + 1;
            cache(Block.getInstance(blockHeight).getHostRegistrations(), blockHeight);
            cacheScannedBlock(blockHeight);
            Sleep.block(400, TimeUnit.MILLISECONDS);
        }
        cache(TPool.getInstance().getHostRegistrations(), -1);
        Sleep.block(10, TimeUnit.SECONDS);
    }

    private void cache(List<HostRegistration> hostRegistrations, int blockId) {
        for (HostRegistration hostRegistration : hostRegistrations) {
            if (hostRegistration.isValid()) {
                try {
                    writeHost(
                        hostRegistration.getHost(),
                        hostRegistration.getSkyLink(),
                        hostRegistration.getRegistrant(),
                        blockId,
                        hostRegistration.getBlockSeconds()
                    );
                } catch (SQLException e) {
                    LOGGER.error(e);
                }
            }
        }
    }

    private void writeHost(
        String host,
        String skylink,
        TxOutput registrant,
        int block,
        int seconds
    ) throws SQLException {
        if (host == null || skylink == null || registrant == null) {
            return;
        }
        WhoIs whoIs = WhoIs.findByHost(host);
        int fee = registrant.getAmount().toString().length() - 24;
        if (whoIs == null) {
            whoIs = new WhoIs();
            whoIs.setHost(host);
            whoIs.setSkylink(skylink);
            whoIs.setRegistrant(registrant.getAddress());
            whoIs.setFee(fee);
            whoIs.setBlock(block);
            whoIs.setSeconds(seconds);
            whoIs.create();
            return;
        }
        if (!registrant.getAddress().equals(whoIs.getRegistrant())) {
            return;
        }
        if (fee < whoIs.getFee()) {
            return;
        }
        if (block != -1 && block < whoIs.getBlock()) {
            return;
        }
        if (block == whoIs.getBlock() && seconds < whoIs.getSeconds()) {
            return;
        }
        whoIs.setSkylink(skylink);
        whoIs.setFee(fee);
        whoIs.setBlock(block);
        whoIs.setSeconds(seconds);
        whoIs.update();
    }

}
