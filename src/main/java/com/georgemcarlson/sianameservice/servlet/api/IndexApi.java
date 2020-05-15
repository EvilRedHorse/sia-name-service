package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndexApi extends SiaNameServiceApi {
    private static final Logger LOGGER = Logger.getInstance();

    public static IndexApi getInstance(){
        return new IndexApi();
    }

    @Override
    protected String getContent(HttpServletRequest request) {
        try (InputStream index = IndexApi.class.getResourceAsStream("/index.html")) {
            return new String(ByteStreams.toByteArray(index));
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return "";
        }
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
