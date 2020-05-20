package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.persistence.Scanner;
import com.georgemcarlson.sianameservice.util.persistence.WhoIs;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class ScannerApi extends SiaNameServiceApi {

    public static ScannerApi getInstance(){
        return new ScannerApi();
    }
    
    @Override
    protected String getContent(HttpServletRequest request) {
        Scanner scanner = Scanner.findHighest();
        if (scanner == null) {
            JSONObject response = new JSONObject();
            response.put("message", "no block has been scanned");
            return response.toString(2);
        }
        return scanner.toString(2);
    }

}
