package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.math.BigInteger;

/**
 * Created by חן on 28-Dec-16.
 */
public class GcdScrewDriver implements Tool {

    private String type = "gs-driver";

    /**
     *
     *@return type - the tool's type name as presented in the JSON file
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * this methods sums all the product's {@link #screwMe(long)}  return value
     * @param p - Product to use tool on
     * @return value - a long describing the result of tool use on Product package
     */
    @Override
    public long useOn(Product p) {
        long value=0;
        for(Product part : p.getParts()){
            value += Math.abs(screwMe(part.getFinalId()));

        }
        return value;
    }

    /**
     * this method calculates the gcd of given product's id and its reverse (using {@link #reverse(long)} method)
     * @param id of the product
     * @return value of main calculation
     */
    private long screwMe(long id){
        BigInteger b1 = BigInteger.valueOf(id);
        BigInteger b2 = BigInteger.valueOf(reverse(id));
        long value= (b1.gcd(b2)).longValue();
        return value;
    }

    /**
     * calculate the reverse of a  given long  number.
      * @param n number to  reverse
     * @return reverse value of n
     */
    private long reverse(long n) {
        long reverse=0;
        while( n != 0 ){
            reverse = reverse * 10;
            reverse = reverse + n%10;
            n = n/10;
        }
        return reverse;
    }
}
