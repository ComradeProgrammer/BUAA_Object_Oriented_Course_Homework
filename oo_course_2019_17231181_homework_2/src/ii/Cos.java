package ii;

import java.math.BigInteger;

public class Cos implements Factor {
    private BigInteger index;
    private FactorClass factorclass;

    Cos(BigInteger index) {
        this.index = index;
        this.factorclass = FactorClass.cos;
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
        if (index.equals(new BigInteger("0"))) {
            tuple[0] = new BigInteger("0");
            tuple[1] = new Term();
            return tuple;
        }
        else {
            //d(cos(x)^t)/dx= -t *cos(x)^(t-1) *sin(x)   (t!=0)
            BigInteger newIndex = index.subtract(new BigInteger("1"));
            Cos newCosFact = new Cos(newIndex);
            Sin newSinFact = new Sin(new BigInteger("1"));
            Term newTerm = new Term();
            newTerm.addFactor(newSinFact);
            newTerm.addFactor(newCosFact);
            tuple[0] = index.negate();
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
            return "cos(x)";
        }
        else {
            return "cos(x)^" + index.toString();
        }
    }

    public Cos clone() {
        return new Cos(index);
    }

    public Cos clone(BigInteger index) {
        return new Cos(index);
    }
}
