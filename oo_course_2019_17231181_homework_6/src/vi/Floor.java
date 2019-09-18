package vi;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Floor {
    private LinkedList<Request> queue;
    private HashMap<Integer, LinkedList<Request>> floors;
    private int size;

    Floor(List<Integer> availableFloors) {
        size = 0;
        queue = new LinkedList<Request>();
        floors = new HashMap<>();
        for (int i : availableFloors) {
            LinkedList<Request> tmp = new LinkedList<Request>();
            floors.put(i, tmp);
        }
    }

    public void add(Request r) {
        LinkedList<Request> tmp = floors.get(r.to());
        queue.add(r);
        tmp.add(r);

        size++;
        //System.out.println("floor:added"+size);
    }

    public void remove(Request r) {
        LinkedList<Request> tmp = floors.get(r.to());
        queue.remove(r);
        tmp.remove(r);
        size--;
        //System.out.println("floor:removed"+size);
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Request get() {
        Collections.sort(queue);
        Request r = queue.get(0);
        //remove(r);
        return r;
    }

    public LinkedList<Request> getAll(int floor) {
        LinkedList<Request> tmp = floors.get(floor);
        LinkedList<Request> result = new LinkedList<>();
        for (Request r : tmp) {
            result.add(r);
        }
        for (Request r : result) {
            remove(r);
        }
        return result;
    }

    public boolean hasRequest(int floor) {
        LinkedList<Request> tmp = floors.get(floor);
        return !tmp.isEmpty();

    }

    protected LinkedList<Request> getFloorReference(int floor) {
        return floors.get(floor);
    }
}
