package bgu.spl.a2;

import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * represents a work stealing thread pool - to understand what this class does
 * please refer to your assignment.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class WorkStealingThreadPool {

    private LinkedBlockingDeque<Task<?>>[] processorsQueues;
    private Thread[] threadsArr;
    private VersionMonitor versionMonitor;

    /**
     * creates a {@link WorkStealingThreadPool} which has nthreads
     * {@link Processor}s. Note, threads should not get started until calling to
     * the {@link #start()} method.
     * <p>
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this
     *                 thread pool
     */
    public WorkStealingThreadPool(int nthreads) {
        processorsQueues = new LinkedBlockingDeque[nthreads];
        threadsArr = new Thread[nthreads];
        versionMonitor = new VersionMonitor();
        for (int i = 0; i < nthreads; i++) {
            processorsQueues[i] = new LinkedBlockingDeque<Task<?>>();
        }
    }

    /**
     * processorsQueues getter
     *
     * @return the array of the processors' queues
     */
    /*package*/ LinkedBlockingDeque<Task<?>>[] getProcessorsQueues() {
        return processorsQueues;
    }

    /**
     * ThreadsArr getter
     *
     * @return the array of the threads
     */
    /*package*/ Thread[] getThreadsArr() {
        return threadsArr;
    }

    /**
     * versionMonitor getter
     *
     * @return the version monitor
     */
    /*package*/ VersionMonitor getVersionMonitor() {
        return versionMonitor;
    }

    /**
     * submits a task to be executed by a processor belongs to this thread pool
     *
     * @param task the task to execute
     */
    public void submit(Task<?> task) {
        processorsQueues[new Random().nextInt(processorsQueues.length)].addFirst(task);
        versionMonitor.inc();
    }

    /**
     * closes the thread pool - this method interrupts all the threads and wait
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     * <p>
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException          if the thread that shut down the threads is interrupted
     * @throws UnsupportedOperationException if the thread that attempts to shutdown the queue is itself a
     *                                       processor of this queue
     */
    public void shutdown() throws InterruptedException {
        for (Thread thread : threadsArr) {
            if (Thread.currentThread().getName().equals(thread.getName()))
                throw new UnsupportedOperationException("the thread that shut down belongs to the pool");
        }
        if (Thread.interrupted()) {
            throw new InterruptedException("the thread that shut down is interrupted");
        }
        for (Thread thread : threadsArr) {
            thread.interrupt();
        }

    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
        for (int i = 0; i < threadsArr.length; i++) {
            threadsArr[i] = new Thread(new Processor(i, this), "Thread id: " + i);
            threadsArr[i].start();
        }
    }


    /**
     * this method implements the work stealing process.
     * @param processorId - id of the processor that looking to steal some tasks
     * @return true if the processor managed to steal, or false otherwise.
     */
    /*package*/  boolean steal(int processorId) {

        int len = processorsQueues.length;
        for (int i = 1; i < len; i++) { //for each processor queue except myself
            int index = (i + processorId) % len;
            if (processorsQueues[index].size()>1){
                int size = processorsQueues[index].size()/2;
                for (int n = 0; n < size; n++){
                    Task<?> taskToSteal = processorsQueues[index].pollLast();
                    if(taskToSteal!=null){
                        processorsQueues[processorId].addFirst(taskToSteal);
                    }
                    else{
                        break; //the victim's queue is empty
                    }
                }
                return true;
            }
        }

        return false;
    }

}
