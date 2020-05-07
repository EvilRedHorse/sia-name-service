package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.cacher.SiaHostScannerCache;
import com.sawwit.integration.util.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public class RedirectApi extends SiaNameServiceApi {
    private static final Logger LOGGER = Logger.getInstance();
    public static final String PATH = "/redirect";
    public static final String HOST_PARAMETER = "host";
    public static final String PORTAL_PARAMETER = "portal";

    public static RedirectApi getInstance(){
        return new RedirectApi();
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
        parameters.put("(String) " + PORTAL_PARAMETER);
        api.put("parameters", parameters);
        return api;
    }

    @Override
    protected String getContent(HttpServletRequest request) {
        return null;
    }

    private String getSkyLink(String host) {
        File file = new File(SiaHostScannerCache.TOP_FOLDER + "/" + host);
        if (file.exists()) {
            try {
                return new JSONObject(new String(Files.readAllBytes(file.toPath())))
                    .getString("skylink");
            } catch (Exception e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        }
        return null;
    }

    private void doIt(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        String host = request.getParameter(HOST_PARAMETER);
        String portal = request.getParameter(PORTAL_PARAMETER);
        String url = "https://" + portal + "/" + getSkyLink(host);
        response.sendRedirect(url);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        doIt(request, response);
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        doIt(request, response);
    }

}
