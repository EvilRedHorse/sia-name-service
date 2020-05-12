package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.cacher.SiaHostScannerCache;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;

public class ListApi extends SiaNameServiceApi {

    public static ListApi getInstance(){
        return new ListApi();
    }
    
    @Override
    protected String getContent(HttpServletRequest request) {
        JSONArray files = new JSONArray();
        for (File file : new File(SiaHostScannerCache.TOP_FOLDER).listFiles()) {
            for (String tld : Settings.TLDS) {
                if (file.getName().endsWith("." + tld)) {
                    files.put(file.getName());
                }
            }
        }
        return files.toString(2);
    }

}
