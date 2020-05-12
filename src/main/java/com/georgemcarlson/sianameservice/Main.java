package com.georgemcarlson.sianameservice;

import com.georgemcarlson.sianameservice.servlet.SiaNameServiceServer;
import com.georgemcarlson.sianameservice.util.cacher.SiaHostScanner;

public class Main {
    public static SiaNameServiceServer SERVER;

    public static void main(String[] args) throws Exception {
        int port;
        try{
            port = Integer.parseInt(args[0]);
        } catch(Exception e){
            //defaulting to "8080"
            port = 80;
        }
        run(port);
    }

    public static void run() throws Exception {
        run(80);
    }

    public static void run(int port) throws Exception {
        Thread sawwitScannerThread = new Thread(SiaHostScanner.getInstance());
        sawwitScannerThread.start();

        System.out.println("starting: http://localhost:"+port);
        SERVER = SiaNameServiceServer.getInstance(port);
        SERVER.start();
        System.out.println("started");
    }

}
