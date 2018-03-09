package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.lang.Math;

public class NextPrimeHammer implements Tool {

    /**
     * @param id the product's part's id
     * @return the next prime after id
     */
    private long nextPrime(long id) {
        long isPrimeLong = id + 1;
        while (!isPrime(isPrimeLong))
            isPrimeLong++;
        return isPrimeLong;
    }

    /**
     * @param isPrimeLong
     * @return if isPrimeLong is prime
     */
    private boolean isPrime(long isPrimeLong) {
        if (isPrimeLong < 2)
            return false;
        if (isPrimeLong == 2)
            return true;
        for (long i = 2; i <= Math.sqrt(isPrimeLong); i++) {
            if (isPrimeLong % i == 0)
                return false;
        }
        return true;
    }


    /**
     * @return the tool type
     * @Override
     */
    public String getType() {
        return "np-hammer";
    }

    /**
     * @return a long describing the result of tool use on Product package
     * @Override
     */
    public long useOn(Product p) {
        long value = 0;
        for (Product part : p.getParts()) {
            value += Math.abs(nextPrime(part.getFinalId()));

        }
        return value;
    }

}