package com.georgemcarlson.sianameservice.servlet.api;

import com.georgemcarlson.sianameservice.util.Logger;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public class RedirectorDownload extends SiaNameServiceApi {

    private static final Logger LOGGER = Logger.getInstance();
    public static final String PATH = "/redirector";
    public static final String HELP_PARAMETER = "help";
    public static final String SIA_NAME_SERVICE_PORTAL_PARAMETER = "sianameserviceportal";

    public static RedirectorDownload getInstance(){
        return new RedirectorDownload();
    }

    public JSONObject getHelp() {
        JSONObject api = new JSONObject();
        api.put("path", PATH);
        JSONArray parameters = new JSONArray();
        parameters.put("(String) " + SIA_NAME_SERVICE_PORTAL_PARAMETER);
        api.put("parameters", parameters);
        return api;
    }

    @Override
    protected String[] getContentDisposition() {
        return new String[] {
            "Content-Disposition",
            "attachment; filename=sia-name-service-redirector-config.json"
        };
    }
    
    @Override
    protected String getContent(HttpServletRequest request) {
        if(!request.getParameterNames().hasMoreElements()){
            return getHelp().toString(2);
        } else if(request.getParameter(HELP_PARAMETER)!=null){
            return getHelp().toString(2);
        }
        try (InputStream index
                 = IndexApi.class.getResourceAsStream("/redirector_config_template.json")
        ) {
            return String.format(
                new String(ByteStreams.toByteArray(index)),
                request.getParameter(SIA_NAME_SERVICE_PORTAL_PARAMETER)
            );
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return "";
        }
    }

}
