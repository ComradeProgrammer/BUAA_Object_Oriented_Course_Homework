package iii;

import java.math.BigInteger;

public interface HasIndex extends Basic {
    public Basic derivate();

    //public boolean equals(Object o);
    public BigInteger getIndex();

    public void setIndex(BigInteger index);

    public Basic clone();
}
