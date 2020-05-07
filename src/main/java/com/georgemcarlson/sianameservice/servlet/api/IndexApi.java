package com.georgemcarlson.sianameservice.servlet.api;

import com.sawwit.integration.util.StreamReader;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;

public class IndexApi extends SiaNameServiceApi {
    public static IndexApi getInstance(){
        return new IndexApi();
    }
    
    @Override
    public String getPath() {
        return "";
    }

    @Override
    public JSONObject getHelp() {
        JSONObject api = new JSONObject();
        api.put(WhoIsApi.getInstance().getPath(), WhoIsApi.getInstance().getHelp());
        api.put(ListApi.getInstance().getPath(), ListApi.getInstance().getHelp());
        api.put(RedirectApi.getInstance().getPath(), RedirectApi.getInstance().getHelp());
        api.put(RegisterApi.getInstance().getPath(), RegisterApi.getInstance().getHelp());
        return api;
    }

    @Override
    protected String getContent(HttpServletRequest request) {
        return StreamReader.read(IndexApi.class.getResourceAsStream("/index.html"));
    }

    @Override
    protected String getContentType() {
        return "text/html";
    }

}
