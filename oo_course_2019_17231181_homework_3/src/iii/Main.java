package iii;

import java.util.Scanner;

public class Main {
    /*public static void main(String[] args) throws Exception {
         //Basic x=new Naive(BigInteger.ONE);
         //Cos c=new Cos(x,BigInteger.ONE);
         //Basic d=c.derivate();
         //System.out.println(x.getClass().getName());
         String s = "sin(x)+sin(x)+sin(x)";
         //Scanner
         Poly p = new Poly(s);
        System.out.println(p);
           Basic pp=p.simple0();
         System.out.println(pp);
         Basic d=p.derivate();
         System.out.println(d);
         Basic dd=d.simple0();
         System.out.println(d.simple0());
     }*/
    public static void main(String[] args) {
        try {
            Scanner stdin = new Scanner(System.in);
            String s = stdin.nextLine();
            Kit.checkIllegalChar(s);
            s = s.replaceAll("\\s+", " ");
            Poly p = new Poly(s);
            //System.out.println(p.simple0());
            Basic d = p.derivate();
            //System.out.println(d);
            Basic dd = d.simple0();
            System.out.println(dd);
        }
        catch (FormatException e) {
            System.out.println("WRONG FORMAT!");
            //System.out.println(e);
        }
        catch (IndexException e) {
            System.out.println("WRONG FORMAT!");
            //System.out.println(e);
        }
        catch (Exception e) {
            System.out.println("WRONG FORMAT!");
        }
    }
}