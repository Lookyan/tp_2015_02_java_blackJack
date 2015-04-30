package main;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class ContextTest {

//    private final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
////    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
//
//    @Before
//    public void setUpStreams() {
//        System.setOut(new PrintStream(outStream));
//    }
//
//    @After
//    public void resetStreams() {
//        System.setOut(null);
//    }

    @Test
    public void testAdd() throws Exception {
        Context context = new Context();

        String str = new String("sample string");
        context.add(String.class, str);

        assertEquals(str, context.get(String.class));

        context.add(String.class, "sample 2");

        assertEquals(str, context.get(String.class));

//        str = outStream.toString().substring(13);

//        assertEquals("[main] FATAL main.Context - Trying to add existing class to context!\n", str); // 13 символов - время
    }
}