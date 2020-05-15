package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.cacher.SiaHostScannerCache;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class RedirectApi extends SiaNameServiceApi {
    private static final Logger LOGGER = Logger.getInstance();
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
    protected String getContentType() {
        return "text/html";
    }

    @Override
    protected String getContent(HttpServletRequest request) {
        StringBuilder portalChooser = new StringBuilder();
        portalChooser.append("<h1>Choose The Portal To Use</h1>");
        portalChooser
            .append("<div><i>")
            .append("Note that you can automate this page by supplying a `portal=` parameter to ")
            .append("the URL query")
            .append("</i></div>");
        for (String portal : Settings.PORTALS) {
            portalChooser.append("<div><a href='https://").append(portal).append("/")
                .append(getSkyLink(request)).append(getSnsPath(request))
                .append(getSnsQuery(request)).append("'>").append(portal).append("</a>");
        }
        return portalChooser.toString();
    }

    @Override
    protected void doIt(final HttpServletRequest request, final HttpServletResponse response)
        throws IOException, ServletException {
        String portal = request.getParameter(PORTAL_PARAMETER);
        if (portal == null) {
            super.doIt(request, response);
        } else {
            String url = "https://" + portal + "/" + getSkyLink(request) + getSnsPath(request) + getSnsQuery(request);
            response.sendRedirect(url);
        }
    }

    private String getSkyLink(final HttpServletRequest request) {
        String host = getHost() != null ? getHost() : request.getParameter(HOST_PARAMETER);
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
