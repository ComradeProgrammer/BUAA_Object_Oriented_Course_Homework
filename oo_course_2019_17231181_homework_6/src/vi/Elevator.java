package vi;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.oocourse.TimableOutput;

public class Elevator implements Runnable {
    private static int count = 0;
    private int floor;
    private int targetFloor;
    private int direction;//0 means down ,1 means up, 2 means still
    private boolean doorOpened;
    private boolean stop;
    private OuterFloor outerFloor;
    private Floor lift;
    private int id = count++;
    private long time;
    private int stage = 0;
    private Request mainRequest = null;

    Elevator(OuterFloor outerFloor, List<Integer> availableFloors) {
        this.floor = 1;
        this.direction = 2;
        this.targetFloor = 1;
        this.doorOpened = false;
        this.stop = false;
        this.outerFloor = outerFloor;
        this.lift = new Floor(availableFloors);
    }

    /* public synchronized int getFloor() {
         return this.floor;
     }

     public synchronized int getTargetFloor() {
         return this.targetFloor;
     }
 */
    public synchronized void signalStop() {
        this.stop = true;
        this.notifyAll();
    }

    public void open() {
        time = TimableOutput.println("OPEN-" + floor);
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Kit.doorTime * 0.95));
        }
        catch (InterruptedException e) {
            return;
        }
        doorOpened = true;
    }

    public void recieve(Request r) {
        TimableOutput.println("IN-" + r.id() + "-" + floor);
    }

    public void release(Request r) {
        TimableOutput.println("OUT-" + r.id() + "-" + floor);
    }

    public void letout() {
        LinkedList<Request> l = lift.getAll(floor);
        for (Request r : l) {
            release(r);
        }
    }

    public void letin() {
        LinkedList<Request> l = outerFloor.alsGet(floor, targetFloor);
        for (Request r : l) {
            recieve(r);
            lift.add(r);
        }
    }

    public void still() {
        direction = 2;
        synchronized (this) {
            if (lift.isEmpty() && outerFloor.isEmpty() && !stop) {
                //System.out.println("yes");
                try {
                    this.wait();
                }
                catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    public void switchAndLoad() {
        Request r = null;
        if (!lift.isEmpty()) {
            r = lift.get();
            stage = 1;
        }
        else {
            r = outerFloor.get();
            stage = 0;
        }
        targetFloor = r.to();
        mainRequest = r;

        letin();
    }

    public void close() {
        long duration = time + 2 * Kit.doorTime - new Date().getTime();
        if (duration > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(duration);
            }
            catch (InterruptedException e) {
                return;
            }
        }
        time = TimableOutput.println("CLOSE-" + floor);
    }

    public void move() {

        if (stage == 0) {
            if (floor == mainRequest.from()) {
                stage = 1;
                if (targetFloor > floor) {
                    direction = 1;
                }
                else {
                    direction = 0;
                }
            }
            else {
                if (mainRequest.from() > floor) {
                    direction = 1;
                }
                else {
                    direction = 0;
                }
            }
        }
        else {
            if (targetFloor > floor) {
                direction = 1;
            }
            else {
                direction = 0;
            }
        }
        int newfloor = Kit.nextFloor(floor, direction);
        long duration = time + Kit.floorTime - new Date().getTime();
        try {
            TimeUnit.MILLISECONDS.sleep(duration);
        }
        catch (InterruptedException e) {
            return;
        }
        floor = newfloor;
        time = TimableOutput.println("ARRIVE-" + floor);

    }

    public void run() {
        time = new Date().getTime();
        TimableOutput.initStartTimestamp();
        while (!(lift.isEmpty() && outerFloor.isEmpty() && stop)) {
            if (direction != 2) {
                //if the elevator is not still
                if (outerFloor.hasAlsNext(floor, targetFloor)
                    || lift.hasRequest(floor)) {
                    //if the elevator should stop
                    open();
                    //let people out
                    letout();
                    //check whether we need to change the main request
                    if (floor == targetFloor) {
                        //check whether we have new request
                        if (lift.isEmpty() && outerFloor.isEmpty()) {
                            //we need to be still
                            still();
                            continue;
                        }
                        else {
                            //we need to change main request
                            switchAndLoad();
                            close();
                            move();
                            continue;
                        }
                    }
                    else {
                        //we don't need to change the request
                        letin();
                        close();
                        move();
                        continue;
                    }
                }
                else {
                    //if we don't need to stop
                    move();
                    continue;
                }
            }
            else {
                //if the elevator is still(now it's waked)
                if (outerFloor.isEmpty()) {
                    //still need to wait
                    still();
                    continue;
                }
                else {
                    if (!doorOpened) {
                        open();
                    }
                    switchAndLoad();
                    close();
                    move();
                    continue;
                }

            }
        }
        if (doorOpened) {
            close();
        }
    }

    /*public static void main(String[] args) throws InterruptedException{
        OuterFloor o = new OuterFloor(Kit.availableFloors);
        Elevator e = new Elevator(o, Kit.availableFloors);
        o.setElevator(e);
        Request r;
        r = new Request(0, 2, 3);
        o.add(r);
        r = new Request(1, 3, -3);
        o.add(r);
        new Thread(e).start();
        TimeUnit.MILLISECONDS.sleep(6000);
        e.signalStop();



    }*/
}
