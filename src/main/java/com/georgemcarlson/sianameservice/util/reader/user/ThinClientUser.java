package com.georgemcarlson.sianameservice.util.reader.user;

public class ThinClientUser extends ThickClientUser {
    private final String walletSeed;
    private final String walletPassword;
    
    protected ThinClientUser(String walletSeed, String walletPassword){
        this.walletSeed = walletSeed;
        this.walletPassword = walletPassword;
    }
    
    public static ThinClientUser getInstance(String walletSeed, String walletPassword){
        return new ThinClientUser(walletSeed, walletPassword);
    }

}
