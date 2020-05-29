package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.skynet.SkynetClientSiaWallet;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;

public class SkyfileApi extends HttpServlet {

    public static final String FILENAME_PARAMETER = "filename";
    public static final String FILE_PARAMETER = "file";

    public static final String MULTIPART_FORMDATA_TYPE = "multipart/form-data";

    private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(
        System.getProperty("java.io.tmpdir"));

    public static boolean isMultipartRequest(ServletRequest request) {
        return request.getContentType() != null
            && request.getContentType().startsWith(MULTIPART_FORMDATA_TYPE);
    }

    private SkyfileApi() {

    }

    public static SkyfileApi getInstance(){
        return new SkyfileApi();
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {
        if (Settings.SKYNET_CLIENT != SkynetClientSiaWallet.getSingletonInstance()) {
            Settings.SKYNET_CLIENT.store(null, null, request, response);
            return;
        }
        if (isMultipartRequest(request)) {
            request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
        }
        byte[] file = null;
        String fileName = null;
        if (request.getPart(FILE_PARAMETER) != null) {
            fileName = request.getPart(FILE_PARAMETER).getSubmittedFileName();
            try (InputStream inputStream = request.getPart(FILE_PARAMETER).getInputStream()) {
                file = ByteStreams.toByteArray(inputStream);
            }
        }
        if (request.getPart(FILENAME_PARAMETER) != null) {
            try (InputStream inputStream = request.getPart(FILENAME_PARAMETER).getInputStream()) {
                fileName = new String(ByteStreams.toByteArray(inputStream));
            }
        }
        Settings.SKYNET_CLIENT.store(
            fileName,
            file,
            request,
            response
        );
    }

}
