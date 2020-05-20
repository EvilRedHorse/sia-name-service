package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.persistence.WhoIs;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;

public class ListApi extends SiaNameServiceApi {

    public static ListApi getInstance(){
        return new ListApi();
    }
    
    @Override
    protected String getContent(HttpServletRequest request) {
        JSONArray files = new JSONArray();
        for (WhoIs whoIs : WhoIs.list(0)) {
            files.put(whoIs.getHost());
        }
        return files.toString(2);
    }

}
