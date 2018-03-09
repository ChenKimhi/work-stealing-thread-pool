package bgu.spl.a2.sim;


import bgu.spl.a2.sim.conf.ManufactoringPlan;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by חן on 30-Dec-16.
 *
 * auxilary class to read from the input JSON file
 */
public class JReader implements Serializable{

    @SerializedName("threads")
    private Integer threads;

    @SerializedName("tools")
    private Jtool[] tools;

    @SerializedName("plans")
    private ManufactoringPlan[] plans;

    @SerializedName("waves")
    private Order[][] waves;


    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Jtool[] getTools() {
        return tools;
    }

    public void setTools(Jtool[] tools) {
        this.tools = tools;
    }

    public ManufactoringPlan[] getPlans() {
        return plans;
    }

    public void setPlans(ManufactoringPlan[] plans) {
        this.plans = plans;
    }

    public Order[][] getWaves() {
        return waves;
    }

    public void setWaves(Order[][] waves) {
        this.waves = waves;
    }

}