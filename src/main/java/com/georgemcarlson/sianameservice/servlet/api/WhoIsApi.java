package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.cacher.SiaHostScannerCache;
import com.sawwit.integration.util.Logger;
import java.io.File;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class WhoIsApi extends SiaNameServiceApi {
    private static final Logger LOGGER = Logger.getInstance();
    public static final String PATH = "/" + SiaHostScannerCache.TOP_FOLDER + "/*";
    public static final String HELP_PARAMETER = "help";
    public static final String HOST_PARAMETER = "host";

    public static WhoIsApi getInstance(){
        return new WhoIsApi();
    }
    
    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public JSONObject getHelp() {
        JSONObject api = new JSONObject();
        api.put("path", "/whois/[name].sns");
        JSONArray parameters = new JSONArray();
        parameters.put("(String) " + HOST_PARAMETER);
        api.put("parameters", parameters);
        return api;
    }
    
    @Override
    protected String getContent(HttpServletRequest request) {
        if(request.getParameter(HELP_PARAMETER)!=null){
            return getHelp().toString(2);
        } else{
            File file = new File(SiaHostScannerCache.TOP_FOLDER + "/" + request.getPathInfo());
            if (file.exists()) {
                try {
                    return new String(Files.readAllBytes(file.toPath()));
                } catch (Exception e) {
                    LOGGER.error(e.getLocalizedMessage(), e);
                }
            }
            return getHelp().toString(2);
        }
    }

}
