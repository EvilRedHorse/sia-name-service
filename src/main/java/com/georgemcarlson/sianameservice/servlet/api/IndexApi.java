package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.Settings;
import com.sawwit.integration.util.StreamReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndexApi extends SiaNameServiceApi {

    public static IndexApi getInstance(){
        return new IndexApi();
    }

    @Override
    protected String getContent(HttpServletRequest request) {
        return StreamReader.read(IndexApi.class.getResourceAsStream("/index.html"));
    }

    @Override
    protected String getContentType() {
        return "text/html";
    }

    @Override
    protected void doIt(final HttpServletRequest request, final HttpServletResponse response)
        throws IOException, ServletException {
        for (String tld : Settings.TLDS) {
            if (request.getServerName().endsWith("." + tld)) {
                RedirectApi.getInstance(request.getServerName()).doIt(request, response);
                return;
            }
        }
        super.doIt(request, response);
    }

}
