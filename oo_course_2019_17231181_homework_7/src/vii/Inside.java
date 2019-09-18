package vii;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Inside {
    private HashMap<Integer, LinkedList<Request>> floors = new HashMap<>();
    private int length = 0;
    private int top;
    private int bottom;

    Inside(List<Integer> availableFloors) {
        for (int i : availableFloors) {
            LinkedList<Request> tmp = new LinkedList<Request>();
            floors.put(i, tmp);
        }
        top = availableFloors.get(availableFloors.size() - 1);
        bottom = availableFloors.get(0);
    }

    public void add(Request r) {
        LinkedList<Request> tmp = floors.get(r.to());
        tmp.add(r);
        length++;
    }

    public LinkedList<Request> get(int floor) {
        LinkedList<Request> tmp = floors.get(floor);
        LinkedList<Request> res = (LinkedList<Request>) tmp.clone();
        tmp.clear();
        length -= res.size();
        return res;
    }

    public boolean seek(int floor, int direction, boolean containCurrent) {
        int step = 2 * direction - 1;
        int start = floor;
        if (!containCurrent) {
            start += step;
        }
        for (int i = start; i <= top && i >= bottom; i += step) {
            if (!floors.containsKey(i)) {
                continue;
            }
            else {
                if (!floors.get(i).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean shouldUnload(int floor) {
        LinkedList<Request> tmp = floors.get(floor);
        return !tmp.isEmpty();
    }

    public boolean isEmpty() {
        return length == 0;
    }

}
