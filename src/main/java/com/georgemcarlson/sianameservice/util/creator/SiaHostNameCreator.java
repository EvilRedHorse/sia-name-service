package com.georgemcarlson.sianameservice.util.creator;

import com.georgemcarlson.sianameservice.util.reader.user.User;

public class SiaHostNameCreator {
    private final User user;
    private final String host;
    private final String skylink;
    private final String registrant;
    private final int fee;

    private SiaHostNameCreator(User user, String host, String skylink, String registrant, int fee) {
        this.user = user;
        this.host = host;
        this.skylink = skylink;
        this.registrant = registrant;
        this.fee = fee;
    }
    
    public static SiaHostNameCreator getInstance(User user, String host, String skylink, String registrant, int fee){
        return new SiaHostNameCreator(user, host, skylink, registrant, fee);
    }

    public String create() {
        return user.postArbitraryData(getArbitraryData(), registrant, fee).getTransactionId();
    }

    private byte[] getArbitraryData() {
        return (host + " " + skylink).getBytes();
    }

}
