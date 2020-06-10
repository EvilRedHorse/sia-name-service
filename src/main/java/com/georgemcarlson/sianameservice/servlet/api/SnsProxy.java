package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.Settings;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SnsProxy extends HttpServlet {

    @Override
    protected void doHead(final HttpServletRequest request, final HttpServletResponse response)
        throws IOException {
        if (request.getPathInfo().startsWith("/pubaccess/publink")) {
            SkylinkApi.getInstance().doHead(request, response);
        } else if (isSkylink(request.getPathInfo())) {
            SkylinkApi.getInstance().doHead(request, response);
        } else {
            doHead(request, response);
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {
        doIt(request,response);
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {
        doIt(request,response);
    }

    protected void doIt(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {

        for (String tld : Settings.getTlds()) {
            if (request.getServerName().endsWith("." + tld)) {
                RedirectApi.getInstance(request.getServerName()).doIt(request, response);
                return;
            }
        }
        String serverNameFromPath = getServerNameFromPath(request.getPathInfo());
        if (serverNameFromPath != null) {
            if (request.getPathInfo().startsWith("/whois/")) {
                WhoIsApi.getInstance(serverNameFromPath).doIt(request, response);
            } else {
                RedirectApi.getInstance(serverNameFromPath).doIt(request, response);
            }
        } else if (request.getPathInfo().equals("/redirect")) {
            RedirectApi.getInstance().doIt(request, response);
        } else if (request.getPathInfo().equals("/register")) {
            RegisterApi.getInstance().doIt(request, response);
        } else if (request.getPathInfo().equals("/update")) {
            UpdateApi.getInstance().doIt(request, response);
        } else if (request.getPathInfo().equals("/list")) {
            ListApi.getInstance().doIt(request, response);
        } else if (request.getPathInfo().equals("/scanner")) {
            ScannerApi.getInstance().doIt(request, response);
        } else if (request.getPathInfo().equals("/redirector")) {
            RedirectorDownload.getInstance().doIt(request, response);
        } else if (request.getPathInfo().startsWith("/pubaccess/pubfile")) {
            SkyfileApi.getInstance().doPost(request, response);
        } else if (request.getPathInfo().startsWith("/pubaccess/publink")) {
            SkylinkApi.getInstance().doGet(request, response);
        } else if (isSkylink(request.getPathInfo())) {
            SkylinkApi.getInstance().doGet(request, response);
        } else {
            IndexApi.getInstance().doIt(request, response);
        }
    }

    public static boolean isSkylink(String path) {
        if (path == null || path.length() < 47) {
            return false;
        } else if (path.indexOf("/", 1) != -1 && path.indexOf("/", 1) < 47) {
            return false;
        } else {
            return true;
        }
    }

    public static String getServerNameFromPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        for (String tld : Settings.getTlds()) {
            if (path.endsWith("." + tld) || path.contains("." + tld + "/")
            ) {
                String serverName
                    = path.substring(0, path.lastIndexOf("." + tld) + tld.length() + 1);
                if (serverName.contains("/")) {
                    serverName = serverName.substring(serverName.lastIndexOf("/") + 1);
                }
                return serverName;
            }
        }
        return null;
    }

}
