package bgu.spl.a2;

import java.util.ArrayList;


/**
 * this class represents a deferred result i.e., an object that eventually will
 * be resolved to hold a result of some operation, the class allows for getting
 * the result once it is available and registering a callback that will be
 * called once the result is available.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 * @param <T> the result type
 */
public class Deferred<T> {


    protected ArrayList<Runnable> callBacks;
    private  T result;
    private boolean isResolved =  false;


    /**
     * constructor
     */
    public Deferred() {
        callBacks = new ArrayList<Runnable>();
    }

    /**
     *
     * @return the resolved value if such exists (i.e., if this object has been
     * {@link #resolve(java.lang.Object)}ed yet
     * @throws IllegalStateException in the case where this method is called and
     * this object is not yet resolved
     */
    public T get() {
        if (isResolved())
            return result;
        else
            throw new IllegalStateException("this object is not yet resolved");

    }

    /**
     *
     * @return true if this object has been resolved - i.e., if the method
     * {@link #resolve(java.lang.Object)} has been called on this object before.
     */
    public boolean isResolved() {
        return isResolved;
    }

    /**
     * resolve this deferred object - from now on, any call to the method
     * {@link #get()} should return the given value
     *
     * Any callbacks that were registered to be notified when this object is
     * resolved via the {@link #whenResolved(java.lang.Runnable)} method should
     * be executed before this method returns
     *
     * we synchronized this method to prevent concurrent access to the {@link ArrayList} of the callbacks
     * @param value - the value to resolve this deferred object with
     * @throws IllegalStateException in the case where this object is already
     * resolved
     */
    public synchronized void resolve(T value) {
        if (isResolved())
            throw new IllegalStateException("this object  is already resolved");
        else{
            result = value;
            isResolved =  true;
            for(int i = 0; i < callBacks.size();i++)
                callBacks.get(i).run();
            callBacks.clear();
        }
    }

    /**
     * add a callback to be called when this object is resolved. if while
     * calling this method the object is already resolved - the callback should
     * be called immediately
     *
     * Note that in any case, the given callback should never get called more
     * than once, in addition, in order to avoid memory leaks - once the
     * callback got called, this object should not hold its reference any
     * longer.
     *
     * @param callback the callback to be called when the deferred object is
     * resolved
     */
    public synchronized void whenResolved(Runnable callback) {
        /**we added the synchronization for the case that the deffered object was resolved right after isResolved returned false*/
        if (isResolved())
            callback.run();
        else callBacks.add(callback);
    }
}
