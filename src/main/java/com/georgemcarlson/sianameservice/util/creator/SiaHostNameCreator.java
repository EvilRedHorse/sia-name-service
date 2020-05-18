package com.georgemcarlson.sianameservice.util.creator;

import com.georgemcarlson.sianameservice.util.reader.User;

public class SiaHostNameCreator {
    private final User user;
    private final String host;
    private final String skylink;
    private final String registrant;
    private final int fee;
    private final long blockSeconds;

    private SiaHostNameCreator(
        User user,
        String host,
        String skylink,
        String registrant,
        int fee,
        long blockSeconds
    ) {
        this.user = user;
        this.host = host;
        this.skylink = skylink;
        this.registrant = registrant;
        this.fee = fee;
        this.blockSeconds = blockSeconds;
    }
    
    public static SiaHostNameCreator getInstance(
        User user,
        String host,
        String skylink,
        String registrant,
        int fee,
        long blockSeconds
    ){
        return new SiaHostNameCreator(user, host, skylink, registrant, fee, blockSeconds);
    }

    public String create() {
        return user.postArbitraryData(getArbitraryData(), registrant, fee).getTransactionId();
    }

    private byte[] getArbitraryData() {
        return (blockSeconds + " " + host + " " + skylink).getBytes();
    }

}
