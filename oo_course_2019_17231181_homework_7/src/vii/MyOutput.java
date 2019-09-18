package vii;

import com.oocourse.TimableOutput;

public class MyOutput {
    /*static{

    }*/
    public static synchronized long println(String s) {
        return TimableOutput.println(s);
    }
}
