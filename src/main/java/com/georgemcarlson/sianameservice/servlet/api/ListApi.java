package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.cacher.SiaHostScannerCache;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class ListApi extends SiaNameServiceApi {
    public static final String PATH = "/list";

    public static ListApi getInstance(){
        return new ListApi();
    }
    
    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public JSONObject getHelp() {
        JSONObject api = new JSONObject();
        api.put("path", PATH);
        return api;
    }
    
    @Override
    protected String getContent(HttpServletRequest request) {
        JSONArray files = new JSONArray();
        for (File file : new File(SiaHostScannerCache.TOP_FOLDER).listFiles()) {
            if (file.getName().endsWith(".sns")) {
                files.put(file.getName());
            }
        }
        return files.toString(2);
    }

}
