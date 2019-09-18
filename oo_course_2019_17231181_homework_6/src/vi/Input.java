package vi;

import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.PersonRequest;

public class Input implements Runnable {
    private Elevator[] elevators;
    private Buffer buf;
    private Scheduler sch;

    Input(Elevator[] elevators, Buffer buf, Scheduler sch) {
        this.buf = buf;
        this.elevators = elevators;
        this.sch = sch;
    }

    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            // when request == null
            // it means there are no more lines in stdin
            if (request == null) {
                sch.signalStop();
                for (int i = 0; i < Kit.elevatorNum; i++) {
                    elevators[i].signalStop();
                }
                break;
            }
            else {
                //System.out.println("HERE");
                Request r = new Request(request.getPersonId(),
                    request.getFromFloor(), request.getToFloor());
                //System.out.println("HERE");

                //System.out.println("input:added");
                synchronized (sch) {
                    //System.out.println(request);
                    buf.add(r);
                    sch.notifyAll();
                    //System.out.println("input:notified");
                }
                //System.out.println(request);
            }
        }

        try {
            elevatorInput.close();
        }
        catch (Exception e) {
            return;
        }
    }
}
