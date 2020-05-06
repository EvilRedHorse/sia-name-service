package test.com.georgemcarlson.sianameservice.util;

import com.georgemcarlson.sianameservice.util.TxOutputEncoder;
import com.georgemcarlson.sianameservice.util.cacher.AddressCache;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class TxOutputEncoderTest {

    @Test
    public void canRetrieve() {
        List<String> pool = Arrays.asList(AddressCache.getDevFundAddresses());
        String data = "test";
        String registrant
            = "4397ec4472eeab4665faf92a8fb935ed2d246f232177001ca6dc6533b2e14dfee38ca2072cdf";
        int fee = 5; //11111SC

        Assert.assertEquals(
            data,
            new String(
                TxOutputEncoder.decodeArbitraryData(
                    TxOutputEncoder.encodeArbitraryData(
                        pool,
                        data.getBytes(),
                        registrant,
                        fee
                    )
                )
            )
        );
    }

}
