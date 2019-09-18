package vi;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class OuterFloor {
    private Elevator elevator;
    private LinkedList<Request> queue;
    private HashMap<Integer, LinkedList<Request>> floors;
    private int size;

    OuterFloor(List<Integer> availableFloors) {
        size = 0;
        queue = new LinkedList<Request>();
        floors = new HashMap<>();
        this.elevator = elevator;
        for (int i : availableFloors) {
            LinkedList<Request> tmp = new LinkedList<Request>();
            floors.put(i, tmp);
        }
    }

    public void setElevator(Elevator e) {
        elevator = e;
    }

    public synchronized void add(Request r) {
        LinkedList<Request> tmp = floors.get(r.from());
        queue.add(r);
        tmp.add(r);
        size++;
        synchronized (elevator) {
            elevator.notifyAll();
        }
        //System.out.println("of:gotted"+size);
    }

    public synchronized void remove(Request r) {
        LinkedList<Request> tmp = floors.get(r.from());
        queue.remove(r);
        tmp.remove(r);
        size--;
    }

    public synchronized boolean isEmpty() {
        return size == 0;
    }

    public synchronized LinkedList<Request> alsGet(int floor, int targetFloor) {
        LinkedList<Request> result = new LinkedList<Request>();
        LinkedList<Request> tmp = floors.get(floor);
        //int currentFloor = elevators[elevatorid].getFloor();
        //int targetFloor = elevators[elevatorid].getTargetFloor();
        for (Request r : tmp) {
            if (Kit.sign(r.to() - floor) == Kit.sign(targetFloor - floor)) {
                result.add(r);
            }
        }
        for (Request r : result) {
            remove(r);
        }
        return result;
    }

    public synchronized boolean hasAlsNext(int floor, int targetFloor) {
        LinkedList<Request> tmp = floors.get(floor);
        //int currentFloor = elevators[elevatorid].getFloor();
        //int targetFloor = elevators[elevatorid].getTargetFloor();
        for (Request r : tmp) {
            if (Kit.sign(r.to() - floor) == Kit.sign(targetFloor - floor)) {
                return true;
            }
        }
        return false;
    }

    public synchronized Request get() {
        Request r = queue.get(0);
        //remove(r);
        return r;
    }

}
