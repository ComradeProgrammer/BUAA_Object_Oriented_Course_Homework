package iii;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Naive implements HasIndex {
    private BigInteger index;

    Naive(BigInteger index) {
        this.index = index;
    }

    Naive(String s) throws IndexException, FormatException {
        Matcher m = Pattern.compile(Kit.naiveReg).matcher(s);
        if (m.matches()) {
            if (m.group(1) == null) {
                index = BigInteger.ONE;
            }
            else {
                index = new BigInteger(m.group(1).trim());
                if (index.abs().compareTo(new BigInteger("10000")) > 0) {
                    throw new IndexException();
                }
            }
        }

    }

    public Basic derivate() {
        if (index.equals(BigInteger.ZERO)) {
            return new Const(BigInteger.ZERO);
        }
        Term result = new Term();
        Const coeff = new Const(index);
        Naive naiveFact = new Naive(index.subtract(BigInteger.ONE));
        result.mult(coeff);
        result.mult(naiveFact);
        return result;
    }

    /*public boolean equals(Object o) {
        //todo implements
        return false;
    }*/

    public BigInteger getIndex() {
        return index;
    }

    public void setIndex(BigInteger index) {
        this.index = index;
    }

    public Basic clone() {
        return new Naive(index);
    }

    public Basic simple0() {
        if (index.equals(BigInteger.ZERO)) {
            return new Const(BigInteger.ONE);
        }
        else {
            return clone();
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if (index.equals(BigInteger.ZERO)) {
            return "1";
        }
        buffer.append("x");
        if (!index.equals(BigInteger.ONE)) {
            buffer.append("^");
            buffer.append(index.toString());
        }
        return new String(buffer);
    }

    public int compareTo(Basic bb) {
        if (Kit.classCompare(this) < Kit.classCompare(bb)) {
            return -1;
        }
        else if (Kit.classCompare(this) > Kit.classCompare(bb)) {
            return 1;
        }
        else {
            return index.compareTo(((Naive) bb).index);
        }
    }

}
