package iii;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sin implements HasIndex {
    private BigInteger index = BigInteger.ONE;
    private Basic b0;

    Sin(String s) throws FormatException, IndexException {
        String sharpStr = Kit.subSharp(s, Kit.bracketMatch(s));
        Matcher m = Pattern.compile(Kit.sinReg).matcher(sharpStr);
        if (m.matches()) {
            if (m.group(2) == null) {
                index = BigInteger.ONE;
            }
            else {
                index = new BigInteger(m.group(2));
                if (index.abs().compareTo(new BigInteger("10000")) > 0) {
                    throw new IndexException();
                }
            }
            String fuck1 = Kit.subSharp(m.group(1),
                Kit.bracketMatch(m.group(1)));
            if (fuck1.matches(Kit.powerReg)) {
                b0 = new Power(m.group(1));
            }
            else if (fuck1.matches(Kit.naiveReg)) {
                b0 = new Naive(m.group(1));
            }
            else if (fuck1.matches(Kit.constReg)) {
                b0 = new Const(m.group(1));
            }
            else if (fuck1.matches(Kit.sinReg)) {
                b0 = new Sin(m.group(1));
            }
            else if (fuck1.matches(Kit.cosReg)) {
                b0 = new Cos(m.group(1));
            }
            else {
                throw new FormatException();
            }
        }
    }

    Sin(Basic b, BigInteger index) {
        this.b0 = b;
        this.index = index;
    }

    public Basic derivate() {
        if (index.equals(BigInteger.ZERO)) {
            return new Const(BigInteger.ONE);
        }
        Sin sinFact = new Sin(b0.clone(), index.subtract(BigInteger.ONE));
        Cos cosFact = new Cos(b0.clone(), BigInteger.ONE);
        Basic dfdx = b0.derivate();
        Const coeff = new Const(index);
        Term result = new Term();
        result.mult(cosFact);
        result.mult(sinFact);
        result.mult(dfdx);
        result.mult(coeff);
        return result;
    }

    public BigInteger getIndex() {
        return index;

    }

    public void setIndex(BigInteger index) {
        this.index = index;
    }

    /*public boolean equals() {
        //todo implements
        return false;
    }*/

    public Basic clone() {
        return new Sin(b0.clone(), index);
    }

    public String toString() {
        String tmp = b0.toString();
        if (b0.getClass().getName().equals("iii.Poly")
            || b0.getClass().getName().equals("iii.Term")) {
            tmp = "(" + tmp + ")";
        }
        if (index.equals(BigInteger.ZERO)) {
            return "1";
        }
        else if (index.equals(BigInteger.ONE)) {
            return "sin(" + tmp + ")";
        }
        else {
            return "sin(" + tmp + ")^" + index.toString();
        }
    }

    public Basic simple0() {
        if (index.equals(BigInteger.ZERO)) {
            return new Const(BigInteger.ONE);
        }
        else {
            return new Sin(b0.clone().simple0(), index);
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
            return b0.compareTo(((Sin) bb).b0);
        }
    }
}
