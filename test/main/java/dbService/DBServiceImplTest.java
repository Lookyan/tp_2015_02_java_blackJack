package dbService;

import base.DBService;
import base.dataSets.UserDataSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import resourceSystem.DatabaseConfig;

import static org.junit.Assert.*;

public class DBServiceImplTest {

    DBService dbService;

    @Before
    public void setUp() throws Exception {
        DatabaseConfig config = new DatabaseConfig(
                "org.hibernate.dialect.H2Dialect",
                "org.h2.Driver",
                "jdbc:h2:mem:java_blackjack_test",
                "",
                "",
                "true",
                "create"
        );
        dbService = new DBServiceImpl(config);
    }

    @After
    public void tearDown() throws Exception {
        dbService.shutdown();
    }

    @Test
    public void testSaveAndGetUser() throws Exception {
        dbService.saveUser(new UserDataSet("andrey", "123", "a@a", 1000));

        UserDataSet dataSet = dbService.getUserData("andrey");
        assertEquals("123", dataSet.getPassword());
        assertEquals("a@a", dataSet.getEmail());
        assertEquals(1000, dataSet.getChips());
    }

    @Test
    public void testGetChipsByName() throws Exception {
        dbService.saveUser(new UserDataSet("andrey", "123", "a@a", 1000));
        dbService.saveUser(new UserDataSet("alex", "123", "a@a", 2000));
        dbService.saveUser(new UserDataSet("bob", "123", "a@a", 3000));

        assertEquals(1000, dbService.getChipsByName("andrey"));
        assertEquals(2000, dbService.getChipsByName("alex"));
        assertEquals(3000, dbService.getChipsByName("bob"));
    }

    @Test
    public void testAddChipsByName() throws Exception {
        dbService.saveUser(new UserDataSet("andrey", "123", "a@a", 1000));

        dbService.addChipsByName("andrey", 500);
        UserDataSet dataSet = dbService.getUserData("andrey");
        int c = dbService.getChipsByName("andrey");
        assertEquals(1500, c);
    }

    @Test
    public void testSubChipsByName() throws Exception {
        dbService.saveUser(new UserDataSet("andrey", "123", "a@a", 1000));

        dbService.subChipsByName("andrey", 300);
        assertEquals(700, dbService.getChipsByName("andrey"));
    }
}