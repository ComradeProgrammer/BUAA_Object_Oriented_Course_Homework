package i;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Poly poly = new Poly();
        poly.init(new Scanner(System.in));
        poly.derivate();
        poly.merge();
        //poly.debug();
        System.out.println(poly);
    }

}
