package ii;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Poly {
    private HashMap<Term, BigInteger> map = new HashMap<>();
    private HashSet<String> statusSet = new HashSet<>();
    private HashSet<String> resultSet = new HashSet<>();

    public void addTerm(Object[] tuple) {
        BigInteger coeff = (BigInteger) tuple[0];
        Term term = (Term) tuple[1];
        if (map.containsKey(term)) {
            BigInteger newcoeff = map.get(term).add(coeff);
            if (newcoeff.equals(BigInteger.ZERO)) {
                map.remove(term);
            }
            else {
                map.put(term, newcoeff);
            }
        }
        else {
            if (!coeff.equals(BigInteger.ZERO)) {
                map.put(term, coeff);
            }
        }
    }

    public Poly derivate() {
        Poly derivation = new Poly();
        for (Term t : map.keySet()) {
            BigInteger coeff = map.get(t);
            ArrayList<Object[]> dlist = t.derivate();
            for (Object[] tuple : dlist) {
                tuple[0] = coeff.multiply((BigInteger) tuple[0]);
                derivation.addTerm(tuple);
            }
        }
        return derivation;
    }

    public String toString() {
        if (map.isEmpty()) {
            return "0";
        }
        ArrayList<Map.Entry<Term, BigInteger>> entrylist =
            new ArrayList<>(map.entrySet());
        entrylist.sort(new Comparator<Map.Entry<Term, BigInteger>>() {
            @Override
            public int compare(
                Map.Entry<Term, BigInteger> o1,
                Map.Entry<Term, BigInteger> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        boolean first = true;
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<Term, BigInteger> entry : entrylist) {
            BigInteger coeff = entry.getValue();
            Term term = entry.getKey();
            if (term.isConst()) {
                if (!first && coeff.compareTo(BigInteger.ZERO) > 0) {
                    sb.append("+" + coeff);
                }
                else {
                    sb.append(coeff);
                }
            }
            else { //if this is not constant
                if (!first && coeff.compareTo(BigInteger.ZERO) > 0) {
                    sb.append("+");
                    if (!coeff.equals(BigInteger.ONE)) {
                        sb.append(coeff);
                        sb.append("*");
                    }
                    sb.append(term);
                }
                else {
                    if (coeff.equals(BigInteger.ONE)) {
                        ;
                    } //under this if
                    // block ,if the coefficient is positive,them it must be
                    // the first term
                    else if (coeff.equals(BigInteger.ONE.negate())) {
                        sb.append("-");
                    }
                    else {
                        sb.append(coeff + "*");
                    }

                    sb.append(term);
                }
            }
            first = false;
        }
        return new String(sb);
    }

    public BigInteger[] abstraction(Map.Entry<Term, BigInteger> entry) {
        BigInteger[] result = {BigInteger.ZERO, BigInteger.ZERO,
            BigInteger.ZERO, BigInteger.ZERO};
        Term t = entry.getKey();
        result[0] = entry.getValue();
        for (Factor f : t) {
            if (f.getCls() == FactorClass.power) {
                result[1] = f.getIndex();
            }
            else if (f.getCls() == FactorClass.sin) {
                result[2] = f.getIndex();
            }
            else if (f.getCls() == FactorClass.cos) {
                result[3] = f.getIndex();
            }
        }
        return result;
    }

    public void subTerm(Map.Entry<Term, BigInteger> e, BigInteger coeff) {
        Term term = e.getKey();
        if (map.containsKey(term)) {
            BigInteger newcoeff = map.get(term).subtract(coeff);
            if (newcoeff.equals(BigInteger.ZERO)) {
                map.remove(term);
            }
            else {
                map.put(term, newcoeff);
            }
        }
        else {
            if (!coeff.equals(BigInteger.ZERO)) {
                map.put(term, coeff.negate());
            }
        }
    }

    public Poly clone() {
        Poly np = new Poly();
        for (Map.Entry<Term, BigInteger> e : map.entrySet()) {
            Object[] o = new Object[2];
            o[0] = e.getValue();
            o[1] = e.getKey();
            np.addTerm(o);
        }
        return np;
    }

    private static int times = 0;

    public void dfs(Poly p) {
        times++;
        if (times >= 1000) { return; }
        BigInteger two = new BigInteger("2");//define 2
        if (statusSet.contains(p.toString())) {
            //exclude the status that has been already considered
            return;
        }
        statusSet.add(p.toString());
        ArrayList<Map.Entry<Term, BigInteger>> entrylist =
            new ArrayList<>(p.map.entrySet());
        for (int i = 0; i < p.map.size(); i++) {
            for (int j = 0; j < p.map.size(); j++) {
                if (i == j) {
                    //exclude the situation that the two term we choose is same
                    continue;
                }
                //parse the term's index
                BigInteger[] b1 = p.abstraction(entrylist.get(i));
                BigInteger[] b2 = p.abstraction(entrylist.get(j));
                if (b1[1].equals(b2[1])
                    && b1[2].subtract(b2[2]).equals(two)
                    && b2[3].subtract(b1[3]).equals(two)) {
                    //sin^2+cos^2=1
                    Poly res = p.clone();
                    res.subTerm(entrylist.get(i), b1[0]);
                    res.subTerm(entrylist.get(j), b1[0]);
                    Term newterm = new Term(b1[1], b1[2].subtract(two),
                        b1[3]);
                    Object[] o = {b1[0], newterm};
                    res.addTerm(o);
                    dfs(res);
                }
                else if (b1[1].equals(b2[1])
                    && b1[3].equals(b2[3])
                    && b1[2].subtract(two).equals(b2[2])) {
                    Term newterm = new Term(b2[1], b2[2],
                        b2[3].add(two));
                    BigInteger tmp = b1[0].negate();
                    Poly res = p.clone();
                    res.subTerm(entrylist.get(i), b1[0]);
                    res.subTerm(entrylist.get(j), b1[0].negate());
                    Object[] o = {tmp, newterm};
                    res.addTerm(o);
                    dfs(res);
                }
                else if (b1[1].equals(b2[1])
                    && b1[2].equals(b2[2])
                    && b1[3].subtract(two).equals(b2[3])) {
                    Term newterm = new Term(b2[1], b2[2].add(two),
                        b2[3]);
                    BigInteger tmp = b1[0].negate();
                    Poly res = p.clone();
                    res.subTerm(entrylist.get(i), b1[0]);
                    res.subTerm(entrylist.get(j), b1[0].negate());
                    Object[] o = {tmp, newterm};
                    res.addTerm(o);
                    dfs(res);
                }
            }
        }
        resultSet.add(p.toString());
    }

    public String fuck() {
        dfs(this);
        String tmp = null;
        for (String tp : resultSet) {
            if (tmp == null || tmp.length() > tp.length()) {
                tmp = tp;
            }
        }
        return tmp;
    }
}
