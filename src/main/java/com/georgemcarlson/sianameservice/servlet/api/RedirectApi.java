package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.cacher.SiaHostScannerCache;
import com.sawwit.integration.util.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class RedirectApi extends SiaNameServiceApi {
    private static final Logger LOGGER = Logger.getInstance();
    public static final String PATH = "/redirect";
    public static final String HELP_PARAMETER = "help";
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

    protected byte[] getRaw(HttpServletRequest request) throws IOException{
        String host = request.getParameter(HOST_PARAMETER);
        String portal = request.getParameter(PORTAL_PARAMETER);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        Request.Builder requestBuilder = new Request.Builder();
        String url = "http://" + portal + "/" + getSkyLink(host);
        System.out.println(url);
        requestBuilder.url(url);
        requestBuilder.get();

        Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
        return response.body().bytes();
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
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final ByteBuffer bytes = ByteBuffer.wrap(getRaw(request));
        final AsyncContext async = request.startAsync();
        final ServletOutputStream out = response.getOutputStream();
        out.setWriteListener(new WriteListener() {
            @Override
            public void onWritePossible() throws IOException {
                while (out.isReady()) {
                    if (!bytes.hasRemaining()) {
                        response.setContentType("application/json");
                        response.setStatus(200);
                        async.complete();
                        return;
                    }
                    out.write(bytes.get());
                }
            }

            @Override
            public void onError(Throwable t) {
                getServletContext().log("Async Error", t);
                async.complete();
            }
        });
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final ByteBuffer bytes = ByteBuffer.wrap(getRaw(request));
        final AsyncContext async = request.startAsync();
        final ServletOutputStream out = response.getOutputStream();
        out.setWriteListener(new WriteListener() {
            @Override
            public void onWritePossible() throws IOException {
                while (out.isReady()) {
                    if (!bytes.hasRemaining()) {
                        response.setContentType("application/json");
                        response.setStatus(200);
                        async.complete();
                        return;
                    }
                    out.write(bytes.get());
                }
            }

            @Override
            public void onError(Throwable t) {
                getServletContext().log("Async Error", t);
                async.complete();
            }
        });
    }

}
