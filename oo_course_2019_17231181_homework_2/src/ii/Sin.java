package ii;

import java.math.BigInteger;

/**
 * immutable class presenting simple sine term
 */
public class Sin implements Factor {
    private BigInteger index;
    private FactorClass factorclass;

    Sin(BigInteger index) {
        this.index = index;
        this.factorclass = FactorClass.sin;
    }

    public BigInteger getIndex() {
        return index;
    }

    /*public void setIndex(BigInteger index) {
        this.index = index;
    }*/

    public FactorClass getCls() {
        return factorclass;
    }

    /*public void setCls(FactorClass factorclass) {
        this.factorclass = factorclass;
    }*/

    public Object[] derivate() {
        Object[] tuple = new Object[2];
        /*when the index is zero, return 0
         * actually,i don't think my program will reach here */
        if (index.equals(new BigInteger("0"))) {
            tuple[0] = new BigInteger("0");
            tuple[1] = new Term();
            return tuple;
        }
        else {
            //d(sin(x)^t)/dx=t *sin(x)^(t-1) *cos(x)   (t!=0)
            BigInteger newIndex = index.subtract(new BigInteger("1"));
            //index minus one
            Sin newSinFact = new Sin(newIndex);
            Cos newCosfact = new Cos(new BigInteger("1"));
            Term newTerm = new Term();
            newTerm.addFactor(newSinFact);
            newTerm.addFactor(newCosfact);
            tuple[0] = index;
            tuple[1] = newTerm;
            return tuple;
        }

    }

    public boolean equals(Factor f) {
        if (f.getCls() == factorclass && f.getIndex().equals(index)) {
            return true;
        }
        else {
            return false;
        }
    }

    public String toString() {
        if (index.equals(BigInteger.ZERO)) {
            return "";
        }
        else if (index.equals(BigInteger.ONE)) {
            return "sin(x)";
        }
        else {
            return "sin(x)^" + index.toString();
        }
    }

    public Sin clone() {
        return new Sin(index);
    }

    public Sin clone(BigInteger index) {
        return new Sin(index);
    }
}
