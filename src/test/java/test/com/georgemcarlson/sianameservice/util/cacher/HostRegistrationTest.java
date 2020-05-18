package test.com.georgemcarlson.sianameservice.util.cacher;

import com.georgemcarlson.sianameservice.util.cacher.HostRegistration;
import org.junit.Assert;
import org.junit.Test;

public class HostRegistrationTest {

    @Test
    public void constructorTest() {
        //prove that function is protected against null data
        Assert.assertEquals(
            HostRegistration.getInvalidSingletonInstance(),
            HostRegistration.getInstance(null, null)
        );

        //prove that function is protected against bad data
        Assert.assertEquals(
            HostRegistration.getInvalidSingletonInstance(),
            HostRegistration.getInstance(
                "hgfjlkdhgjkfdshgiroetghjfdkhgjklfdhglkjfdhsgkljfdsfdkjgh".getBytes(),
                null
            )
        );

        //prove that function is protected against short skylinks
        Assert.assertEquals(
            HostRegistration.getInvalidSingletonInstance(),
            HostRegistration.getInstance("test.sns _ArnmJ2mQAvFofAiW3qEA".getBytes(), null)
        );

        //prove that function is protected against long skylinks
        Assert.assertEquals(
            HostRegistration.getInvalidSingletonInstance(),
            HostRegistration.getInstance(
                "test.sns _ArnmJ2mQAvFofAiW3qEAfdgkhfksdjhkljfdsglkfdsglkhjfdjkls".getBytes(),
                null
            )
        );

        //prove that a properly formatted entry works
        Assert.assertTrue(
            HostRegistration.getInstance(
                "test.sns _ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ".getBytes(),
                null
            ).isValid()
        );
    }

    @Test
    public void parseSkylink() {
        //prove that a properly formatted entry works
        Assert.assertEquals(
            "_ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ",
            HostRegistration.getInstance(
                "test.sns _ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ".getBytes(),
                null
            ).getSkyLink()
        );
    }

    @Test
    public void parseHostTest() {
        //prove that a properly formatted entry works
        Assert.assertEquals(
            "test.sns",
            HostRegistration.getInstance(
                "test.sns _ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ".getBytes(),
                null
            ).getHost()
        );
    }

}
