package vi;

import java.util.LinkedList;

public class Buffer {
    private LinkedList<Request> buf = new LinkedList<>();

    public synchronized Request get() {
        return buf.removeFirst();
    }

    public synchronized void add(Request r) {
        buf.add(r);
        notifyAll();
    }

    public synchronized boolean isEmpty() {
        return buf.isEmpty();
    }
}
