package seedu.address.model.person.exceptions;

import seedu.address.model.person.Person; // Add import

/**
 * Signals that the operation would result in duplicate phone numbers
 * (between different persons).
 */
public class DuplicatePhoneException extends RuntimeException {

    private final Person conflictingPerson; // Add field

    /**
     * @param conflictingPerson The existing person with the duplicate phone number.
     */
    public DuplicatePhoneException(Person conflictingPerson) { // Modify constructor
        super("This phone number already exists in the address book, assigned to: "
                + conflictingPerson.getName()); // Build specific message
        this.conflictingPerson = conflictingPerson;
    }

    public Person getConflictingPerson() {
        return conflictingPerson;
    }
}
