package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.TimeSlot;

/**
 * Clears past timeslots.
 * Deletes non-recurring past contacts.
 * Updates recurring past contacts to their next occurrence.
 */
public class ClearPastCommand extends Command {

    public static final String COMMAND_WORD = "clearpast";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Clears all past timeslots from the address book.\n"
            + "Contacts with a 'recurring' tag will be updated to their next "
            + "weekly timeslot. All other past contacts will be deleted.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "ClearPast command successful.";
    public static final String MESSAGE_DELETED = "\nDeleted %d past contact(s): %s";
    public static final String MESSAGE_UPDATED = "\nUpdated %d recurring contact(s): %s";
    public static final String MESSAGE_CONFLICTS = "\nCould not update %d recurring contact(s) due to conflicts: %s";
    public static final String MESSAGE_NO_CHANGES = "No past timeslots found to clear or update.";

    public static final String RECURRING_TAG_NAME = "recurring";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        LocalDateTime now = LocalDateTime.now();

        // We must use a copy of the list to avoid ConcurrentModificationException
        List<Person> fullList = new ArrayList<>(model.getAddressBook().getPersonList());

        List<Person> personsToDelete = new ArrayList<>();
        List<PersonToUpdate> personsToUpdate = new ArrayList<>();

        // 1. Find all contacts to delete or update
        for (Person person : fullList) {
            if (person.getTimeSlot().isPast(now)) {
                // Check if person has the "recurring" tag
                boolean isRecurring = person.getTags().stream()
                        .map(tag -> tag.tagName)
                        .anyMatch(tagName -> tagName.equalsIgnoreCase(RECURRING_TAG_NAME));

                if (isRecurring) {
                    personsToUpdate.add(new PersonToUpdate(person, person.getTimeSlot().getNextOccurrence(now)));
                } else {
                    personsToDelete.add(person);
                }
            }
        }

        // 2. Perform deletions and get names
        List<String> deletedNames = new ArrayList<>();
        for (Person person : personsToDelete) {
            model.deletePerson(person);
            deletedNames.add(person.getName().toString());
        }

        // 3. Perform updates and get names/conflicts
        List<String> updatedNames = new ArrayList<>();
        List<String> conflictNames = new ArrayList<>();

        for (PersonToUpdate ptu : personsToUpdate) {
            Person updatedPerson = new Person(
                    ptu.oldPerson.getName(), ptu.oldPerson.getPhone(), ptu.oldPerson.getEmail(),
                    ptu.oldPerson.getAddress(), ptu.newTimeSlot, ptu.oldPerson.getTags()
            );

            try {
                model.setPerson(ptu.oldPerson, updatedPerson);
                updatedNames.add(updatedPerson.getName().toString());
            } catch (Exception e) {
                conflictNames.add(ptu.oldPerson.getName().toString());
            }
        }

        // 4. Build and return the result message
        StringBuilder result = new StringBuilder(MESSAGE_SUCCESS);
        int changes = 0;

        if (!deletedNames.isEmpty()) {
            result.append(String.format(MESSAGE_DELETED,
                    deletedNames.size(), String.join(", ", deletedNames)));
            changes += deletedNames.size();
        }
        if (!updatedNames.isEmpty()) {
            result.append(String.format(MESSAGE_UPDATED,
                    updatedNames.size(), String.join(", ", updatedNames)));
            changes += updatedNames.size();
        }
        if (!conflictNames.isEmpty()) {
            result.append(String.format(MESSAGE_CONFLICTS,
                    conflictNames.size(), String.join(", ", conflictNames)));
            changes += conflictNames.size();
        }

        if (changes == 0) {
            return new CommandResult(MESSAGE_NO_CHANGES);
        }

        // --- SEE FIX FOR QUESTION 2 BELOW ---
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(result.toString());
    }

    /**
     * Helper inner class to store the old person and their new timeslot.
     */
    private static class PersonToUpdate {
        final Person oldPerson;
        final TimeSlot newTimeSlot;

        PersonToUpdate(Person oldPerson, TimeSlot newTimeSlot) {
            this.oldPerson = oldPerson;
            this.newTimeSlot = newTimeSlot;
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ClearPastCommand; // This command has no fields
    }
}
