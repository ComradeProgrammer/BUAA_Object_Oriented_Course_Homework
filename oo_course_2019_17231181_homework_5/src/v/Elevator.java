package v;

import com.oocourse.TimableOutput;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Elevator implements Runnable {
    private int direction = 2;//0 means down, 1 means up,2 means still
    private int floor = 1;
    private Buffer lift = new Buffer(1, null);
    private Buffer outerFloor = null;
    private boolean doorOpened = false;
    private boolean signaled = false;
    private long time;

    Elevator(Buffer floorCrew) {
        this.outerFloor = floorCrew;
    }

    public synchronized void signalStop() {
        this.signaled = true;
    }

    public synchronized int getDirection() {
        return direction;
    }

    public synchronized int floor() {
        return floor;
    }

    public void open() {
        TimableOutput.println("OPEN-" + floor);
        time = new Date().getTime();
        doorOpened = true;
    }

    public void close(long destination) {
        Date date = new Date();
        long currenttime = date.getTime();
        int duration = (int) (destination - currenttime);
        int d1 = duration - (int) (duration * 0.1);
        try {
            TimeUnit.MILLISECONDS.sleep(d1);
        }
        catch (InterruptedException e) {
            return;
        }
        while (true) {
            if (new Date().getTime() >= destination) {
                TimableOutput.println("CLOSE-" + floor);
                time = new Date().getTime();
                break;
            }
        }
        doorOpened = false;
    }

    public void recieve(Request r) {
        TimableOutput.println("IN-" + r.id() + "-" + floor);
    }

    public void release(Request r) {
        TimableOutput.println("OUT-" + r.id() + "-" + floor);
    }

    public boolean checkOuterRequest(int direction,
                                     int type, boolean haveCurrentFloor) {
        int step = 0;
        if (direction == 0) {
            step = -1;
        }
        else {
            step = 1;
        }
        int init = floor;
        if (!haveCurrentFloor) {
            init += step;
        }
        for (int i = init; i >= 1 && i <= 15; i += step) {
            if (outerFloor.getLength(i, type) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean checkInnerRequest(int direction, boolean haveCurrentFloor) {
        ;
        int step = 0;
        if (direction == 0) {
            step = -1;
        }
        else {
            step = 1;
        }
        int init = floor;
        if (!haveCurrentFloor) {
            init += step;
        }
        for (int i = init; i >= 1 && i <= 15; i += step) {
            if (lift.getLength(i, 0) != 0) {
                return true;
            }
        }
        return false;
    }

    public void sleep(long destination) {
        Date date = new Date();
        long currenttime = date.getTime();
        int duration = (int) (destination - currenttime);
        int d1 = duration - (int) (duration * 0.1);
        try {
            TimeUnit.MILLISECONDS.sleep(d1);
        }
        catch (InterruptedException e) {
            return;
        }
        while (true) {
            if (new Date().getTime() > destination) {
                return;
            }
        }
    }

    public void unload() {
        while (lift.getLength(floor, 0) != 0) {
            Request r = lift.get(floor, 0);
            release(r);
        }
    }

    public void load() {
        while (outerFloor.getLength(floor, direction) != 0) {
            Request r = outerFloor.get(floor, direction);
            recieve(r);
            lift.add(r, r.to(), 0);
        }
    }

    public void nextFloor() {
        sleep(Para.floorTime + time);
        time = new Date().getTime();
    }

    public void trigger() {
        if (outerFloor.getLength(floor, direction) != 0) {
            open();
            load();
            close(Para.doorTime * 2 + time);
        }
        nextFloor();
    }

    public void run() {
        while (!Thread.interrupted() && !(lift.isEmpty()
            && outerFloor.isEmpty() && signaled)) {
            time = new Date().getTime();
            if (direction != 2) { floor += 2 * direction - 1; }
            if (direction != 2) {
                //if the elevator is currently not still
                //then we check whether:
                //1.someone want to get off here
                //2.someone want to get boarded here(incidentally)
                if (lift.getLength(floor, 0) != 0
                    || outerFloor.getLength(floor, direction) != 0) {
                    //open the door
                    open();
                    unload();
                    //board the people who want to board(incidentally)
                    load();
                }
                //then we check if there is any request on current direction
                // in lift ;or anyone calling for a incidentally ride on
                // current direction(not current floor); or anyone want calling
                // for a opposite-direction ride on current floor
                //if so,we don't need to change the diretion and just carryon
                synchronized (outerFloor) {
                    if (checkInnerRequest(direction, false)
                        || checkOuterRequest(direction, direction, false)
                        || checkOuterRequest(direction, 1 - direction, false)) {
                        if (doorOpened) { close(time + Para.doorTime * 2); }
                        nextFloor();
                    }
                    else {
                        //check whether the elevator needs to change the
                        // direction
                        if (checkInnerRequest(1 - direction, false)
                            || checkOuterRequest(1 - direction, direction, true)
                            || checkOuterRequest(1 - direction, 1 - direction
                            , true)) {
                            //change the direction
                            direction = 1 - direction;
                            //if we need to load people
                            if (outerFloor.getLength(floor, direction) != 0) {
                                if (!doorOpened) { open(); }
                                load();
                            }
                            // if the door is not closed,shut and move
                            if (doorOpened) { close(Para.doorTime * 2 + time); }
                            //move
                            nextFloor();
                        }
                        else {
                            direction = 2;
                            if (doorOpened) { close(Para.doorTime * 2 + time); }
                            Thread.yield();
                        }
                    }
                }
            }
            else {
                synchronized (outerFloor) {
                    if (checkOuterRequest(1, 1, true)
                        || checkOuterRequest(1, 0, false)) {
                        direction = 1;
                        trigger();
                        continue;
                    }
                    else if (checkOuterRequest(0, 0, true)
                        || checkOuterRequest(0, 1, false)) {
                        direction = 0;
                        trigger();
                        continue;
                    }
                }
                Thread.yield();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        //test
        TimableOutput.initStartTimestamp();
        Buffer bf = new SynchronizedBuffer(2, null);

        Request r = new Request(1, 5, 2);
        bf.add(r, 5, 0);


        Elevator e = new Elevator(bf);
        Thread t = new Thread(e);
        t.start();
        //TimeUnit.MILLISECONDS.sleep(10000);

        r = new Request(2, 2, 3);
        bf.add(r, 2, 1);

        e.signalStop();

    }
}
