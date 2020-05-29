package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.Settings;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SkylinkApi extends HttpServlet {

    private SkylinkApi() {

    }

    public static SkylinkApi getInstance(){
        return new SkylinkApi();
    }

    @Override
    protected void doHead(final HttpServletRequest request, final HttpServletResponse response)
        throws IOException {
        String path = request.getPathInfo().startsWith("/skynet/skylink/")
            ? request.getPathInfo().substring(16)
            : request.getPathInfo().substring(1);
        Settings.SKYNET_CLIENT.head(path, request, response);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws IOException {
        String path = request.getPathInfo().startsWith("/skynet/skylink/")
            ? request.getPathInfo().substring(16)
            : request.getPathInfo().substring(1);
        Settings.SKYNET_CLIENT.get(path, request, response);
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
        throws IOException {
        String path = request.getPathInfo().startsWith("/skynet/skylink/")
            ? request.getPathInfo().substring(16)
            : request.getPathInfo().substring(1);
        Settings.SKYNET_CLIENT.get(path, request, response);
    }

}
