package iii;

import java.util.ArrayList;
import java.util.LinkedList;

public class Kit {
    public static final String constReg = "(?:\\s*([+-]?\\d+)\\s*)";
    public static final String naiveReg = "(?:\\s*x\\s*(?:\\^\\s*([+-]?\\d+)" +
        ")?\\s*)";
    // group 1 is the index

    /*public static final String powerReg = "(?:\\s*#([^#]+)#\\s*(?:\\^\\s*" +
        "([+-]?\\d+))?\\s*)";*/
    public static final String powerReg = "(?:\\s*#([^#]+)#\\s*)";
    //group 1 is the string in the bracket ; group 2 is the index
    //fuck,there is no index now
    //this regex can also match just brackets

    public static final String sinReg = "(?:\\s*sin\\s*#([^#]+)#\\s*" +
        "(?:\\^\\s*" +
        "([+-]?\\d+))?\\s*)";
    // group 1 is the string in the bracket ; group 2 is the index

    public static final String cosReg = "(?:\\s*cos\\s*#([^#]+)#\\s*" +
        "(?:\\^\\s*" +
        "([+-]?\\d+))?\\s*)";
    // group 1 is the string in the bracket ; group 2 is the index

    public static final String exprReg =
        "(?:" + constReg + "|" + sinReg + "|" + cosReg + "|" + powerReg + "|"
            + naiveReg + ")";

    public static final String termReg =
        "(?:\\s*[+-]?\\s*" + exprReg + "(?:\\s*\\*\\s*" + exprReg + "\\s*)*)";
    public static final String polyReg =
        "(?:\\s*[+-]?\\s*" + termReg + "(?:\\s*[+-]\\s*" + termReg + "\\s*)*)";

    public static void checkIllegalChar(String s) throws FormatException {
        for (int i = 0; i < s.length(); i++) {
            if (!s.substring(i, i + 1).matches("[0-9sincox*^() \\t+-]")) {
                // todo implements legal character list
                throw new FormatException();
            }
        }
    }

    public static ArrayList<Tuple<Integer, Integer>> bracketMatch(String s)
        throws FormatException {
        ArrayList<Tuple<Integer, Integer>> res = new ArrayList<>();
        LinkedList<Integer> stack = new LinkedList<>();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.addLast(i);
            }
            else if (s.charAt(i) == ')') {
                if (stack.isEmpty()) {
                    throw new FormatException("bracket not match");
                }
                int tmp = stack.removeLast();
                if (stack.isEmpty()) {
                    res.add(new Tuple<Integer, Integer>(tmp, i));
                }
            }
        }
        if (!stack.isEmpty()) {
            throw new FormatException("bracket not match");
        }
        return res;
    }

    public static String subSharp(String s,
                                  ArrayList<Tuple<Integer, Integer>> list) {
        char[] tmp = s.toCharArray();
        for (Tuple<Integer, Integer> tuple : list) {
            tmp[tuple.a()] = '#';
            tmp[tuple.b()] = '#';
        }
        return new String(tmp);
    }

    public static int classCompare(Basic b1) {
        String cn = b1.getClass().getName();
        if (cn.equals("iii.Const")) {
            return 0;
        }
        else if (cn.equals("iii.Naive")) {
            return 1;
        }
        else if (cn.equals("iii.Power")) {
            return 2;
        }
        else if (cn.equals("iii.Sin")) {
            return 3;
        }
        else if (cn.equals("iii.Cos")) {
            return 4;
        }
        else if (cn.equals("iii.Term")) {
            return 5;
        }
        else if (cn.equals("iii.Poly")) {
            return 6;
        }
        return -1;
    }

}
