package messageSystem;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

import static org.junit.Assert.*;

public class MessageSystemTest {

    private MessageSystem messageSystem;

    private class TestAbonent implements Abonent {

        private Address address = new Address();

        @Override
        public Address getAddress() {
            return address;
        }

        public void work(String str) {
//            ...
        }
    }

    private abstract class MessageToTestAbonent extends Message {
        public MessageToTestAbonent(Address from, Address to) {
            super(from, to);
        }

        @Override
        public void exec(Abonent abonent) {
            if (abonent instanceof TestAbonent) {
                exec((TestAbonent) abonent);
            }
        }

        protected abstract void exec(TestAbonent testAbonent);
    }

    private class MessageTest extends MessageToTestAbonent {
        private String str;

        public MessageTest(Address from, Address to, String str) {
            super(from, to);
            this.str = str;
        }

        @Override
        protected void exec(TestAbonent testAbonent) {
            testAbonent.work(str);
        }

        public String getStr() {
            return str;
        }
    }

    @Before
    public void setUp() throws Exception {
        messageSystem = new MessageSystem();
    }

    @Test
    public void testMessage() throws Exception {
        TestAbonent spyAbonent = spy(new TestAbonent());
        messageSystem.addService(spyAbonent);

        messageSystem.sendMessage(new MessageTest(null, spyAbonent.getAddress(), "example"));

        messageSystem.execForAbonent(spyAbonent);

        verify(spyAbonent).work("example");

    }

    @Test
    public void testManyMessages() throws Exception {
        TestAbonent spyAbonent1 = spy(new TestAbonent());
        TestAbonent spyAbonent2 = spy(new TestAbonent());
        messageSystem.addService(spyAbonent1);
        messageSystem.addService(spyAbonent2);

        messageSystem.sendMessage(new MessageTest(null, spyAbonent1.getAddress(), "example1.1"));
        messageSystem.sendMessage(new MessageTest(null, spyAbonent2.getAddress(), "example2.1"));
        messageSystem.sendMessage(new MessageTest(null, spyAbonent1.getAddress(), "example1.2"));
        messageSystem.sendMessage(new MessageTest(null, spyAbonent2.getAddress(), "example2.2"));

        messageSystem.execForAbonent(spyAbonent1);

        verify(spyAbonent1).work("example1.1");
        verify(spyAbonent1).work("example1.2");

        messageSystem.execForAbonent(spyAbonent2);
        verify(spyAbonent2).work("example2.1");
        verify(spyAbonent2).work("example2.2");

    }

}