package test.com.georgemcarlson.sianameservice.util;

import com.georgemcarlson.sianameservice.servlet.api.RegisterApi;
import com.georgemcarlson.sianameservice.util.TxOutputEncoder;
import com.georgemcarlson.sianameservice.util.cacher.AddressCache;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class RegisterApiTest {

    @Test
    public void isHostValidTest() {
        Assert.assertFalse(RegisterApi.isHostValid(null));
        Assert.assertFalse(RegisterApi.isHostValid("http://test.sns"));
        Assert.assertFalse(RegisterApi.isHostValid("t e s t.sns"));
        Assert.assertTrue(RegisterApi.isHostValid("test.sns"));
        Assert.assertTrue(RegisterApi.isHostValid("test.test.sns"));
    }

}
