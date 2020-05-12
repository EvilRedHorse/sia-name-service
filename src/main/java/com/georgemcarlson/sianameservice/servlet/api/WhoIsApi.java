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
    public static final String HELP_PARAMETER = "help";
    public static final String HOST_PARAMETER = "host";
    private String host;

    private WhoIsApi(String host) {
        this.host = host;
    }

    public static WhoIsApi getInstance(String host){
        return new WhoIsApi(host);
    }
    
    public JSONObject getHelp() {
        JSONObject api = new JSONObject();
        api.put("path", "/whois/[name].[tld]");
        JSONArray parameters = new JSONArray();
        parameters.put("(String) " + HOST_PARAMETER);
        api.put("parameters", parameters);
        return api;
    }
    
    protected String getContent(HttpServletRequest request) {
        if(request.getParameter(HELP_PARAMETER)!=null){
            return getHelp().toString(2);
        } else{
            File file = new File(SiaHostScannerCache.TOP_FOLDER + "/" + host);
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
