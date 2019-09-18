package xi;


public class UnpleasantGraph extends DetailedGraph {
    UnpleasantGraph() {
        super(Integer.MIN_VALUE, 32, 0);
    }

    public int f(int x) {
        return (x % 5 + 5) % 5;
    }

    @Override
    public int u(int from, int to) {
        int tmp = Math.max(f(from), f(to));
        return (int) (Math.pow(4, tmp));

    }

    /*public static void main(String[] args) {
        MyPath p1=new MyPath(1,2,3);
        MyPath p2=new MyPath(4,3,5);
        UnpleasantGraph u=new UnpleasantGraph();
        u.addPath(p1,1);
        u.addPath(p2,2);
        System.out.println(u.getCheapestPrice(2,5));
    }*/
}
