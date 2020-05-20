package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.persistence.WhoIs;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class WhoIsApi extends SiaNameServiceApi {
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
            WhoIs whoIs = WhoIs.findByHost(host);
            if (whoIs == null) {
                JSONObject response = new JSONObject();
                response.put("message", "host not found");
                return response.toString(2);
            }
            return whoIs.toString(2);
        }
    }

}
