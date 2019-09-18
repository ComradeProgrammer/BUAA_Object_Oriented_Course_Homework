package vi;

public class ElevatorSystem {
    public static void main(String[] args) {
        final Buffer buf = new Buffer();
        OuterFloor o = new OuterFloor(Kit.availableFloors);
        Elevator e = new Elevator(o, Kit.availableFloors);
        o.setElevator(e);
        OuterFloor[] ol = new OuterFloor[1];
        Elevator[] el = new Elevator[1];
        ol[0] = o;
        el[0] = e;
        Scheduler s = new Scheduler(ol, buf);
        Input input = new Input(el, buf, s);
        new Thread(e).start();
        new Thread(s).start();
        new Thread(input).start();

    }
}
