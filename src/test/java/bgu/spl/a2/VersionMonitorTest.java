package bgu.spl.a2;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

/**
 * Created by Chen on 12-Dec-16.
 */
public class VersionMonitorTest {
    //Thread first, second;
    private VersionMonitor  versionMonitor;
    private WorkStealingThreadPool pool;
    @Before
    public void setUp() throws Exception {
        versionMonitor = new VersionMonitor();
        pool = new WorkStealingThreadPool(2);
        //first = new Thread(()-> System.out.println("first thread is in the house"));
        //second = new Thread(()-> System.out.println("second thread is in the house"));
    }


    @Test
    public void getVersion() throws Exception {
        assertEquals(0, versionMonitor.getVersion());

    }

    @Test
    public void inc() throws Exception {
        int currVersion = versionMonitor.getVersion();
        versionMonitor.inc();
        assertTrue(versionMonitor.getVersion()==currVersion+1);
    }

    @Test
    public void await() throws Exception {
        final ByteArrayOutputStream threadOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(threadOutput));
        Thread first = new Thread(()-> {
            try {
                versionMonitor.await(versionMonitor.getVersion());
            } catch (InterruptedException e) {
                System.out.println("Interrupted Exception on AWAIT method "+e.getMessage());
            }
            System.out.print("first");
        });
        Thread second = new Thread(()->{
            System.out.print("second before ");
            versionMonitor.inc();
        });
        first.start();

        while(first.getState()!= Thread.State.WAITING)
        {
        }
        second.start();
        Thread.currentThread().sleep(3000);
        String ans = threadOutput.toString();
        assertEquals("second before first", ans);

    }

}