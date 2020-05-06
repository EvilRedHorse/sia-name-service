package test.com.georgemcarlson.sianameservice.util.cacher;

import com.georgemcarlson.sianameservice.util.cacher.SiaHostScanner;
import java.math.BigInteger;
import org.junit.Assert;
import org.junit.Test;

public class SiaHostScannerTest {

    @Test
    public void isArbitraryDataSiaTldTest() {
        //prove that function is protected against null data
        Assert.assertFalse(SiaHostScanner.isArbitraryDataSiaTld(null));

        //prove that function is protected against bad data
        Assert.assertFalse(
            SiaHostScanner.isArbitraryDataSiaTld(
                "hgfjlkdhgjkfdshgiroetghjfdkhgjklfdhglkjfdhsgkljfdsfdkjgh".getBytes()
            )
        );

        //prove that function is protected against short skylinks
        Assert.assertFalse(
            SiaHostScanner.isArbitraryDataSiaTld("test.sia _ArnmJ2mQAvFofAiW3qEA".getBytes())
        );

        //prove that function is protected against long skylinks
        Assert.assertFalse(
            SiaHostScanner.isArbitraryDataSiaTld(
                "test.sia _ArnmJ2mQAvFofAiW3qEAfdgkhfksdjhkljfdsglkfdsglkhjfdjkls".getBytes()
            )
        );

        //prove that a properly formatted entry works
        Assert.assertTrue(
            SiaHostScanner.isArbitraryDataSiaTld(
                "test.sia _ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ".getBytes()
            )
        );
    }

    @Test
    public void parseSkylink() {
        //prove that function is protected against null data
        Assert.assertNull(SiaHostScanner.parseSkyLink(null));

        //prove that function is protected against bad data
        Assert.assertNull(
            SiaHostScanner.parseSkyLink(
                "hgfjlkdhgjkfdshgiroetghjfdkhgjklfdhglkjfdhsgkljfdsfdkjgh".getBytes()
            )
        );

        //prove that function is protected against short skylinks
        Assert.assertNull(SiaHostScanner.parseSkyLink("test.sia _ArnmJ2mQAvFofAiW3qEA".getBytes()));

        //prove that function is protected against long skylinks
        Assert.assertNull(
            SiaHostScanner.parseSkyLink(
                "test.sia _ArnmJ2mQAvFofAiW3qEAfdgkhfksdjhkljfdsglkfdsglkhjfdjkls".getBytes()
            )
        );

        //prove that a properly formatted entry works
        Assert.assertEquals(
            "_ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ",
            SiaHostScanner.parseSkyLink(
                "test.sia _ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ".getBytes()
            )
        );
    }

    @Test
    public void parseHostTest() {
        //prove that function is protected against null data
        Assert.assertNull(SiaHostScanner.parseHost(null));

        //prove that function is protected against bad data
        Assert.assertNull(
            SiaHostScanner.parseHost(
                "hgfjlkdhgjkfdshgiroetghjfdkhgjklfdhglkjfdhsgkljfdsfdkjgh".getBytes()
            )
        );

        //prove that function is protected against short skylinks
        Assert.assertNull(SiaHostScanner.parseHost("test.sia _ArnmJ2mQAvFofAiW3qEA".getBytes()));

        //prove that function is protected against long skylinks
        Assert.assertNull(
            SiaHostScanner.parseHost(
                "test.sia _ArnmJ2mQAvFofAiW3qEAfdgkhfksdjhkljfdsglkfdsglkhjfdjkls".getBytes()
            )
        );

        //prove that a properly formatted entry works
        Assert.assertEquals(
            "test.sia",
            SiaHostScanner.parseHost(
                "test.sia _ArnmJ2mQAvFofAiW3qEA2V1t3PuPJhYvYQdp8I0_nQMXQ".getBytes()
            )
        );
    }

}
