package bgu.spl.a2;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * this class represents a single work stealing processor, it is
 * {@link Runnable} so it is suitable to be executed by threads.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 */
public class Processor implements Runnable {

    private final WorkStealingThreadPool pool;
    private final int id;

    /**
     * @return the processor's id
     */
    /*package*/ int getId() {
        return id;
    }

    /**
     * @return reference to the {@link WorkStealingThreadPool}
     */
    /*package*/ WorkStealingThreadPool getPool() {
        return pool;
    }

    /**
     * constructor for this class
     *
     * IMPORTANT:
     * 1) this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * 2) you may not add other constructors to this class
     * nor you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param id - the processor id (every processor need to have its own unique
     * id inside its thread pool)
     * @param pool - the thread pool which owns this processor
     */
    /*package*/ Processor(int id, WorkStealingThreadPool pool) {
        this.id = id;
        this.pool = pool;
    }


    /**
     * this method is the main function of processor. we fetch and start handling {@link Task}s from the processor's
     * queue until the queue is empty. when its empty we try to steal tasks from other processors using {@link WorkStealingThreadPool}
     * steal method.
     * if there's no available tasks to steal, we wait until version monitor updates.
     */
    @Override
    public void run() {
        LinkedBlockingDeque myQueue = pool.getProcessorsQueues()[id];
        while (!Thread.currentThread().isInterrupted()){
            while (!myQueue.isEmpty()){
                Task<?> taskToHandle  = (Task<?>) myQueue.pollFirst();
                if (taskToHandle != null){
                    taskToHandle.handle(this);
                }
                else break;     //queue is empty
            }
                int version = pool.getVersionMonitor().getVersion();
                if (!pool.steal(id)) {
                    if(myQueue.isEmpty()){ //double check to prevent case we submitted task to myQueue after he started the stealing process
                        try {
                            pool.getVersionMonitor().await(version);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
        }
    }
    /**
     * this method adds a task to a specific processor queue
     *
     * @param taskToAdd
     */
    protected void submitToProcessor(Task taskToAdd) {
        pool.getProcessorsQueues()[id].addFirst(taskToAdd);
    }

}
