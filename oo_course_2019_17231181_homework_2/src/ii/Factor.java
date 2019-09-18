package ii;

import java.math.BigInteger;

/**
 * immutable interface
 */
public interface Factor {

    public BigInteger getIndex();

    //public void setIndex(BigInteger index);

    public FactorClass getCls();

    //public void setCls(FactorClass factorclass);

    public Object[] derivate();

    public boolean equals(Factor f);

    public Factor clone();

    public Factor clone(BigInteger index);
}