package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.cacher.SiaHostScannerCache;
import com.sawwit.integration.util.Logger;
import com.sawwit.integration.util.StreamReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class IndexApi extends SiaNameServiceApi {
    private static final Logger LOGGER = Logger.getInstance();
    public static final String PORTAL_PARAMETER = "portal";

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

    @Override
    protected void doIt(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        if (request.getServerName().endsWith(".sns")) {
            String host = request.getServerName();
            String portal = request.getParameter(PORTAL_PARAMETER);
            if (portal == null) {
                portal = "siasky.net";
            }
            String url = "https://" + portal + "/" + getSkyLink(host);
            response.sendRedirect(url);
        } else {
            super.doIt(request, response);
        }
    }

}
