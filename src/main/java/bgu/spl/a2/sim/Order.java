package bgu.spl.a2.sim;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by חן on 30-Dec-16.
 * this class represents a wave of product orders
 */
public class Order implements Serializable {

    @SerializedName("product")
    private String product;

    @SerializedName("qty")
    private Integer qty;

    @SerializedName("startId")
    private long startId;

    /**
     *product getter
     *@return product type
     */
    public String getProduct() {
        return product;
    }
    //TODO add javadoc
    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public long getStartId() {
        return startId;
    }

    public void setStartId(long startId) {
        this.startId = startId;
    }

}
