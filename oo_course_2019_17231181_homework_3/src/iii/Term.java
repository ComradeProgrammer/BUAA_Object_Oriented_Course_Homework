package iii;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Term implements Basic {
    private ArrayList<Basic> list = new ArrayList<>();

    Term() {

    }

    Term(String str1) throws FormatException, IndexException {
        //System.out.println(str1);//--------for debug only
        String str = str1;
        String sharpStr = Kit.subSharp(str, Kit.bracketMatch(str));
        Matcher m = Pattern.compile(Kit.termReg).matcher(sharpStr);
        if (!m.matches()) {
            throw new FormatException();
        }
        sharpStr = sharpStr.trim();
        str = str.trim();
        if (sharpStr.startsWith("+") || sharpStr.startsWith("-")) {
            if (sharpStr.startsWith("-")) {
                list.add(new Const(BigInteger.ONE.negate()));
            }
            sharpStr = sharpStr.substring(1);
            str = str.substring(1);
        }
        sharpStr = "*" + sharpStr;
        str = "*" + str;
        Matcher m2 =
            Pattern.compile("\\*\\s*(" + Kit.exprReg + ")").matcher(sharpStr);
        while (m2.find()) {
            String gg = m2.group(1);
            String tmp = str.substring(m2.start(1), m2.end(1));
            //System.out.println(tmp);//------for debug only
            Matcher mt1 = Pattern.compile(Kit.constReg).matcher(gg);
            Matcher mt2 = Pattern.compile(Kit.naiveReg).matcher(gg);
            Matcher mt3 = Pattern.compile(Kit.powerReg).matcher(gg);
            Matcher mt4 = Pattern.compile(Kit.sinReg).matcher(gg);
            Matcher mt5 = Pattern.compile(Kit.cosReg).matcher(gg);
            Basic b = null;
            if (mt1.matches()) {
                b = new Const(tmp);
            }
            else if (mt2.matches()) {
                b = new Naive(tmp);
            }
            else if (mt3.matches()) {
                b = new Power(tmp);
            }
            else if (mt4.matches()) {
                b = new Sin(tmp);
            }
            else if (mt5.matches()) {
                b = new Cos(tmp);
            }
            list.add(b);
        }
    }

    public void mult(Basic b) {
        list.add(b);
    }

    public Basic derivate() {
        int n = list.size();
        if (n == 0) {
            return new Const(BigInteger.ONE);
        }
        Poly result = new Poly();
        for (int i = 0; i < n; i++) {
            Term t = new Term();
            t.mult(list.get(i).derivate());
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    continue;
                }
                else {
                    t.mult(list.get(j).clone());
                }
            }
            result.add(t);
        }
        return result;
    }

    /*public boolean equals() {
        // todo implements
        return false;
    }*/

    // todo rewrite toString method
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        for (Basic tmp : list) {
            if (!first) {
                buffer.append("*");
                if (tmp.getClass().getName().equals("iii.Poly")) {
                    buffer.append("(");
                    buffer.append(tmp);
                    buffer.append(")");
                }
                else {
                    buffer.append(tmp);
                }
            }
            else {
                if (tmp.getClass().getName().equals("iii.Poly")) {
                    buffer.append("(");
                    buffer.append(tmp);
                    buffer.append(")");
                }
                else {
                    buffer.append(tmp);
                }
            }
            first = false;
        }
        return new String(buffer);
    }

    public Basic clone() {
        Term copy = new Term();
        for (Basic b : list) {
            copy.mult(b.clone());
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
        // expand term
        ArrayList<Term> bin0 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Basic b = list.get(i);
            if (b.getClass().getName().equals("iii.Term")) {
                bin0.add((Term) b);
            }
            /*else if(b.getClass().getName().equals("iii.Poly")){
                list.set(i,new Power(b,BigInteger.ONE));
            }*/
        }
        for (Term t : bin0) {
            list.addAll(t.list);
        }
        list.removeAll(bin0);
        // remove const;
        ArrayList<Basic> bin = new ArrayList<>();
        BigInteger coeff = BigInteger.ONE;
        for (Basic b : list) {
            if (b.getClass().getName().equals("iii.Const")) {
                bin.add(b);
                coeff = coeff.multiply(((Const) b).value());
            }
        }
        list.removeAll(bin);
        //
        merge();
        if (coeff.equals(BigInteger.ZERO)) {
            return new Const(BigInteger.ZERO);
        }
        else if (list.size() == 0) {
            return new Const(coeff);
        }
        else if (coeff.equals(BigInteger.ONE) && list.size() == 1) {
            return list.get(0);
        }
        else if (!coeff.equals(BigInteger.ONE)) {
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
            Collections.sort(((Term) bb).list);
            ArrayList<Basic> llist = ((Term) bb).list;
            int l1 = list.size();
            int l2 = ((Term) bb).list.size();
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
                list.get(i).compareTo(list.get(i + 1)) == 0) {
                bin.add(list.get(i));
                BigInteger newIndex =
                    ((HasIndex) (list.get(i))).getIndex()
                        .add(((HasIndex) (list.get(i + 1))).getIndex());
                HasIndex fuck =
                    (HasIndex) (((HasIndex) list.get(i + 1)).clone());
                fuck.setIndex(newIndex);
                list.set(i + 1, fuck);
            }
            //list.set(i, list.get(i).simple0());
        }
        list.removeAll(bin);
    }

}
