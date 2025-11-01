package seedu.address.model.person.exceptions;

import seedu.address.model.person.Person;

public class DuplicateEmailException extends RuntimeException {
    private final Person conflictingPerson;

    /**
     * @param conflictingPerson The existing person with the duplicate email.
     */
    public DuplicateEmailException(Person conflictingPerson) {
        super("This email already exists in the address book, assigned to: "
                + conflictingPerson.getName());
        this.conflictingPerson = conflictingPerson;
    }
}
