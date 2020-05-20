package com.georgemcarlson.sianameservice;

import com.georgemcarlson.sianameservice.servlet.SiaNameServiceServer;
import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.cacher.SiaHostScanner;
import java.sql.SQLException;

public class Main {
    public static SiaNameServiceServer SERVER;

    public static void main(String[] args) throws SQLException {
        run(Settings.PORT);
    }

    public static void run(int port) throws SQLException {
        Thread siaHostScannerThread = new Thread(SiaHostScanner.getInstance());
        siaHostScannerThread.start();

        System.out.println("starting: http://localhost:"+port);
        SERVER = SiaNameServiceServer.getInstance(port);
        SERVER.start();
        System.out.println("started");
    }

}
