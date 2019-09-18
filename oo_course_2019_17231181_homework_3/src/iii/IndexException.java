package iii;

public class IndexException extends Exception {
    IndexException() {
        super("Index Out of Range");
    }

    IndexException(String m) {
        super(m);
    }
}
