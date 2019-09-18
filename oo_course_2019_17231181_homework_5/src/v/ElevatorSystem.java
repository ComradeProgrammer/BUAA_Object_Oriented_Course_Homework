package v;

import com.oocourse.TimableOutput;

public class ElevatorSystem {
    private Elevator[] elevators;
    private Input input;
    private Thread[] elevatorThread;
    private Thread inputThread;
    private Buffer floor;

    ElevatorSystem() {
        elevators = new Elevator[Para.elevatorNum];
        elevatorThread = new Thread[Para.elevatorNum];
        floor = new SynchronizedBuffer(2, elevators);
        input = new Input(floor, elevators);
        inputThread = new Thread(input);
        TimableOutput.initStartTimestamp();
        for (int i = 0; i < Para.elevatorNum; i++) {
            elevators[i] = new Elevator(floor);
            elevatorThread[i] = new Thread(elevators[i]);
        }
    }

    public void run() {
        for (int i = 0; i < Para.elevatorNum; i++) {
            elevatorThread[i].start();
        }
        inputThread.start();
    }

    public static void main(String[] args) {
        ElevatorSystem s = new ElevatorSystem();
        s.run();
    }
}
