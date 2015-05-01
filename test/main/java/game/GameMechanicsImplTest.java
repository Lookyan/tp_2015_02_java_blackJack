package game;

import base.GameMechanics;
import base.GameTable;
import main.Context;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameMechanicsImplTest {

    private List<GameTable> gtSpies;
    private Context context = mock(Context.class);
    private GameMechanics gm;

    private class GameTableStub implements GameTable {

        private int players = 0;

        @Override
        public boolean isFull() {
            return players == 3;
        }

        @Override
        public void addUser(String userName) throws GameTableException {
            players++;
        }

        @Override
        public void makeBet(String userName, int bet) throws GameTableException {}

        @Override
        public void hit(String userName) throws GameTableException {}

        @Override
        public void stand(String userName) throws GameTableException {}

        @Override
        public void removeUser(String userName) throws GameTableException {
            players--;
        }
    }

    @Before
    public void setUp() throws Exception {
        gtSpies = new LinkedList<>();
        gm = new GameMechanicsImpl(context, cont -> {
            GameTable gt = spy(new GameTableStub());
            gtSpies.add(gt);
            return gt;
        });
    }

    @Test
    public void testAddUser() throws Exception {
        gm.addUser("andrey");
        assertEquals(1, gtSpies.size());
        verify(gtSpies.get(0)).addUser("andrey");

        gm.addUser("alex");
        assertEquals(1, gtSpies.size());
        verify(gtSpies.get(0)).addUser("alex");

        gm.addUser("bob");
        assertEquals(1, gtSpies.size());
        verify(gtSpies.get(0)).addUser("bob");

        gm.addUser("chris");
        assertEquals(2, gtSpies.size());
        verify(gtSpies.get(1)).addUser("chris");
    }

    @Test
    public void testRemoveUser() throws Exception {
        gm.addUser("andrey");
        assertEquals(1, gtSpies.size());
        verify(gtSpies.get(0)).addUser("andrey");

        gm.addUser("alex");
        assertEquals(1, gtSpies.size());
        verify(gtSpies.get(0)).addUser("andrey");

        gm.removeUser("andrey");
        verify(gtSpies.get(0)).removeUser("andrey");

        gm.addUser("bob");
        assertEquals(1, gtSpies.size());
        verify(gtSpies.get(0)).addUser("bob");

        gm.addUser("chris");
        assertEquals(1, gtSpies.size());
        verify(gtSpies.get(0)).addUser("chris");

        gm.addUser("andrey");
        assertEquals(2, gtSpies.size());
        verify(gtSpies.get(1)).addUser("andrey");
    }

    @Test
    public void testMakeBet() throws Exception {
        gm.addUser("andrey");
        gm.addUser("alex");
        gm.addUser("bob");
        gm.addUser("chris");

        gm.makeBet("andrey", 100);
        gm.makeBet("alex", 200);
        gm.makeBet("chris", 400);
        gm.makeBet("bob", 300);

        verify(gtSpies.get(0)).makeBet("andrey", 100);
        verify(gtSpies.get(0)).makeBet("alex", 200);
        verify(gtSpies.get(0)).makeBet("bob", 300);
        verify(gtSpies.get(1)).makeBet("chris", 400);
    }

    @Test
    public void testHit() throws Exception {
        gm.addUser("andrey");
        gm.addUser("alex");
        gm.addUser("bob");
        gm.addUser("chris");

        gm.hit("andrey");
        gm.hit("alex");
        gm.hit("chris");
        gm.hit("bob");

        verify(gtSpies.get(0)).hit("andrey");
        verify(gtSpies.get(0)).hit("alex");
        verify(gtSpies.get(0)).hit("bob");
        verify(gtSpies.get(1)).hit("chris");
    }

    @Test
    public void testStand() throws Exception {
        gm.addUser("andrey");
        gm.addUser("alex");
        gm.addUser("bob");
        gm.addUser("chris");

        gm.stand("andrey");
        gm.stand("alex");
        gm.stand("chris");
        gm.stand("bob");

        verify(gtSpies.get(0)).stand("andrey");
        verify(gtSpies.get(0)).stand("alex");
        verify(gtSpies.get(0)).stand("bob");
        verify(gtSpies.get(1)).stand("chris");
    }
}