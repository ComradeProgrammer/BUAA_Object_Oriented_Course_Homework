package vii;

import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.PersonRequest;

public class Input implements Runnable {
    private Scheduler sch;

    Input(Scheduler sch) {
        this.sch = sch;
    }

    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            if (request == null) {
                sch.stop();
                break;
            }
            else {
                sch.add(new Request(request));
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
