package vi;

import java.util.ArrayList;
import java.util.List;

public class Kit {
    public static final int elevatorNum = 1;
    public static final int doorTime = 200;
    public static final int floorTime = 400;
    public static final List<Integer> availableFloors = new ArrayList<>();

    static {
        for (int i = -3; i <= 16; i++) {
            if (i == 0) {
                continue;
            }
            availableFloors.add(i);
        }
    }

    public static int sign(int a) {
        if (a > 0) {
            return 1;
        }
        else if (a < 0) {
            return -1;
        }
        else {
            return 0;
        }
    }

    public static int nextFloor(int floor, int direction) {
        return availableFloors.get(
            availableFloors.indexOf(floor) + 2 * direction - 1);
    }
}

