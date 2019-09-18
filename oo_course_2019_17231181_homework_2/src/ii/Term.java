package ii;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class Term implements Iterable<Factor> {
    private ArrayList<Factor> factorList = new ArrayList<>();

    Term() {

    }

    Term(BigInteger p, BigInteger s, BigInteger c) {
        if (!p.equals(BigInteger.ZERO)) {
            factorList.add(new Power(p));
        }
        if (!s.equals(BigInteger.ZERO)) {
            factorList.add(new Sin(s));
        }
        if (!c.equals(BigInteger.ZERO)) {
            factorList.add(new Cos(c));
        }
        merge();
    }

    public void addFactor(Factor f) {
        factorList.add(f);
    }

    @Override
    public Iterator<Factor> iterator() {
        return factorList.iterator();
    }

    /**
     * sort:
     * this method is for homework2 only
     * sort the  factorList for murging in future
     */
    public void sort() {
        factorList.sort(new Comparator<Factor>() {
            private int clsint(Factor f) {
                if (f.getCls() == FactorClass.power) {
                    return -1;
                }
                else if (f.getCls() == FactorClass.sin) {
                    return 0;
                }
                else if (f.getCls() == FactorClass.cos) {
                    return 1;
                }
                else {
                    System.out.println("class error @Term.java sort()");
                    return 0;
                }
            }

            @Override
            public int compare(Factor o1, Factor o2) {
                if (clsint(o1) < clsint(o2)) {
                    return -1;
                }
                else if (clsint(o1) > clsint(o2)) {
                    return 1;
                }
                else {
                    return o1.getIndex().compareTo(o2.getIndex());
                }
            }
        });
    }

    public void merge() {
        sort();
        ArrayList<Factor> bin = new ArrayList<>();
        for (int i = 0; i < factorList.size() - 1; i++) {
            Factor f = factorList.get(i);
            if (f.getIndex().equals(BigInteger.ZERO)) {
                bin.add(f);
                //kick out the "constant 1" factor by adding it to bin
                continue;
            }
            else if (f.getCls() == factorList.get(i + 1).getCls()) {
                //has the same class means mergable
                BigInteger index =
                    factorList.get(i + 1).getIndex().add(f.getIndex());
                factorList.set(i + 1, factorList.get(i + 1).clone(index));
                bin.add(f);
                /*if one item can merge with next item,then we can merge the
                item to next item and remove the current item*/
            }
        }
        if (factorList.isEmpty()) {
            return;
        }
        else if (factorList.get(factorList.size() - 1)
            .getIndex().equals(BigInteger.ZERO)) {
            bin.add(factorList.get(factorList.size() - 1));
        } /*check whether the last item is 1*/
        factorList.removeAll(bin);
    }

    public ArrayList<Object[]> derivate() {
        //the result of derivation for term is a lot of terms and coeffiences
        merge();
        ArrayList<Object[]> tupleTermList = new ArrayList<>();
        if (factorList.isEmpty()) {
            /*if the current factorlist is empty,it means the current term is
             1,whose derivation is 0*/
            Object[] tuple = new Object[2];
            tuple[0] = BigInteger.ZERO;
            tuple[1] = new Term();
            tupleTermList.add(tuple);
            return tupleTermList;
        }
        else if (factorList.size() == 1) {
            Factor f = factorList.get(0);
            Object[] tuple = f.derivate();
            Object obj = (Term) tuple[1];
            ((Term) obj).merge();
            tupleTermList.add(tuple);
            return tupleTermList;
        }
        else {
            for (int i = 0; i < factorList.size(); i++) {
                /*use i to choose the factor who is going to be derivated*/
                Object[] tuple = new Object[2];
                Term derivation = new Term();
                for (int j = 0; j < factorList.size(); j++) {
                    if (i == j) {
                        continue;
                    }
                    else {
                        derivation.addFactor(factorList.get(j).clone());
                        /*copy the other term*/
                    }
                }
                Object[] tmp = factorList.get(i).derivate();//get the
                // derivation of the item no.i
                tuple[0] = tmp[0];
                //give out the coeffience caused by derivation
                //then move all factors in the derivation's result
                Term tmp2 = (Term) (tmp[1]);
                for (Factor ff : tmp2) {
                    derivation.addFactor(ff);
                }
                derivation.merge();
                tuple[1] = derivation;
                tupleTermList.add(tuple);

            }
            return tupleTermList;
        }
    }

    @Override
    public int hashCode() {
        merge();
        BigInteger indexSum = BigInteger.ZERO;
        for (Factor i : factorList) {
            indexSum = indexSum.add(i.getIndex());
        }
        return indexSum.hashCode();
    }

    @Override
    public boolean equals(Object t) {
        Term tp = (Term) t;
        merge();
        tp.merge();
        if (tp.factorList.size() == factorList.size()) {
            for (int i = 0; i < factorList.size(); i++) {
                if (!tp.factorList.get(i).equals(factorList.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean isConst() {
        return factorList.isEmpty();
    }

    @Override
    public String toString() {
        merge();
        if (isConst()) {
            return "";
        }
        boolean first = true;
        StringBuffer sb = new StringBuffer();
        for (Factor f : factorList) {
            if (first) {
                sb.append(f.toString());
            }
            else {
                sb.append("*");
                sb.append(f.toString());
            }
            first = false;
        }
        return new String(sb);
    }
}
