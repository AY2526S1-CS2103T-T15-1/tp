package seedu.address.model.person.exceptions;

public class PastTimeSlotException extends RuntimeException {
    public PastTimeSlotException(String message) {
        super(message);
    }
}
