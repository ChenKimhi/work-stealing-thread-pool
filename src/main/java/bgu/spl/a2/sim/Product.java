package bgu.spl.a2.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class that represents a product produced during the simulation.
 */
public class Product implements java.io.Serializable {
    private String name;
    private long startId, finalId;
    private ArrayList<Product> parts;

    /**
     * Constructor
     *
     * @param startId - Product start id
     * @param name    - Product name
     */
    public Product(long startId, String name) {
        this.startId = startId;
        this.finalId = startId; //until we update it
        this.name = name;
        parts = new ArrayList<Product>();
    }

    /**
     *
     * @param startId to set
     */
    public void setStartId(long startId) {
        this.startId = startId;
    }
    /**
     * @return The product name as a string
     */
    public String getName() {
        return name;
    }

    /**
     * @return The product start ID as a long. start ID should never be changed.
     */
    public long getStartId() {
        return startId;
    }



    /**
     * @return The product final ID as a long.
     * final ID is the ID the product received as the sum of all UseOn();
     */
    public long getFinalId() {
        return finalId;
    }

    /**
     * increases final id by value
     * @param value to add to the final id
     */
    public void incFinalId(long value) {
        this.finalId = finalId + value;
    }

    /**
     * @return Returns all parts of this product as a List of Products
     */
    public List<Product> getParts(){
        return parts;
    }

    /**
     * Add a new part to the product
     *
     * @param p - part to be added as a Product object
     */
    public void addPart(Product p){
        parts.add(p);
    }

}
