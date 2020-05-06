package test.com.georgemcarlson.sianameservice.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Test;

public class BlockHeightTest {
    private static final String URL = "http://localhost:9980/consensus";
    private static final String USER_AGENT = "Sia-Agent";
    private static final int TIMEOUT = 30000;
    
    @Test
    public void canRetrieve() throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(URL);
        requestBuilder.header("User-Agent", USER_AGENT);
        
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
        clientBuilder.readTimeout(TIMEOUT, TimeUnit.SECONDS);
            
        Response response = clientBuilder.build().newCall(requestBuilder.build()).execute();
        System.out.println(response.body().string());
    }
}