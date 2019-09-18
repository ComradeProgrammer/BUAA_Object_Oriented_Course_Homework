package iii;

public class FormatException extends Exception {
    FormatException() {
        super(" Wrong Format");
    }

    FormatException(String m) {
        super(m);
    }
}
