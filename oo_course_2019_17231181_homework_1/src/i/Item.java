package i;

import java.math.BigInteger;

public class Item {
    private BigInteger coeff;
    private BigInteger index;

    /**
     * this is the constructor of the class Item
     */
    Item(BigInteger coeff, BigInteger index) {
        this.coeff = coeff;
        this.index = index;
    }

    public BigInteger getCoeff() {
        return coeff;
    }

    public BigInteger getIndex() {
        return index;
    }

    public void setCoeff(BigInteger coeff) {
        this.coeff = coeff;
    }

    public void setIndex(BigInteger index) {
        this.index = index;
    }

    /**
     * get the derivation of each item
     */
    public void derivate() {
        if (index.equals(new BigInteger("0"))) { /*if this item is a constant*/
            coeff = new BigInteger("0");
        }
        else {
            coeff = coeff.multiply(index);
            index = index.subtract(new BigInteger("1"));
        }
    }

    public boolean isZero() { /*judge whether this item don't needs to be
    show in the result*/
        if (coeff.equals(new BigInteger("0"))) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean notnegative() { /*judge whether a item needs a '+' in
    front of itself when converted to string*/
        return coeff.compareTo(new BigInteger("0")) >= 0;
    }

    @Override
    public String toString() {
        String coeffPart = "";
        String indexPart = "";
        if (coeff.equals(new BigInteger("0"))) { /*if this item is zero, then
         we just do nothing and return, function isZero wll handle it*/
            return "";
        }
        else if (coeff.equals(new BigInteger("1"))) { /* if the coefficient
        is 1,we just output a x for first part*/
            coeffPart = "x";
        }
        else if (coeff.equals(new BigInteger("-1"))) { /* if the coefficient
        is -1,we just output a -x for first part*/
            coeffPart = "-x";
        }
        else { /*else,we need to construct the first part normally*/
            coeffPart = coeff.toString() + "*x";
        }
        if (index.equals(new BigInteger("0"))) { /*if the index is 0,then
        actually we don't need x, unfortunately the second part
         *don't control the existence of x,so we just specially give out the
         * answer and return immediately */
            return coeff.toString();
        }
        else if (index.equals(new BigInteger("1"))) { /* if the index is 1,
        the we just don't output index */
            indexPart = "";
        }
        else {    /*else,normal behavior*/
            indexPart = "^" + index.toString();
        }
        return coeffPart + indexPart;
    }

}

