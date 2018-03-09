package bgu.spl.a2;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Describes a monitor that supports the concept of versioning - its idea is
 * simple, the monitor has a version number which you can receive via the method
 * {@link #getVersion()} once you have a version number, you can call
 * {@link #await(int)} with this version number in order to wait until this
 * version number changes.
 * <p>
 * you can also increment the version number by one using the {@link #inc()}
 * method.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class VersionMonitor {
    private AtomicInteger version = new AtomicInteger(0);

    /**
     *
     * @return version - the current version number
     */
    public int getVersion() {
        return version.get();
    }


    /**
     * this method increment the version by one and notifies all the processors/threads that wate for the
     * state of the queues of tasks to change.
     * we had to synchronize this method so we could use the notifyAll method
     */
    public synchronized void inc() {
        version.incrementAndGet();
        try {
            notifyAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * makes the thread go  to sleep until the version monitor is updated
     * @param version - the version we should wait on, and if it changes the threds sould wake up and
     * try to steal tasks again
     * @throws InterruptedException
     */
    public synchronized void await(int version) throws InterruptedException {
        while (this.version.get() == version) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;

            }
        }
    }
}
