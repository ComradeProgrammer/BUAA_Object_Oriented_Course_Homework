package i;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Poly {
    private LinkedList<Item> array = new LinkedList<>();
    private final String reg =
        "\\s*[\\+-]\\s*(((([\\+-]?(\\d+\\s*\\*))|[\\+-])?\\s*x\\s*" +
            "(?:\\^\\s*[\\+-]?\\d+)?\\s*)|(?:\\s*[\\+-]?\\d+\\s*))";
    /*private final String regTerm =
        "(?:\\s*(?:(?:(?:[\\+-]?\\d+\\s*\\*)|(?:[\\+-]))" +
        "?\\s*x\\s*(?:\\^\\s*[\\+-]?\\d+)?\\s*)|(?:\\s*[\\+-]?\\d+\\s*))";*/
    /*private final String regTerm = "(?:\\s*(?:(?:[\\+-]?(?:\\d+\\s*\\*))" +
        "?\\s*x\\s*(?:\\^\\s*[\\+-]?\\d+)?\\s*)|(?:\\s*[\\+-]?\\d+\\s*))";*/
    /*private final String regGlobal =
        "\\s*[\\+-]?" + regTerm + "([\\+-]" + regTerm + ")*\\s*";*/
    private final String regTerm1 = "([\\+-])(([\\+-]?\\d+)\\*)x((\\^" +
        "([\\+-]?\\d+))?)";
    private final String regTerm2 = "([\\+-])([\\+-]?)x((\\^([\\+-]?\\d+))?)";
    private final String regTerm3 = "([\\+-])([\\+-]?\\d+)";

    public void panic() {
        /**this is what we need to do when the format is incorrect
         * */

        System.out.print("WRONG FORMAT!");
        System.exit(0);
    }

    public void check(String s) {
        /**
         * This is a method which is used to check whether the input is legal
         * and PREVENT FROM BEING HACKED BY STACKOVERFLOW EXCEPTION
         */
        //String reg = "\\s*[\\+-]\\s*(?:(([\\+-]?\\d+\\s*\\*|[\\+-])
        // ?\\s*x\\s*(\\^\\s*[\\+-]?\\d+)?\\s*)|([\\+-]?\\d+))\\s*";
        //Pattern pattern = Pattern.compile(reg);
        for (int i = 0; i < s.length(); i++) {
            if (!s.substring(i, i + 1).matches("[0-9x\\+\\-\\*\\t\\^ ]")) {
                panic();
            }
        }
        String tmp = new String(s).trim();
        String tmpNoSpace = tmp.replaceAll("\\s", "");
        if (!(tmpNoSpace.startsWith("+") || tmpNoSpace.startsWith("-"))) {
            tmp = "+" + tmp;
        }
        while (tmp.length() > 0) {
            Matcher m = Pattern.compile(reg).matcher(tmp);
            if (!m.lookingAt()) {
                panic();
            }
            //System.out.println(m.group());
            tmp = tmp.replaceFirst(reg, "");
            //System.out.println(tmp);
        }
    }

    public void debug() {
        /**
         * Overview: just a function to print the array for the convenience
         * of debugging
         * */
        System.out.print("(");
        for (Item i : array) {
            System.out.print(" " + i);
        }
        System.out.print(")\n");

    }

    public void init(Scanner stdin) {
        String originalString = stdin.nextLine();
        check(originalString);
        /*check whether the input is legal*/
        String trimStr = originalString.replaceAll("\\s", "");
        if (!trimStr.startsWith("+") && !trimStr.startsWith("-")) {
            /*if the string doesn't start with any +/-,then the mTermx
            matcher will be unable to recognize that item,so add a + */
            trimStr = "+" + trimStr;
        }
        Matcher mmTerm = Pattern.compile(reg).matcher(trimStr);
        /*we search for a term with a +/- in front of it*/
        while (mmTerm.find()) {
            String subStr = mmTerm.group();
            //System.out.println(subStr);
            Matcher mmTerm1 = Pattern.compile(regTerm1).matcher(subStr);
            Matcher mmTerm2 = Pattern.compile(regTerm2).matcher(subStr);
            Matcher mmTerm3 = Pattern.compile(regTerm3).matcher(subStr);
            BigInteger coeff = new BigInteger("0");
            BigInteger index = new BigInteger("0");
            if (mmTerm1.matches()) {
                coeff = new BigInteger(mmTerm1.group(3));
                if ("-".equals(mmTerm1.group(1))) { /* if this term is
                subtracted,negate the sign of coefficient*/
                    coeff = coeff.negate();
                }
                if ("".equals(mmTerm1.group(4))) {
                    /*term like -3*x ,index is 1*/
                    index = new BigInteger("1");
                }
                else { /*normal situation*/
                    index = new BigInteger(mmTerm1.group(6));
                }
            }
            else if (mmTerm2.matches()) {
                //System.out.println("hit 2");
                coeff = new BigInteger(mmTerm2.group(2) + "1");
                if ("-".equals(mmTerm2.group(1))) {
                    /* if this term is
                    subtracted,negate the sign of coefficient*/
                    coeff = coeff.negate();
                }
                if ("".equals(mmTerm2.group(3))) {
                    index = new BigInteger("1");
                }
                else {
                    index = new BigInteger(mmTerm2.group(5));
                }
            }
            else if (mmTerm3.matches()) {
                //System.out.println("hit 3");
                coeff = new BigInteger(mmTerm3.group(2));
                if ("-".equals(mmTerm3.group(1))) {
                    coeff = coeff.negate();
                }
                index = new BigInteger("0");
            }
            //System.out.println(coeff + "     " + index);//----------for debug
            //Item item = new Item(coeff, index);
            array.add(new Item(coeff, index));
        }
    }

    public void derivate() {
        for (Item i : array) {
            i.derivate();
        }
    }

    public void merge() {
        if (array.isEmpty()) {
            return;
        }
        /*use inner class to sort*/
        array.sort(new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o2.getIndex().compareTo(o1.getIndex());
            }
        });
        //System.out.println("debug inside merge1");
        //debug();
        LinkedList<Item> trashbin = new LinkedList<>();
        for (int i = 0; i < array.size() - 1; i++) {
            Item current = array.get(i);
            Item next = array.get(i + 1);
            if (current.getIndex().equals(next.getIndex())) {
                next.setCoeff(current.getCoeff().add(next.getCoeff()));
                current.setCoeff(new BigInteger("0"));
                current.setIndex(new BigInteger("0"));
            }
            if (current.isZero()) {
                trashbin.add(current);
            }
        }
        if (array.isEmpty()) {
            return;
        }
        //System.out.println("debug inside merge");
        //debug();
        if (array.get(array.size() - 1).isZero()) {
            trashbin.add(array.get(array.size() - 1));
        }
        array.removeAll(trashbin);
    }

    @Override
    public String toString() {
        if (array.isEmpty()) {
            return "0";
        }
        else {
            array.sort(new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    return -(o1.getCoeff().compareTo(o2.getCoeff()));
                }
            });
            StringBuffer buffer = new StringBuffer();
            Item tmp = array.removeFirst();
            buffer.append(tmp.toString());
            for (Item i : array) {
                if (i.notnegative()) {
                    buffer.append("+" + i);
                }
                else {
                    buffer.append(i);
                }
            }
            array.addFirst(tmp);
            return new String(buffer);
        }
    }
}
