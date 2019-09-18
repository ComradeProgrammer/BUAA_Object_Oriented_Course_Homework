package vi;

public class Scheduler implements Runnable {
    private OuterFloor[] floors;
    private Buffer buf;
    private boolean stop;

    Scheduler(OuterFloor[] floors, Buffer buf) {
        this.buf = buf;
        this.floors = floors;
        stop = false;
    }

    public synchronized void signalStop() {

        this.stop = true;
        this.notifyAll();

    }

    public synchronized boolean isStop() {
        return stop;
    }

    public void sched(Request r) {
        //System.out.println("send");
        floors[0].add(r);
        //System.out.println("sended");
    }

    public void run() {
        Request r = null;
        while (!(buf.isEmpty() && isStop())) {
            synchronized (this) {
                while (buf.isEmpty() && !isStop()) {
                    try {
                        //System.out.println("sch:wait");
                        this.wait();
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                    if (buf.isEmpty() && isStop()) {
                        return;
                    }
                }
                //System.out.println("sch:got");
                r = buf.get();
                //System.out.println("sch:got");
            }
            sched(r);
        }
    }
}
