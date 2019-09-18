package vii;

import com.oocourse.elevator3.PersonRequest;

import java.util.LinkedList;

public class Request {
    private boolean stop = false;
    private int id0 = 0;
    private int from0 = 0;
    private int to0 = 0;
    private int direction0 = 0;
    private boolean respond = false;

    Request(PersonRequest r) {
        //System.out.println(r);
        if (r == null) {
            stop = true;
            return;
        }
        from0 = r.getFromFloor();
        to0 = r.getToFloor();
        id0 = r.getPersonId();
        stop = false;
        if (from0 < to0) {
            direction0 = 1;
        }
        else {
            direction0 = 0;
        }
    }

    Request(int id, int from, int to, boolean respond) {
        id0 = id;
        from0 = from;
        to0 = to;
        if (from0 < to0) {
            direction0 = 1;
        }
        else {
            direction0 = 0;
        }
        this.respond = respond;
    }

    public int id() {
        return this.id0;
    }

    public int from() {
        return this.from0;
    }

    public int to() {
        return this.to0;
    }

    public boolean isStop() {
        return this.stop;
    }

    public int direction() {
        return this.direction0;
    }

    public boolean shouldRespond() {
        return this.respond;
    }

    public LinkedList<Request> selfGen() {
        //return null;
        // TODO IMPLEMENTS;
        int a = this.from();
        int b = this.to();
        for (int i = 0; i < 3; i++) {
            if (Main.floors[i].contains(a) && Main.floors[i].contains(b)) {
                return null;
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == j) {
                    continue;
                }
                if (Main.floors[i].contains(a) && Main.floors[j].contains(b)) {
                    int tmpa;
                    int tmpb;
                    if (a < 0) {
                        tmpa = a + 1;
                    }
                    else {
                        tmpa = a;
                    }
                    if (b < 0) {
                        tmpb = b + 1;
                    }
                    else {
                        tmpb = b;
                    }
                    int reload = 0;
                    LinkedList<Request> tmp = new LinkedList<>();
                    if (Math.abs(tmpa - 1) + Math.abs(tmpb - 1)
                        < Math.abs(tmpa - 15)
                        + Math.abs(tmpb - 15)) {
                        reload = 1;
                    }
                    else {
                        reload = 15;
                    }
                    tmp.add(new Request(id0, a, reload, true));
                    tmp.add(new Request(id0, reload, b, false));
                    return tmp;
                }
            }
        }
        System.out.println("error");
        return null;
    }
}
