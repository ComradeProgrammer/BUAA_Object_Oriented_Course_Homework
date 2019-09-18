package iii;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Power implements HasIndex {
    private BigInteger index = BigInteger.ONE;
    private Basic b0;

    Power(String s) throws FormatException, IndexException {
        String sharpStr = Kit.subSharp(s, Kit.bracketMatch(s));
        Matcher m = Pattern.compile(Kit.powerReg).matcher(sharpStr);
        if (m.matches()) {
            /*if (m.group(2) == null) {
                index = BigInteger.ONE;
            }
            else {
                index = new BigInteger(m.group(2));
                if(index.compareTo(new BigInteger("1000"))>0){
                    throw new IndexException();
                }
            }
            b = new Poly(m.group(1));*/
            index = BigInteger.ONE;
            b0 = new Poly(m.group(1));
        }
    }

    public Power(Basic b, BigInteger index) {
        this.b0 = b;
        this.index = index;
    }

    public Basic derivate() {
        if (index.equals(BigInteger.ZERO)) {
            return new Const(BigInteger.ONE);
        }
        Power powerFact = new Power(b0.clone(), index.subtract(BigInteger.ONE));
        Basic dfdx = b0.derivate();
        Const coeff = new Const(index);
        Term result = new Term();
        result.mult(powerFact);
        result.mult(dfdx);
        result.mult(coeff);
        return result;
    }

    public BigInteger getIndex() {
        return index;
    }

    /* public boolean equals() {
         //todo implements
         return false;
     }*/
    public void setIndex(BigInteger index) {
        this.index = index;
    }

    public String toString() {
        if (index.equals(BigInteger.ZERO)) {
            return "1";
        }
        else if (index.equals(BigInteger.ONE)) {
            return "(" + b0.toString() + ")";
        }
        else {
            return "(" + b0.toString() + ")^" + index.toString();
        }
    }

    public Basic clone() {
        return new Power(b0.clone(), index);
    }

    public Basic simple0() {
        if (index.equals(BigInteger.ZERO)) {
            return new Const(BigInteger.ONE);
        }
        else {
            //return new Power(b0.clone().simple0(),index);
            return b0.clone().simple0();
        }
    }

    public int compareTo(Basic bb) {
        if (Kit.classCompare(this) < Kit.classCompare(bb)) {
            return -1;
        }
        else if (Kit.classCompare(this) > Kit.classCompare(bb)) {
            return 1;
        }
        else {
            return b0.compareTo(((Power) bb).b0);
        }
    }
}
