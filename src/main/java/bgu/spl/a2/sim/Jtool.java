package bgu.spl.a2.sim;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by חן on 30-Dec-16.
 *
 * auxilary class to create Tools from the json input file
 */
public class Jtool implements Serializable{

    @SerializedName("tool")
    private String tool;

    @SerializedName("qty")
    private Integer qty;


    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }


}
