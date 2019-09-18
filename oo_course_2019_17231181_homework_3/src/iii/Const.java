package iii;

import java.math.BigInteger;

public class Const implements Basic {
    private BigInteger coeff;

    Const(String s) throws IndexException, FormatException {
        this.coeff = new BigInteger(s.trim());
    }

    Const(BigInteger coeff) {
        this.coeff = coeff;
    }

    public Basic derivate() {
        return new Const(BigInteger.ZERO);
    }

    /*public boolean equals(Object o) {
        //todo implemments
        return false;
    }*/

    public Basic clone() {
        return new Const(coeff);
    }

    public Basic simple0() {
        return clone();
    }

    public String toString() {
        return coeff.toString();
    }

    public BigInteger value() {
        return coeff;
    }

    public int compareTo(Basic bb) {
        if (Kit.classCompare(this) < Kit.classCompare(bb)) {
            return -1;
        }
        else if (Kit.classCompare(this) > Kit.classCompare(bb)) {
            return 1;
        }
        else {
            return coeff.compareTo(((Const) bb).coeff);
        }
    }
}
