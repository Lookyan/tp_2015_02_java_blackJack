package resourceSystem;

import base.Resource;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResourceFactoryTest {

    @Test
    public void testInit() throws Exception {
        ResourceFactory resourceFactory = ResourceFactory.getInstance();
        resourceFactory.init("test/files");

        TestFile A = (TestFile) resourceFactory.get("test/files/a.xml");
        assertNotNull(A);
        assertEquals(23, A.getIntField());
        assertEquals("hello world", A.getStrField());

        TestFile B = (TestFile) resourceFactory.get("test/files/b.xml");
        assertNotNull(A);
        assertEquals(64, B.getIntField());
        assertEquals("goodbye", B.getStrField());

        assertNull(resourceFactory.get("test/files/c.xml"));
        assertNull(resourceFactory.get("test/files/d.xml"));
    }
}