package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.persistence.WhoIs;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class ListApi extends SiaNameServiceApi {
    public static final String PATH = "/list";
    public static final String HELP_PARAMETER = "help";
    public static final String OFFSET_PARAMETER = "offset";

    public static ListApi getInstance(){
        return new ListApi();
    }

    public JSONObject getHelp() {
        JSONObject api = new JSONObject();
        api.put("path", PATH);
        JSONArray parameters = new JSONArray();
        parameters.put("(String) " + OFFSET_PARAMETER);
        api.put("parameters", parameters);
        return api;
    }
    
    @Override
    protected String getContent(HttpServletRequest request) {
        if(request.getParameter(HELP_PARAMETER)!=null){
            return getHelp().toString(2);
        }
        int offset;
        try {
            offset = Integer.valueOf(request.getParameter(OFFSET_PARAMETER));
        } catch (Exception e) {
            offset = 0;
        }
        JSONArray files = new JSONArray();
        for (WhoIs whoIs : WhoIs.list(offset)) {
            files.put(whoIs.getHost());
        }
        return files.toString(2);
    }

}
