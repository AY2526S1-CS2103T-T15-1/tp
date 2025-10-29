package seedu.address.model.person.exceptions;

import seedu.address.logic.Messages;
import seedu.address.model.person.Person;
/**
 * Signals that the operation will result in a timeslot conflict.
 */
public class TimeSlotConflictException extends RuntimeException {

    private final Person conflictingPerson;

    /**
     * @param conflictingPerson The person that causes the conflict.
     *      Creates a message that also includes the specific person and timeslots that causes conflict
     */
    public TimeSlotConflictException(Person conflictingPerson) {
        super(Messages.MESSAGE_TIMESLOT_CONFLICT
                + " " + conflictingPerson.getName()
                + " [" + conflictingPerson.getTimeSlot() + "]");
        this.conflictingPerson = conflictingPerson;
    }

    public Person getConflictingPerson() {
        return conflictingPerson;
    }
}
