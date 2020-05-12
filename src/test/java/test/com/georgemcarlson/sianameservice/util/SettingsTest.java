package test.com.georgemcarlson.sianameservice.util;

import com.georgemcarlson.sianameservice.util.Settings;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class SettingsTest {

    @Test
    public void getTldsTest() {
        String key = "tlds";

        Assert.assertEquals(0, Settings.optStrings(null, key).size());

        Assert.assertEquals(0, Settings.optStrings(new JSONObject(), key).size());

        JSONObject settings = new JSONObject();
        settings.put(key, "");
        Assert.assertEquals(0, Settings.optStrings(settings, key).size());

        settings.put(key, "1");
        Assert.assertEquals(0, Settings.optStrings(settings, key).size());

        JSONArray tlds = new JSONArray();
        settings.put(key, tlds);
        Assert.assertEquals(0, Settings.optStrings(settings, key).size());

        tlds = new JSONArray();
        tlds.put("sns");
        settings.put(key, tlds);
        Assert.assertEquals(1, Settings.optStrings(settings, key).size());

        tlds = new JSONArray();
        tlds.put("sns");
        tlds.put("pin");
        settings.put(key, tlds);
        Assert.assertEquals(2, Settings.optStrings(settings, key).size());
    }

}
