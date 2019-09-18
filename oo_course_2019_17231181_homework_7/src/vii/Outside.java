package vii;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Outside {
    private HashMap<Integer, LinkedList<Request>> floors = new HashMap<>();
    private int length;
    private Elevator[] els;
    private int id;

    Outside(Elevator[] els, int id) {
        for (int i : Main.allFloors) {
            LinkedList<Request> tmp = new LinkedList();
            floors.put(i, tmp);
        }
        this.els = els;
        this.id = id;
    }

    public boolean isEmpty() {
        return length == 0;
    }

    public synchronized void add(Request r) {
        synchronized (els[id]) {
            LinkedList<Request> tmp = floors.get(r.from());
            tmp.add(r);
            length++;


            els[id].notifyAll();
        }

    }

    public synchronized List<Request> get(int floor, int direction,
                                          int maxSize, int no) {
        LinkedList<Request> tmp = floors.get(floor);
        int i = 0;
        LinkedList<Request> res = new LinkedList<>();
        for (Request r : tmp) {
            if (i < maxSize && r.direction() == direction
                && Main.floors[no].contains(r.to())) {
                res.add(r);
                length--;
                i++;
            }
        }
        tmp.removeAll(res);
        return res;
    }

    private boolean has(int floor, int no) {
        LinkedList<Request> tmp = floors.get(floor);
        for (Request r : tmp) {
            if (Main.floors[no].contains(r.to())) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean shouldLoad(int floor, int direction, int no) {
        LinkedList<Request> tmp = floors.get(floor);
        for (Request r : tmp) {
            if (Main.floors[no].contains(r.to())
                && r.direction() == direction) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean seek(int floor, int direction, int no,
                                     boolean thisFloor) {
        int step = 2 * direction - 1;
        int start = floor;
        if (!thisFloor) {
            start += step;
        }
        for (int i = start; i <= 20 && i >= -3; i += step) {
            if (!Main.floors[no].contains(i)) {
                continue;
            }
            if (has(i, no)) {
                return true;
            }
        }
        return false;
    }

}
