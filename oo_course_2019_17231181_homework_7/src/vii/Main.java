package vii;

import com.oocourse.TimableOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static final List<Integer> allFloors =
        new ArrayList<Integer>(Arrays.asList(-3, -2, -1, 1, 2, 3, 4, 5, 6, 7,
            8, 9, 10, 11
            , 12, 13, 14, 15, 16, 17, 18, 19, 20));
    public static final List<Integer> aFloors =
        new ArrayList<Integer>(Arrays.asList(-3, -2, -1, 1, 15, 16, 17, 18,
            19, 20));
    public static final List<Integer> bFloors =
        new ArrayList<Integer>(Arrays.asList(-2, -1, 1, 2, 4, 5, 6, 7, 8, 9,
            10, 11, 12,
            13, 14, 15));
    public static final List<Integer> cFloors =
        new ArrayList<Integer>(Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15));
    public static final List<Integer>[] floors;

    static {
        floors = new List[3];
        floors[0] = aFloors;
        floors[1] = bFloors;
        floors[2] = cFloors;
    }

    public static void main(String[] args) throws Exception {
        TimableOutput.initStartTimestamp();
        Elevator[] els = new Elevator[3];
        Outside [] o = new Outside[3];
        Outside o1 = new Outside(els, 0);
        Outside o2 = new Outside(els, 1);
        Outside o3 = new Outside(els, 2);
        o[0] = o1;
        o[1] = o2;
        o[2] = o3;
        Scheduler sch = new Scheduler(els, o);
        Elevator e0 = new Elevator(Main.aFloors, o[0], 400, 200, 1, 2, 0, sch);
        els[0] = e0;
        Elevator e1 = new Elevator(Main.bFloors, o[1], 500, 200, 1, 2, 1, sch);
        els[1] = e1;
        Elevator e2 = new Elevator(Main.cFloors, o[2], 600, 200, 1, 2, 2, sch);
        els[2] = e2;
        Input in = new Input(sch);

        new Thread(e0).start();
        new Thread(e1).start();
        new Thread(e2).start();
        new Thread(sch).start();
        new Thread(in).start();

    }

}
