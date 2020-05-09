package com.georgemcarlson.sianameservice.servlet.api;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class SiaNameServiceApi extends HttpServlet {

    protected abstract String getContent(HttpServletRequest request)
        throws ServletException, IOException;

    protected String getContentType() {
        return "application/json";
    }

    protected void doIt(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {
        final String content = getContent(request);
        final ByteBuffer bytes = ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8));
        final AsyncContext async = request.startAsync();
        final ServletOutputStream out = response.getOutputStream();
        out.setWriteListener(new WriteListener() {
            @Override
            public void onWritePossible() throws IOException {
                while (out.isReady()) {
                    if (!bytes.hasRemaining()) {
                        response.setContentType(getContentType());
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
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {
        doIt(request,response);
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {
        doIt(request,response);
    }

}
