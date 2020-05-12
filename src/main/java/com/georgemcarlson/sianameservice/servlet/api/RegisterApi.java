package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.Settings;
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

    public static RegisterApi getInstance(){
        return new RegisterApi();
    }

    public JSONObject getHelp() {
        JSONObject api = new JSONObject();
        api.put("path", PATH);
        JSONArray parameters = new JSONArray();
        parameters.put("(String) " + HOST_PARAMETER);
        parameters.put("(String) " + SKYLINK_PARAMETER);
        parameters.put("(String) " + REGISTRANT_PARAMETER);
        api.put("parameters", parameters);
        return api;
    }
    
    @Override
    protected String getContent(HttpServletRequest request) {
        if (Settings.FEE < 1) {
            JSONObject response = new JSONObject();
            response.put("message", "service is not currently accepting registration requests");
            return response.toString(2);
        }
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
        } else if (!isTldValid(host)) {
            JSONObject response = new JSONObject();
            response.put("message", "host does not end in " + String.join(" or ", Settings.TLDS));
            return response.toString(2);
        } else if (!isHostValid(host)) {
            JSONObject response = new JSONObject();
            response.put("message", "invalid host");
            return response.toString(2);
        }
        String skylink = request.getParameter(SKYLINK_PARAMETER);
        if (skylink == null) {
            JSONObject response = new JSONObject();
            response.put("message", "no skylink hash supplied");
            return response.toString(2);
        } else if (skylink.length() != 46) {
            JSONObject response = new JSONObject();
            response.put("message", "invalid skylink hash. must be exactly 46 characters.");
            return response.toString(2);
        }
        String registrant = request.getParameter(REGISTRANT_PARAMETER);
        if (registrant == null) {
            JSONObject response = new JSONObject();
            response.put("message", "no registrant sia address supplied");
            return response.toString(2);
        } else if (registrant.length() != 76) {
            JSONObject response = new JSONObject();
            response.put("message", "invalid registraint sia address. must be exactly 76 characters.");
            return response.toString(2);
        }
        User user = ThickClientUser.getInstance();
        SiaHostNameCreator.getInstance(user, host, skylink, registrant, Settings.FEE).create();

        JSONObject hostFile = new JSONObject();
        hostFile.put("host", host);
        hostFile.put("skylink", skylink);
        hostFile.put("registrant", registrant);
        hostFile.put("fee", Settings.FEE);
        return hostFile.toString(2);
    }

    public static boolean isTldValid(String host) {
        if (host == null || host.trim().isEmpty()) {
            return false;
        }
        for (String tld : Settings.TLDS) {
            if (host.endsWith("." + tld)) {
                return true;
            }
        }
        return false;
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
