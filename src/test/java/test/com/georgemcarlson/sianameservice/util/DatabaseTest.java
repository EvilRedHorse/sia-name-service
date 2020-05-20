package test.com.georgemcarlson.sianameservice.util;

import com.georgemcarlson.sianameservice.util.persistence.EntityController;
import com.georgemcarlson.sianameservice.util.persistence.Scanner;
import com.georgemcarlson.sianameservice.util.persistence.WhoIs;
import java.sql.SQLException;
import org.junit.Test;

public class DatabaseTest {
    
    @Test
    public void canRetrieve() throws Exception {
        main();
    }

    public void main() throws SQLException {
        System.out.println(Scanner.findHighest().getBlock());
    }    // main()
}