package ii;

import java.math.BigInteger;

/**
 * immutable class presenting simple power function term
 */
public class Power implements Factor {
    private BigInteger index;
    private FactorClass factorclass;

    Power(BigInteger index) {
        this.index = index;
        this.factorclass = FactorClass.power;
    }

    public BigInteger getIndex() {
        return index;
    }

    /* public void setIndex(BigInteger index) {
         this.index = index;
     }
 */
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
            BigInteger newIndex = index.subtract(BigInteger.ONE);
            Power newPower = new Power(newIndex);
            Term newTerm = new Term();
            newTerm.addFactor(newPower);
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
            return "x";
        }
        else {
            return "x^" + index.toString();
        }
    }

    public Power clone() {
        return new Power(index);
    }

    public Power clone(BigInteger index) {
        return new Power(index);
    }
}
