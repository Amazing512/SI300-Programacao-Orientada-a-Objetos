package UnitTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.unicamp.poo.dao.impl.mariadb.MariaDBConnection;

class MariaDBConnectionTest {

    @AfterEach
    void tearDown() {
        MariaDBConnection.resetInstance();
    }

    @Test
    void returnsSameInstance() {
        MariaDBConnection first = MariaDBConnection.getInstance("WindServer");
        MariaDBConnection second = MariaDBConnection.getInstance("WindServer");
        MariaDBConnection third = MariaDBConnection.getInstance();

        assertNotNull(first);
        assertSame(first, second);
        assertSame(first, third);
    }

    @Test
    void createsNewInstanceAfterReset() {
        MariaDBConnection first = MariaDBConnection.getInstance("WindServer");
        MariaDBConnection.resetInstance();
        MariaDBConnection second = MariaDBConnection.getInstance("WindServer");

        assertNotNull(first);
        assertNotNull(second);
    }

    public static void main(String[] args) {
        MariaDBConnectionTest test = new MariaDBConnectionTest();
        test.returnsSameInstance();
        test.tearDown();
        test.createsNewInstanceAfterReset();
        test.tearDown();
        System.out.println("MariaDBConnection Singleton tests PASSED successfully!");
    }
}
