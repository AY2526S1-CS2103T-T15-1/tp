package seedu.address.model.person.exceptions;

/**
 * Exception indicating a user inputting a timeslot in the past
 */
public class PastTimeSlotException extends RuntimeException {
    public PastTimeSlotException(String message) {
        super(message);
    }
}
