package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.persistence.WhoIs;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RedirectApi extends SiaNameServiceApi {
    public static final String HOST_PARAMETER = "host";
    public static final String PORTAL_PARAMETER = "portal";
    private String host;

    private RedirectApi(String host) {
        this.host = host;
    }

    public static RedirectApi getInstance(){
        return new RedirectApi(null);
    }

    public static RedirectApi getInstance(String host){
        return new RedirectApi(host);
    }

    @Override
    protected String getContent(HttpServletRequest request) {
        return null;
    }

    @Override
    protected void doIt(final HttpServletRequest request, final HttpServletResponse response)
        throws IOException {
        Settings.SKYNET_CLIENT.get(getSkyLink(request) + getSnsPath(request) + getSnsQuery(request), request, response);
    }

    private String getSkyLink(final HttpServletRequest request) {
        String host = getHost() != null ? getHost() : request.getParameter(HOST_PARAMETER);
        WhoIs whoIs = WhoIs.findByHost(host);
        return whoIs == null ? null : whoIs.getSkylink();
    }

    private static String getSnsPath(final HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path == null || path.equals("/redirect")) {
            return "";
        }
        for (String tld : Settings.TLDS) {
            if (path.endsWith("." + tld)) {
                return "";
            }
            if (path.contains("." + tld + "/")) {
                return path.substring(path.lastIndexOf("." + tld + "/") + tld.length() + 1);
            }
        }
        return path;
    }

    private static String getSnsQuery(final HttpServletRequest request) {
        String query = request.getQueryString();
        if (query == null || query.trim().isEmpty()) {
            return "";
        }
        return "?" + query;
    }

    private String getHost() {
        return host;
    }

}
