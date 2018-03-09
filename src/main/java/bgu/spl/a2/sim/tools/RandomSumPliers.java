package bgu.spl.a2.sim.tools;

import java.util.Random;

import bgu.spl.a2.sim.Product;


public class RandomSumPliers implements Tool {

    /**
     * @param id the product's part's id
     * @return the sum of pliers
     */
    private long sumOfPliers(long id) {
        Random randomSumOfPliers;
        long sumPliers = 0;
        randomSumOfPliers = new Random(id);
        for (int i = 0; i < id % 10000; i++)
            sumPliers = sumPliers + randomSumOfPliers.nextInt();
        return sumPliers;
    }

    /**
     * @return the tool type
     * @Override
     */
    public String getType() {
        return "rs-pliers";
    }

    /**
     * @return a long describing the result of tool use on Product package
     * @Override
     */
    public long useOn(Product p) {
        long value = 0;
        for (Product part : p.getParts()) {
            //value += Math.abs(sumOfPliers(part.getFinalId()));
            value += Math.abs(sumOfPliers(part.getFinalId()));
        }
        return value;
    }

}