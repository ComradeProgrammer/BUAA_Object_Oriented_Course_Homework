package iii;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Poly implements Basic {
    private ArrayList<Basic> list = new ArrayList<>();

    Poly() {

    }

    Poly(String str1) throws FormatException, IndexException {
        //System.out.println(str1);//----------for debug only
        String str = str1;
        String sharpStr = Kit.subSharp(str, Kit.bracketMatch(str));
        Matcher m = Pattern.compile(Kit.polyReg).matcher(sharpStr);
        if (!m.matches()) {
            throw new FormatException();
        }
        String trimmedStr = str.trim();
        if (!trimmedStr.startsWith("+") && !trimmedStr.startsWith("-")) {
            sharpStr = "+" + sharpStr;
            str = "+" + str;
        }
        String termMatch = "([+-])\\s*(" + Kit.termReg + ")";
        Matcher m2 = Pattern.compile(termMatch).matcher(sharpStr);
        while (m2.find()) {
            Term t = new Term(str.substring(m2.start(2), m2.end(2)));
            if (m2.group(1).equals("-")) {
                t.mult(new Const(BigInteger.ONE.negate()));
            }
            list.add(t);
        }
    }

    public Basic derivate() {
        if (list.size() == 0) {
            return new Const(BigInteger.ZERO);
        }
        Poly result = new Poly();
        for (Basic b : list) {
            result.add(b.derivate());
        }
        return result;

    }

    // todo rewrite toString method
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        for (Basic tmp : list) {
            if (!first) {
                buffer.append("+");
                buffer.append(tmp.toString());
            }
            else {
                buffer.append(tmp);
            }
            first = false;
        }
        return new String(buffer);
    }

    public void add(Basic b) {
        list.add(b);
    }

    /*public boolean equals(Object o) {
        // todo implements
        return false;
    }*/

    public Basic clone() {
        Poly copy = new Poly();
        for (Basic b : list) {
            copy.add(b.clone());
        }
        return copy;
    }

    public Basic simple0() {
        // recursive execute simple0()
        for (int i = 0; i < list.size(); i++) {
            Basic tmp = list.get(i).clone();
            tmp = tmp.simple0();
            list.set(i, tmp);
        }
        // expand poly
        ArrayList<Poly> bin0 = new ArrayList<>();
        for (Basic b : list) {
            if (b.getClass().getName().equals("iii.Poly")) {
                bin0.add((Poly) b);
            }
        }
        for (Poly t : bin0) {
            list.addAll(t.list);
        }
        list.removeAll(bin0);
        // remove const;
        ArrayList<Basic> bin = new ArrayList<>();
        BigInteger coeff = BigInteger.ZERO;
        for (Basic b : list) {
            if (b.getClass().getName().equals("iii.Const")) {
                bin.add(b);
                coeff = coeff.add(((Const) b).value());
            }
        }
        list.removeAll(bin);
        //
        merge();
        if (list.size() == 0) {
            return new Const(coeff);
        }
        else if (coeff.equals(BigInteger.ZERO) && list.size() == 1) {
            return list.get(0);
        }
        else if (!coeff.equals(BigInteger.ZERO)) {
            list.add(0, new Const(coeff));
        }
        return this;
    }

    public int compareTo(Basic bb) {
        if (Kit.classCompare(this) < Kit.classCompare(bb)) {
            return -1;
        }
        else if (Kit.classCompare(this) > Kit.classCompare(bb)) {
            return 1;
        }
        else {
            Collections.sort(list);
            Collections.sort(((Poly) bb).list);
            ArrayList<Basic> llist = ((Poly) bb).list;
            int l1 = list.size();
            int l2 = ((Poly) bb).list.size();
            int minl = Math.min(l1, l2);
            for (int i = 0; i < minl; i++) {
                if (list.get(i).compareTo(llist.get(i)) != 0) {
                    return list.get(i).compareTo(llist.get(i));
                }
            }
            return new Integer(l1).compareTo(new Integer(l2));
        }
    }

    public void merge() {
        ArrayList<Basic> bin = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).getClass().getInterfaces()[0].getName().equals(
                "iii.HasIndex") &&
                list.get(i + 1).getClass().getInterfaces()[0].getName().equals(
                    "iii.HasIndex") &&
                list.get(i).compareTo(list.get(i + 1)) == 0 &&
                ((HasIndex) (list.get(i))).getIndex()
                    .equals(((HasIndex) (list.get(i))).getIndex())) {
                BigInteger coeff = BigInteger.ONE;
                while (i < list.size() - 1 &&
                    list.get(i).getClass().getInterfaces()[0].getName().equals(
                        "iii.HasIndex") &&
                    list.get(i + 1).getClass().getInterfaces()[0]
                        .getName().equals("iii.HasIndex") &&
                    list.get(i).compareTo(list.get(i + 1)) == 0 &&
                    ((HasIndex) (list.get(i))).getIndex()
                        .equals(((HasIndex) (list.get(i))).getIndex())) {
                    coeff = coeff.add(BigInteger.ONE);
                    bin.add(list.get(i));
                    i++;
                }
                Term tmp = new Term();
                tmp.mult(new Const(coeff));
                tmp.mult(list.get(i).clone());
                list.set(i, tmp.simple0());
            }
        }
        list.removeAll(bin);
    }

}
