package v;

import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;

public class Input implements Runnable {
    private Buffer buf;
    private Elevator[] elevators;

    Input(Buffer buf, Elevator[] elevators) {
        this.buf = buf;
        this.elevators = elevators;
    }

    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            // when request == null
            // it means there are no more lines in stdin
            if (request == null) {
                for (int i = 0; i < Para.elevatorNum; i++) {
                    elevators[i].signalStop();
                }
                break;
            }
            else {
                Request r = new Request(request.getPersonId(),
                    request.getFromFloor(), request.getToFloor());
                buf.add(r, r.from(), r.direction());
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
