package ii;

import java.math.BigInteger;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadConsole {
    private Poly poly = null;

    ReadConsole(Poly poly) {
        this.poly = poly;
    }

    public static final String fact1 =
        "(?:\\s*x\\s*(?:\\^\\s*([+-]?\\d+))?\\s*)";
    public static final String rawfact1 =
        "\\*x((\\^([+-]?\\d+))?)";
    // power term

    public static final String fact2 =
        "(?:\\s*sin\\s*\\(\\s*x\\s*\\)\\s*(?:\\^\\s*([+-]?\\d+))?\\s*)";
    public static final String rawfact2 =
        "\\*sin\\(x\\)((\\^([+-]?\\d+))?)";
    //sine term

    public static final String fact3 =
        "(?:\\s*cos\\s*\\(\\s*x\\s*\\)\\s*(?:\\^\\s*([+-]?\\d+))?\\s*)";
    public static final String rawfact3 =
        "\\*cos\\(x\\)((\\^([+-]?\\d+))?)";
    // cosine term
    public static final String fact4 =
        "(?:\\s*([+-]?\\d+)\\s*)";
    public static final String rawfact4 =
        "\\*([+-]?\\d+)";
    //constant
    public static final String factreg =
        "(" + fact1 + "|" + fact2 + "|" + fact3 + "|" + fact4 + ")";
    //fact
    // public static final String varfact =
    //"(?:\\s*[+-]?\\s*(?:" + fact1 + "|" + fact2 + "|" + fact3 + "))";
    // public static final String termHeader =
    //"(?:" + varfact + "|" + fact4 + ")";
    //public static final String termreg =
    //"(?:[+-]\\s*" + termHeader + "(\\s*\\*\\s*" + factreg + "\\s*)*\\s*)";
    public static final String termreg =
        "(?:[+-]\\s*[+-]?\\s*" + factreg + "(\\s*\\*\\s*" + factreg
            + "\\s*)*\\s*)";
    public static final String p =
        "(\\s*" + termreg + "\\s*)+";

    public void panic() throws FormatExeception {
        System.out.print("WRONG FORMAT!");
        throw new FormatExeception();
    }

    public String checkvalid(String s) throws FormatExeception {
        for (int i = 0; i < s.length(); i++) {
            if (!s.substring(i, i + 1).matches(
                "[0-9\\+\\-\\*\\t \\^sincox\\(\\)]")) {
                panic();
            }
        }
        String rawstr = s.replaceAll("\\s", "");
        String ss = s;
        if (!rawstr.startsWith("+") && !rawstr.startsWith("-")) {
            rawstr = "+" + rawstr;
            ss = "+" + s;
        }
        Matcher m = Pattern.compile(p).matcher(ss);
        if (!m.matches()) {
            panic();
        }
        return rawstr;
    }

    public Object[] termParse(String s) {
        //returns a term and coefficienct
        BigInteger coeff = BigInteger.ONE;
        Term term = new Term();
        int negate = 0;
        if (s.charAt(0) == '-') { negate = 1; }
        String tmp = s.substring(1);
        if (tmp.charAt(0) == '+' || tmp.charAt(0) == '-') {
            if (tmp.charAt(0) == '-') { negate = 1 - negate; }
            tmp = tmp.substring(1);
        } //up to now all the +- at the beginning of the term is handled
        tmp = "*" + tmp;
        Matcher m = Pattern.compile("\\*" + factreg).matcher(tmp);
        while (m.find()) {
            String factorStr = m.group();
            Matcher m1 = Pattern.compile(rawfact1).matcher(factorStr);
            if (m1.matches()) {
                BigInteger index = BigInteger.ONE;
                if (m1.group(1).equals("")) { index = BigInteger.ONE; }
                //if the indexed was omitted
                else {
                    index = new BigInteger(m1.group(3));
                }
                //else we get the index
                Power factor = new Power(index);
                //construct the factor
                term.addFactor(factor);
                //add it to term
                continue;
            }
            Matcher m2 = Pattern.compile(rawfact2).matcher(factorStr);
            if (m2.matches()) {
                BigInteger index = BigInteger.ONE;
                if (m2.group(1).equals("")) { ; }
                else {
                    index = new BigInteger(m2.group(3));
                }
                Sin factor = new Sin(index);
                term.addFactor(factor);
                continue;
            }
            Matcher m3 = Pattern.compile(rawfact3).matcher(factorStr);
            if (m3.matches()) {
                BigInteger index = BigInteger.ONE;
                if (m3.group(1).equals("")) { ; }
                else {
                    index = new BigInteger(m3.group(3));
                }
                Cos factor = new Cos(index);
                term.addFactor(factor);
                continue;
            }
            Matcher m4 = Pattern.compile(rawfact4).matcher(factorStr);
            if (m4.matches()) {
                BigInteger constant = new BigInteger(m4.group(1));
                coeff = coeff.multiply(constant);
                continue;
            }
        }
        if (negate == 1) { coeff = coeff.negate(); }
        Object[] tuple = new Object[2];
        tuple[0] = coeff;
        tuple[1] = term;
        return tuple;
    }

    public void establish(Scanner stdin) throws FormatExeception {
        String s = stdin.nextLine();
        String rawstr = checkvalid(s);
        Matcher m = Pattern.compile(termreg).matcher(rawstr);
        while (m.find()) {
            Object[] tuple = termParse(m.group());
            poly.addTerm(tuple);
        }
        stdin.close();
    }
}
