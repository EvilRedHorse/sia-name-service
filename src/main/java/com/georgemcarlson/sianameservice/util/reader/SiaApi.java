package com.georgemcarlson.sianameservice.util.reader;

import org.json.JSONObject;

public abstract class SiaApi {
    
    public String get(String key){
        return getData().get(key).toString();
    }
    
    /**
     * Generates a serialized json string representation of the object
     * @param indentFactor
     * @return 
     */
    public String toString(int indentFactor){
        return getData().toString(indentFactor);
    }
    
    protected abstract JSONObject getData();

}
