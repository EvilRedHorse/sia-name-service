package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.creator.SiaHostNameCreator;
import com.georgemcarlson.sianameservice.util.reader.user.ThickClientUser;
import com.georgemcarlson.sianameservice.util.reader.user.User;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class RegisterApi extends SiaNameServiceApi {
    public static final String PATH = "/register";
    public static final String HELP_PARAMETER = "help";
    public static final String HOST_PARAMETER = "host";
    public static final String SKYLINK_PARAMETER = "skylink";
    public static final String REGISTRANT_PARAMETER = "registrant";
    public static final String FEE_PARAMETER = "fee";

    public static RegisterApi getInstance(){
        return new RegisterApi();
    }

    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public JSONObject getHelp() {
        JSONObject api = new JSONObject();
        api.put("path", PATH);
        JSONArray parameters = new JSONArray();
        parameters.put("(String) " + HOST_PARAMETER);
        parameters.put("(String) " + SKYLINK_PARAMETER);
        parameters.put("(String) " + REGISTRANT_PARAMETER);
        parameters.put("(String) " + FEE_PARAMETER);
        api.put("parameters", parameters);
        return api;
    }
    
    @Override
    protected String getContent(HttpServletRequest request) {
        if(!request.getParameterNames().hasMoreElements()){
            return getHelp().toString(2);
        } else if(request.getParameter(HELP_PARAMETER)!=null){
            return getHelp().toString(2);
        }
        String host = request.getParameter(HOST_PARAMETER);
        if (host == null) {
            JSONObject response = new JSONObject();
            response.put("message", "no host supplied");
            return response.toString(2);
        } else if (!host.endsWith(".sns")) {
            JSONObject response = new JSONObject();
            response.put("message", "host does not end in .sns");
            return response.toString(2);
        } else if (!isHostValid(host)) {
            JSONObject response = new JSONObject();
            response.put("message", "invalid host");
            return response.toString(2);
        }
        String skylink = request.getParameter(SKYLINK_PARAMETER);
        String registrant = request.getParameter(REGISTRANT_PARAMETER);
        int fee = Integer.parseInt(request.getParameter(FEE_PARAMETER));
        User user = ThickClientUser.getInstance();
        SiaHostNameCreator.getInstance(user, host, skylink, registrant, fee).create();

        JSONObject hostFile = new JSONObject();
        hostFile.put("host", host);
        hostFile.put("skylink", skylink);
        hostFile.put("registrant", registrant);
        hostFile.put("fee", fee);
        return hostFile.toString(2);
    }

    public static boolean isHostValid(String host) {
        if (host == null) {
            return false;
        }
        try {
            return host.equals(new URI("http://"+host).getHost());
        } catch (URISyntaxException e) {
            return false;
        }
    }

}
