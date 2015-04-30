package game;

import base.GameMechanics;
import base.GameTable;
import main.Context;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameMechanicsImplTest {

    private List<GameTable> gtMocks;
    private final Context context = mock(Context.class);
    private GameMechanics gm;

    @Before
    public void setUp() throws Exception {
        gtMocks = new LinkedList<>();
        gm = new GameMechanicsImpl(context, cont -> {
            GameTable gt = mock(GameTable.class);
            when(gt.isFull()).thenReturn(false, false, true);
            gtMocks.add(gt);
            return gt;
        });
    }

    @Test
    public void testAddUser() throws Exception {
        gm.addUser("andrey");
        assertEquals(1, gtMocks.size());
        verify(gtMocks.get(0), times(1)).addUser("andrey");

        gm.addUser("alex");
        assertEquals(1, gtMocks.size());
        verify(gtMocks.get(0), times(1)).addUser("alex");

        gm.addUser("bob");
        assertEquals(1, gtMocks.size());
        verify(gtMocks.get(0), times(1)).addUser("bob");

        gm.addUser("chris");
        assertEquals(2, gtMocks.size());
        verify(gtMocks.get(1), times(1)).addUser("chris");
    }

    @Test
    public void testRemoveUser() throws Exception {

//        TODO: write stub instead of mock (control users to 3)
        gm.addUser("andrey");
        assertEquals(1, gtMocks.size());
        verify(gtMocks.get(0), times(1)).addUser("andrey");

        gm.addUser("alex");
        assertEquals(1, gtMocks.size());
        verify(gtMocks.get(0), times(1)).addUser("andrey");

        gm.removeUser("andrey");
        verify(gtMocks.get(0), times(1)).removeUser("andrey");

        gm.addUser("bob");
        assertEquals(1, gtMocks.size());
        verify(gtMocks.get(0), times(1)).addUser("bob");

        gm.addUser("chris");
        assertEquals(1, gtMocks.size());
        verify(gtMocks.get(0), times(1)).addUser("chris");

//        gm.addUser("alex");
//        assertEquals(2, gtMocks.size());
//        verify(gtMocks.get(1), times(1)).addUser("alex");
    }

    @Test
    public void testMakeBet() throws Exception {

    }

    @Test
    public void testHit() throws Exception {

    }

    @Test
    public void testStand() throws Exception {

    }
}