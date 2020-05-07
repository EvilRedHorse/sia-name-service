package com.georgemcarlson.sianameservice.servlet.api;

import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

public class HelpApi extends SiaNameServiceApi {
    public static HelpApi getInstance(){
        return new HelpApi();
    }
    
    @Override
    public String getPath() {
        return "";
    }

    @Override
    public JSONObject getHelp() {
        JSONObject api = new JSONObject();
        api.put(HostsApi.getInstance().getPath(), HostsApi.getInstance().getHelp());
        api.put(ListApi.getInstance().getPath(), ListApi.getInstance().getHelp());
        api.put(RedirectApi.getInstance().getPath(), RedirectApi.getInstance().getHelp());
        api.put(RegisterApi.getInstance().getPath(), RegisterApi.getInstance().getHelp());
        return api;
    }
    
    @Override
    protected String getContent(HttpServletRequest request) {
        return getHelp().toString(2);
    }

}
