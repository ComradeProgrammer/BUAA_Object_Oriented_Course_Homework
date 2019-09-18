package v;

public class SynchronizedBuffer extends Buffer {
    SynchronizedBuffer(int mode, Elevator[] elevators) {
        super(mode, elevators);
    }

    public synchronized void add(Request r, int floor, int type) {
        super.add(r, floor, type);
    }

    public synchronized Request get(int floor, int type) {
        return super.get(floor, type);
    }

    public synchronized int getLength(int floor, int type) {
        return super.getLength(floor, type);
    }
}
