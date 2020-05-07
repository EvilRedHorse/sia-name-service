package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.cacher.SiaHostScannerCache;
import com.sawwit.integration.util.StreamReader;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class IndexApi extends SiaNameServiceApi {
    public static IndexApi getInstance(){
        return new IndexApi();
    }
    
    @Override
    public String getPath() {
        return "";
    }

    @Override
    public JSONObject getHelp() {
        JSONObject api = new JSONObject();
        api.put(WhoIsApi.getInstance().getPath(), WhoIsApi.getInstance().getHelp());
        api.put(ListApi.getInstance().getPath(), ListApi.getInstance().getHelp());
        api.put(RedirectApi.getInstance().getPath(), RedirectApi.getInstance().getHelp());
        api.put(RegisterApi.getInstance().getPath(), RegisterApi.getInstance().getHelp());
        return api;
    }

    @Override
    protected String getContent(HttpServletRequest request) {
        return StreamReader.read(IndexApi.class.getResourceAsStream("/index.html"));
    }

    private void doIt(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String content = getContent(request);
        final ByteBuffer bytes = ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8));
        final AsyncContext async = request.startAsync();
        final ServletOutputStream out = response.getOutputStream();
        out.setWriteListener(new WriteListener() {
            @Override
            public void onWritePossible() throws IOException {
                while (out.isReady()) {
                    if (!bytes.hasRemaining()) {
                        response.setContentType("text/html");
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
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        doIt(request, response);
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        doIt(request, response);
    }

}
