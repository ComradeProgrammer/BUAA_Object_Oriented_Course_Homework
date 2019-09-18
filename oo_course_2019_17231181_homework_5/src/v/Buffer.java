package v;

import java.util.ArrayList;

public class Buffer {
    private ArrayList<Request>[][] buf;
    private Elevator[] elevators = null;
    private int mode;

    Buffer(int mode, Elevator[] elevators) {
        this.mode = mode;
        buf = new ArrayList[Para.totalFloor][mode];
        //mode means how many columns it will have
        for (int i = 0; i < Para.totalFloor; i++) {
            buf[i][0] = new ArrayList<Request>();
            if (mode == 2) {
                buf[i][1] = new ArrayList<Request>();
            }
        }
        this.elevators = elevators;
    }

    public void add(Request r, int floor, int type) {
        //type 0 means down while 1 means up
        ArrayList<Request> lis = (ArrayList<Request>) buf[floor - 1][type];
        lis.add(r);
    }

    public Request get(int floor, int type) {
        ArrayList<Request> lis = (ArrayList<Request>) buf[floor - 1][type];
        if (lis.size() == 0) {
            return null;
        }
        else {
            return lis.remove(0);
        }
    }

    public int getLength(int floor, int type) {
        ArrayList<Request> lis = (ArrayList<Request>) buf[floor - 1][type];
        return lis.size();
    }

    public boolean isEmpty() {
        for (int i = 0; i < Para.totalFloor; i++) {
            if (!buf[i][0].isEmpty()) {
                return false;
            }
            if (mode == 2 && !buf[i][1].isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
