package seedu.duke;

public class DukeException extends Exception {
    public DukeException(String errorMessage) {
        super(errorMessage);
        System.out.println(errorMessage);
    }
}