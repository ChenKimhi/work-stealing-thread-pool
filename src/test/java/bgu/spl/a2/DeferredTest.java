package bgu.spl.a2;

import jdk.internal.util.xml.impl.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by חן on 12-Dec-16.
 */
public class DeferredTest {
    private Deferred deferred;
    @Before
    public void setUp() throws Exception
    {
        deferred = new Deferred();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test (expected = IllegalStateException.class)
    public void testGet() throws Exception {
        assertEquals("this object is not yet resolved" , deferred.get());
        deferred.resolve(new Integer(5));
        assertEquals(5, deferred.get());
    }

    @Test
    public void testIsResolved() throws Exception {
        assertFalse(deferred.isResolved());
        deferred.resolve(new Integer(2));
        assertTrue(deferred.isResolved());

    }

    @Test (expected = IllegalStateException.class)
    public void testResolve() throws Exception {
        assertFalse(deferred.isResolved());
        assertEquals("this object is not yet resolved",deferred.get());
        deferred.resolve(new Integer(18));
        assertNotNull(deferred.get());
        assertEquals(18, deferred.get());
        assertTrue(deferred.isResolved());

    }

    @Test
    public void testWhenResolved() throws Exception {
        int numOfCallBacks  = deferred.callBacks.size();
        deferred.whenResolved( () ->{
                System.out.println("im running in whenResolve Test");});
        assertEquals(numOfCallBacks+1, deferred.callBacks.size());

    }


}