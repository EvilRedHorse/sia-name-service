package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.creator.SiaHostNameCreator;
import com.georgemcarlson.sianameservice.util.persistence.WhoIs;
import com.georgemcarlson.sianameservice.util.reader.User;
import com.georgemcarlson.sianameservice.util.wallet.Block;
import com.georgemcarlson.sianameservice.util.wallet.Consensus;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class UpdateApi extends SiaNameServiceApi {
    public static final String PATH = "/update";
    public static final String HELP_PARAMETER = "help";
    public static final String HOST_PARAMETER = "host";
    public static final String SKYLINK_PARAMETER = "publink";

    public static UpdateApi getInstance(){
        return new UpdateApi();
    }

    public JSONObject getHelp() {
        JSONObject api = new JSONObject();
        api.put("path", PATH);
        JSONArray parameters = new JSONArray();
        parameters.put("(String) " + HOST_PARAMETER);
        parameters.put("(String) " + SKYLINK_PARAMETER);
        api.put("parameters", parameters);
        return api;
    }
    
    @Override
    protected String getContent(HttpServletRequest request) {
        if (Settings.getFee() < 1) {
            JSONObject response = new JSONObject();
            response.put("message", "Service is not currently accepting registration requests.");
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
            response.put("message", "No domain name supplied.");
            return response.toString(2);
        } else if (!isTldValid(host)) {
            JSONObject response = new JSONObject();
            List<String> tlds = new ArrayList<>();
            for (String tld : Settings.getTlds()) {
                tlds.add("." + tld);
            }
            response.put("message", "Domain name does not end in " + String.join(" or ", tlds));
            return response.toString(2);
        } else if (!isHostValid(host)) {
            JSONObject response = new JSONObject();
            response.put("message", "Invalid host.");
            return response.toString(2);
        }
        String skylink = request.getParameter(SKYLINK_PARAMETER);
        if (skylink == null) {
            JSONObject response = new JSONObject();
            response.put("message", "No publink hash supplied.");
            return response.toString(2);
        } else if (skylink.length() != Settings.SKYLINK_LENGTH) {
            JSONObject response = new JSONObject();
            response.put("message", "Invalid publink hash. Must be exactly "
                + Settings.SKYLINK_LENGTH + " characters.");
            return response.toString(2);
        }
        Consensus consensus = Consensus.getInstance().execute();
        if (!consensus.isOnline()) {
            JSONObject response = new JSONObject();
            response.put("message", "Wallet is offline.");
            return response.toString(2);
        } else if (!consensus.isSynced()) {
            JSONObject response = new JSONObject();
            response.put("message", "Wallet is not synced.");
            return response.toString(2);
        }
        WhoIs whoIs = WhoIs.findByHost(host);
        if (whoIs == null) {
            JSONObject response = new JSONObject();
            response.put("message", "Supplied domain name is not registered.");
            return response.toString(2);
        }
        if (whoIs.getFee() > Settings.getFee()) {
            JSONObject response = new JSONObject();
            response.put(
                "message",
                "Configured fee is not high enough to update the domain name entry."
            );
            return response.toString(2);
        }
        long blockSeconds
            = Instant.now().getEpochSecond()
            - Block.getInstance(consensus.getHeight()).getEpochSeconds();
        boolean successful = SiaHostNameCreator.getInstance(
            User.getSingletonInstance(),
            host,
            skylink,
            whoIs.getRegistrant(),
            Settings.getFee(),
            blockSeconds
        ).create();
        if (!successful) {
            JSONObject response = new JSONObject();
            response.put("message", "Serverside error.");
            return response.toString(2);
        }
        JSONObject hostFile = new JSONObject();
        hostFile.put("host", host);
        hostFile.put("publink", skylink);
        hostFile.put("registrant", whoIs.getRegistrant());
        hostFile.put("fee", Settings.getFee());
        return hostFile.toString(2);
    }

    public static boolean isTldValid(String host) {
        if (host == null || host.trim().isEmpty()) {
            return false;
        }
        for (String tld : Settings.getTlds()) {
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
