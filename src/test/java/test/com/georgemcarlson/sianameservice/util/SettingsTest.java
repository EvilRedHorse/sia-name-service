package test.com.georgemcarlson.sianameservice.util;

import com.georgemcarlson.sianameservice.util.Settings;
import com.georgemcarlson.sianameservice.util.skynet.SkynetClientPortal;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class SettingsTest {

    @Test
    public void canSaveTest() {
        String settingsTestPath = "settings_test.json";

        //test can save public
        Assert.assertTrue(Settings.isPublic());
        Settings.setPublic(false);
        Settings.persistSettings(settingsTestPath);
        Assert.assertFalse(Settings.getSettings(settingsTestPath).getBoolean("public"));

        //test can save enable_upnp
        Assert.assertTrue(Settings.getEnabledUpnp());
        Settings.setEnabledUpnp(false);
        Settings.persistSettings(settingsTestPath);
        Assert.assertFalse(Settings.getSettings(settingsTestPath).getBoolean("enabled_upnp"));

        //test can save port
        Assert.assertEquals(8080, Settings.getPort());
        Settings.setPort(80);
        Settings.persistSettings(settingsTestPath);
        Assert.assertEquals(80, Settings.getSettings(settingsTestPath).getInt("port"));

        //test can save fee
        Assert.assertEquals(1, Settings.getFee());
        Settings.setFee(2);
        Settings.persistSettings(settingsTestPath);
        Assert.assertEquals(2, Settings.getSettings(settingsTestPath).getInt("fee"));

        //test can save genesis block
        Assert.assertEquals(258549, Settings.getGenesisBlock());
        Settings.setGenesisBlock(500000);
        Settings.persistSettings(settingsTestPath);
        Assert.assertEquals(500000, Settings.getSettings(settingsTestPath).getInt("genesis_block"));

        //test can save wallet api port
        Assert.assertEquals(4280, Settings.getWalletApiPort());
        Settings.setWalletApiPort(9000);
        Settings.persistSettings(settingsTestPath);
        Assert.assertEquals(9000, Settings.getSettings(settingsTestPath).getInt("wallet_api_port"));

        //test can save wallet api port
        Assert.assertEquals("", Settings.getWalletApiPassword());
        Settings.setWalletApiPassword("a_password");
        Settings.persistSettings(settingsTestPath);
        Assert.assertEquals(
            "a_password",
            Settings.getSettings(settingsTestPath).getString("wallet_api_password")
        );

        //test can save wallet api port
        Assert.assertEquals("ScPrime-Agent", Settings.getWalletApiUserAgent());
        Settings.setWalletApiUserAgent("Generic-User");
        Settings.persistSettings(settingsTestPath);
        Assert.assertEquals(
            "Generic-User",
            Settings.getSettings(settingsTestPath).getString("wallet_api_user_agent")
        );

        //test can save wallet api port
        Assert.assertEquals("sns", Settings.getTlds().get(0));
        Settings.setTlds(Collections.singletonList("pin"));
        Settings.persistSettings(settingsTestPath);
        Assert.assertEquals(
            "pin",
            Settings.getSettings(settingsTestPath).getJSONArray("tlds").getString(0)
        );

        //test can save wallet api port
        Assert.assertEquals("localhost", Settings.getSkynetClient().getName());
        Settings.setSkynetClient(SkynetClientPortal.getInstance("scp.techandsupply.ca"));
        Settings.persistSettings(settingsTestPath);
        Assert.assertEquals(
            "scp.techandsupply.ca",
            Settings.getSettings(settingsTestPath).getString("portal")
        );
    }

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
