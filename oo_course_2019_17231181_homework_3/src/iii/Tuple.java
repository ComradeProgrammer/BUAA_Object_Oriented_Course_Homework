package iii;

public class Tuple<A, B> {
    private A aa = null;
    private B bb = null;

    Tuple(A aa, B bb) {
        this.aa = aa;
        this.bb = bb;
    }

    public A a() {
        return aa;
    }

    public B b() {
        return bb;
    }
}
