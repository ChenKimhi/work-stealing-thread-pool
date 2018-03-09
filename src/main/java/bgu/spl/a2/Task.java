package bgu.spl.a2;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * an abstract class that represents a task that may be executed using the
 * {@link WorkStealingThreadPool}
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the task result type
 */
public abstract class Task<R> {

    private Processor currentHandler = null;
    private Deferred result = new Deferred();

    /**
     * start handling the task - note that this method is protected, a handler
     * cannot call it directly but instead must use the
     * {@link #handle(bgu.spl.a2.Processor)} method
     */
    protected abstract void start();

    /**
     * start/continue handling the task
     * <p>
     * this method should be called by a processor in order to start this task
     * or continue its execution in the case where it has been already started,
     * any sub-tasks / child-tasks of this task should be submitted to the queue
     * of the handler that handles it currently
     * <p>
     * IMPORTANT: this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * @param handler the handler that wants to handle the task
     */
    /*package*/
    final void handle(Processor handler) {
        if(currentHandler == null){
            currentHandler = handler;
            this.start();
        }
        else currentHandler = handler;
    }

    /**
     * This method schedules a new task (a child of the current task) to the
     * same processor which currently handles this task.
     * <p>
     * uses {@link #currentHandler} addTaskToQueue method
     *
     * @param task the task to execute
     * @throws NullPointerException exception
     */
    protected final void spawn(Task<?>... task) throws NullPointerException {

        for (Task<?> taskToAdd : task) {
            if (taskToAdd == null) {
                throw new NullPointerException("spawn function got null task");
            } else {
                currentHandler.submitToProcessor(taskToAdd);
                currentHandler.getPool().getVersionMonitor().inc(); // update the version after submitting the task
            }
        }
    }

    /**
     * add a callback to be executed once *all* the given tasks results are
     * resolved
     * <p>
     * Implementors note: make sure that the callback is running only once when
     * all the given tasks completed.
     *
     * @param tasks    a collection of tasks that the current task depend on
     * @param callback the callback to execute once all the results are resolved
     */
    protected final void whenResolved(Collection<? extends Task<?>> tasks, Runnable callback) {
        AtomicInteger count = new AtomicInteger(tasks.size());
        for (Task<?> task : tasks) {
            task.result.whenResolved(()->{
                    count.decrementAndGet();
                    if(count.get() == 0){
                        callback.run();
                    }
            });
        }
    }

    /**
     * resolve the internal result - should be called by the task derivative
     * once it is done.
     *
     * @param result - the task calculated result
     */
    protected final void complete(R result) {
        this.result.resolve(result);

    }

    /**
     * @return result - this task's deferred result
     */
    public final Deferred<R> getResult() {
        return result;
    }

    /**
     *
     * @return currentHandler the current processor handling this  task
     */
    public Processor getCurrentHandler() {
        return currentHandler;
    }
}
