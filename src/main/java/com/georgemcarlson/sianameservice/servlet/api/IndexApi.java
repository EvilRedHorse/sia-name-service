package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.Logger;
import com.georgemcarlson.sianameservice.util.Settings;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndexApi extends SiaNameServiceApi {
    private static final Logger LOGGER = Logger.getInstance();
    private static final String INDEX_FILE_PATH = "index.html";

    public static IndexApi getInstance(){
        return new IndexApi();
    }

    @Override
    protected String getContent(HttpServletRequest request) {
        try {
            return new String(Files.readAllBytes(new File(INDEX_FILE_PATH).toPath()));
        } catch (Exception e) {
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
