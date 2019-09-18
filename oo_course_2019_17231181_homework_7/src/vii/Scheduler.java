package vii;

import java.util.HashMap;
import java.util.LinkedList;

public class Scheduler implements Runnable {
    private LinkedList<Request> queue = new LinkedList<Request>();
    private HashMap<Integer, LinkedList<Request>> buffer = new HashMap<>();
    private boolean stopped = false;
    private Elevator[] els;
    private Outside[] outs;

    Scheduler(Elevator[] els, Outside[] outs) {
        this.outs = outs;
        this.els = els;
    }

    public synchronized void stop() {
        this.stopped = true;
        notifyAll();
    }

    public synchronized void add(Request r) {
        LinkedList<Request> tmp = r.selfGen();
        if (tmp == null) {
            queue.add(r);
        }
        else {
            queue.add(tmp.removeFirst());
            buffer.put(r.id(), tmp);
        }
        notifyAll();
    }

    public synchronized void respond(Request r) {
        LinkedList<Request> tmp = buffer.get(r.id());
        queue.add(tmp.removeFirst());
        if (tmp.isEmpty()) {
            buffer.remove(r.id());
        }
        notifyAll();
    }

    public void run() {
        Request r;
        while (true) {
            synchronized (this) {
                if (queue.isEmpty() && buffer.isEmpty() && stopped) {
                    for (int i = 0; i < els.length; i++) {
                        els[i].stop();
                    }
                    break;
                }
            }
            synchronized (this) {
                if (queue.isEmpty()) {
                    try {
                        wait();
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                }
                if (queue.isEmpty() && buffer.isEmpty() && stopped) {
                    continue;
                }
                if (!queue.isEmpty()) {
                    r = queue.removeFirst();
                }
                else {
                    continue;
                }

            }
            sched(r);
        }
    }

    private void sched(Request r) {
        if (Main.aFloors.contains(r.to()) && Main.aFloors.contains(r.from())) {
            outs[0].add(r);
        }
        else if (Main.cFloors.contains(r.to())
            && Main.cFloors.contains(r.from())) {
            outs[2].add(r);
        }
        else {
            outs[1].add(r);
        }
    }
}
