package ii;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Poly p = new Poly();
        ReadConsole r = new ReadConsole(p);
        try {
            r.establish(new Scanner(System.in));
        }
        catch (FormatExeception e) {
            return;
        }
        catch (NoSuchElementException e2) {
            System.out.println("WRONG FORMAT!");
            return;
        }
        Poly d = p.derivate();
        System.out.println(d.fuck());
        //System.out.println(p.fuck());
    }
}
