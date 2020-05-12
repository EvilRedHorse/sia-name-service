package test.com.georgemcarlson.sianameservice.util.reader.user;

import com.georgemcarlson.sianameservice.util.reader.User;
import org.junit.Test;

public class AddressRequestTest {

    @Test
    public void canSend() {
        User user = User.getSingletonInstance();
        System.out.println(user.getAddresses().size());
        System.out.println(user.getAddresses().size());
        System.out.println(user.getAddresses().size());
        System.out.println(user.getAddresses().size());
    }

}
