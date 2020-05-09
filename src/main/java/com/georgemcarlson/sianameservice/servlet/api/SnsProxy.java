package com.georgemcarlson.sianameservice.servlet.api;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SnsProxy extends HttpServlet {

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

        if (request.getServerName().endsWith(".sns")) {
            RedirectApi.getInstance(request.getServerName()).doIt(request, response);
        } else if (request.getPathInfo().startsWith("/whois/") && request.getPathInfo().contains(".sns")) {
            WhoIsApi.getInstance(getServerNameFromPath(request)).doIt(request, response);
        } else if (request.getPathInfo().contains(".sns")) {
            RedirectApi.getInstance(getServerNameFromPath(request)).doIt(request, response);
        } else if (request.getPathInfo().equals("/redirect")) {
            RedirectApi.getInstance().doIt(request, response);
        } else if (request.getPathInfo().equals("/register")) {
            RegisterApi.getInstance().doIt(request, response);
        } else if (request.getPathInfo().equals("/list")) {
            ListApi.getInstance().doIt(request, response);
        } else {
            IndexApi.getInstance().doIt(request, response);
        }
    }

    private static String getServerNameFromPath(final HttpServletRequest request) {
        String serverName
            = request.getPathInfo().substring(0, request.getPathInfo().lastIndexOf(".sns") + 4);
        if (serverName.contains("/")) {
            serverName = serverName.substring(serverName.lastIndexOf("/") + 1);
        }
        return serverName;
    }

}
