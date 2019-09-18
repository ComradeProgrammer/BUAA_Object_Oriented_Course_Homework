package vii;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Elevator implements Runnable {
    private int id;
    private Inside inside;
    private Outside outside;
    private int floor;
    private int direction;
    private int capacity;
    private boolean doorOpened;
    private int perFloor;
    private int perDoor;
    private int amount;
    private List<Integer> availableFloors;
    private boolean stopped;
    private long time = 0;
    private Scheduler sch = null;

    Elevator(List<Integer> availableFloors, Outside outside, int perFloor,
             int perDoor, int initFloor, int capacity, int id, Scheduler sch) {
        this.availableFloors = availableFloors;
        this.inside = new Inside(this.availableFloors);
        this.outside = outside;
        this.floor = initFloor;
        this.direction = 2;
        this.capacity = capacity;
        this.doorOpened = false;
        this.perFloor = perFloor;
        this.perDoor = perDoor;
        this.amount = 0;
        this.stopped = false;
        this.id = id;
        this.sch = sch;
        time = new Date().getTime();
    }

    public int nextFloor(int floor, int direction) {
        return Main.allFloors.get(
            Main.allFloors.indexOf(floor) + 2 * direction - 1);
    }

    public synchronized void stop() {
        stopped = true;
        this.notifyAll();
    }

    public synchronized void setFloor(int newFloor) {
        this.floor = newFloor;
    }

    public synchronized void setDirection(int newdirection) {
        this.direction = newdirection;
    }

    private void open() {
        try {
            time = MyOutput.println("OPEN-" + floor + "-" + (char) (id + 'A'));
            TimeUnit.MILLISECONDS.sleep(2 * perDoor);
            /*synchronized(this){
                this.wait(2*perDoor);
            }*/

        }
        catch (InterruptedException e) {
            System.out.println("error:interrupted");
        }

        doorOpened = true;
    }

    private void close() {
        time = MyOutput.println("CLOSE-" + floor + "-" + (char) (id + 'A'));
        doorOpened = false;
    }

    private void letout() {
        List<Request> tmp = inside.get(floor);
        for (Request r : tmp) {
            MyOutput.println("OUT-" + r.id() + "-" + floor + "-" +
                (char) (id + 'A'));
            amount--;
            if (r.shouldRespond()) {
                sch.respond(r);
            }
        }
    }

    private void letin() {
        List<Request> tmp = outside.get(floor, direction, capacity - amount,
            id);
        for (Request r : tmp) {
            MyOutput.println("IN-" + r.id() + "-" + floor +
                "-" + (char) (id + 'A'));
            amount++;
            inside.add(r);
        }
    }

    private synchronized void still() {
        if (outside.isEmpty() && inside.isEmpty() && !stopped) {
            setDirection(2);
            try {
                // System.out.println("wait:"+id);
                this.wait();
            }
            catch (InterruptedException e) {
                System.out.println("error:interrupted");
            }
            // System.out.println("notifyed"+id);

        }
    }

    private void move() {
        int newfloor = nextFloor(floor, direction);
        setFloor(newfloor);
        //long duration = time + perFloor - new Date().getTime();
        try {
            TimeUnit.MILLISECONDS.sleep(perFloor);
            /*synchronized(this){
                this.wait(perFloor);
            }*/
        }
        catch (InterruptedException e) {
            return;
        }
        time = MyOutput.println("ARRIVE-" + floor + "-" + (char) (id + 'A'));
    }

    private void func1() {
        open();
        letout();
        letin();
    }

    private void func2() {
        setDirection(1 - direction);
        letin();
        close();
        move();
    }

    private void func3() {
        open();
        letin();
        close();
    }

    private void func4() {
        letin();
        close();
    }

    private void func5() {
        close();
        move();
    }

    private void func6() {
        if (outside.shouldLoad(floor, direction, id)) {
            if (!doorOpened) { open(); }
            func4();//letin();close();
        }
        if (doorOpened) { close(); }
        move();
    }

    private void func7() {
        setDirection(1 - direction);
        if (outside.shouldLoad(floor, direction, id)) {
            func3(); }
        move();
    }

    public void run() {
        while (true) {
            synchronized (this) {
                if (inside.isEmpty() && outside.isEmpty() && stopped) { break; }
            }
            if (!availableFloors.contains(floor)) {
                move();
                continue;
            }
            if (direction != 2) {
                if ((amount == capacity && inside.shouldUnload(floor))
                    || (amount < capacity && (inside.shouldUnload(floor)
                    || outside.shouldLoad(floor, direction, id)))) {
                    func1();
                    if (inside.seek(floor, direction, false)
                        || outside.seek(floor, direction, id, false)) {
                        func5();
                        continue;
                    }
                    if (inside.seek(floor, 1 - direction, false)
                        || outside.seek(floor, 1 - direction, id, false)
                        || outside.shouldLoad(floor, 1 - direction, id)) {
                        func2();
                        continue;
                    }
                    still();//continue;
                }
                else {
                    if (inside.seek(floor, direction, false)
                        || outside.seek(floor, direction, id, false)) {
                        move();
                        continue;
                    }
                    if (inside.seek(floor, 1 - direction, false) || outside.
                        seek(floor, 1 - direction, id, false)
                        || outside.shouldLoad(floor, 1 - direction, id)) {
                        func7();
                        continue;
                    }
                    still();//continue;
                }
            }
            else {
                if (outside.seek(floor, 1, id, false)
                    || outside.shouldLoad(floor, 1, id)) {
                    setDirection(1);
                    func6();
                    continue;
                }
                if (outside.seek(floor, 0, id, false)
                    || outside.shouldLoad(floor, 0, id)) {
                    setDirection(0);
                    func6();
                }
                still();
                continue;
            } //}
        }
        if (doorOpened) { close(); }
    }

    /*public static void main(String[] args) throws Exception{
        Elevator[] els = new Elevator[1];
        Outside o = new Outside(els);
        //ArrayList<Integer> fuck=new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8
            //,9,10));
        TimableOutput.initStartTimestamp();
        Elevator e = new Elevator(Main.bFloors, o, 400, 200, 1, 2, 1);
        els[0] = e;
        new Thread(e).start();
        TimeUnit.MILLISECONDS.sleep(1000);
        o.add(new Request(1, 2, 5, false));
        o.add(new Request(3, 2, 5, false));

        TimeUnit.MILLISECONDS.sleep(200);
        o.add(new Request(2, 6, -1, false));
        o.add(new Request(4,4,5,false));

        TimeUnit.MILLISECONDS.sleep(10000);
        e.stop();


    }*/
}
