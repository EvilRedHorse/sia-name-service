package test.com.georgemcarlson.sianameservice.util;

import com.georgemcarlson.sianameservice.util.Settings;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class SettingsTest {

    @Test
    public void getTldsTest() {
        Assert.assertEquals(0, Settings.getTlds(null).size());

        Assert.assertEquals(0, Settings.getTlds(new JSONObject()).size());

        JSONObject settings = new JSONObject();
        settings.put("tlds", "");
        Assert.assertEquals(0, Settings.getTlds(settings).size());

        settings.put("tlds", "1");
        Assert.assertEquals(0, Settings.getTlds(settings).size());

        JSONArray tlds = new JSONArray();
        settings.put("tlds", tlds);
        Assert.assertEquals(0, Settings.getTlds(settings).size());

        tlds = new JSONArray();
        tlds.put("sns");
        settings.put("tlds", tlds);
        Assert.assertEquals(1, Settings.getTlds(settings).size());

        tlds = new JSONArray();
        tlds.put("sns");
        tlds.put("pin");
        settings.put("tlds", tlds);
        Assert.assertEquals(2, Settings.getTlds(settings).size());
    }
}