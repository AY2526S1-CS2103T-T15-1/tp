package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.TimeSlot;
import seedu.address.model.person.exceptions.TimeSlotConflictException;

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
    private static final Logger logger = LogsCenter.getLogger(ClearPastCommand.class);

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        LocalDateTime now = LocalDateTime.now();

        // We must use a copy of the list to avoid ConcurrentModificationException
        List<Person> fullList = new ArrayList<>(model.getAddressBook().getPersonList());

        List<Person> personsToDelete = new ArrayList<>();
        List<PersonToUpdate> personsToUpdate = new ArrayList<>();

        // 1. Find and categorize all contacts to delete or update
        categorizePastContacts(fullList, now, personsToDelete, personsToUpdate);

        // 2. Perform deletions and get names
        List<String> deletedNames = performDeletions(model, personsToDelete);

        // 3. Perform updates and get names/conflicts
        List<String> updatedNames = new ArrayList<>();
        List<String> conflictNames = new ArrayList<>();
        performUpdates(model, personsToUpdate, updatedNames, conflictNames);

        // 4. Build and return the result message
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return buildCommandResult(deletedNames, updatedNames, conflictNames);
    }

    /**
     * Iterates through the full list and populates the to-delete and to-update lists.
     */
    private void categorizePastContacts(List<Person> fullList, LocalDateTime now,
                                        List<Person> personsToDelete, List<PersonToUpdate> personsToUpdate) {
        for (Person person : fullList) {
            if (person.getTimeSlot() != null && person.getTimeSlot().isPast(now)) {
                if (isRecurring(person)) {
                    personsToUpdate.add(new PersonToUpdate(person, person.getTimeSlot().getNextOccurrence(now)));
                } else {
                    personsToDelete.add(person);
                }
            }
        }
    }

    /**
     * Checks if a person has the "recurring" tag (case-insensitive).
     */
    private boolean isRecurring(Person person) {
        return person.getTags().stream()
                .map(tag -> tag.tagName)
                .anyMatch(tagName -> tagName.equalsIgnoreCase(RECURRING_TAG_NAME));
    }

    /**
     * Deletes all persons in the list from the model and returns their names.
     */
    private List<String> performDeletions(Model model, List<Person> personsToDelete) {
        List<String> deletedNames = new ArrayList<>();
        for (Person person : personsToDelete) {
            model.deletePerson(person);
            deletedNames.add(person.getName().toString());
        }
        return deletedNames;
    }

    /**
     * Updates all persons in the list in the model.
     * Populates updatedNames and conflictNames lists based on the outcome.
     */
    private void performUpdates(Model model, List<PersonToUpdate> personsToUpdate,
                                List<String> updatedNames, List<String> conflictNames) {
        for (PersonToUpdate ptu : personsToUpdate) {
            Person updatedPerson = new Person(
                    ptu.oldPerson.getName(), ptu.oldPerson.getPhone(), ptu.oldPerson.getEmail(),
                    ptu.oldPerson.getAddress(), ptu.newTimeSlot, ptu.oldPerson.getTags()
            );

            try {
                model.setPerson(ptu.oldPerson, updatedPerson);
                updatedNames.add(updatedPerson.getName().toString());
            } catch (TimeSlotConflictException e) {
                String originalMessage = e.getMessage();
                String conflictDetails = originalMessage;
                String prefix = "This time slot conflicts with another existing time slot! ";
                if (originalMessage.startsWith(prefix)) {
                    conflictDetails = originalMessage.substring(prefix.length());
                }
                conflictNames.add(ptu.oldPerson.getName().toString()
                        + "'s next recurring slot [" + ptu.newTimeSlot.toString()
                        + "] conflicts with " + conflictDetails);
            } catch (Exception e) {
                // Catch other potential errors
                logger.warning("Unexpected error while updating recurring person ");
                conflictNames.add(ptu.oldPerson.getName().toString() + " (Error: " + e.getMessage() + ")");
            }
        }
    }

    /**
     * Builds the final CommandResult based on the lists of changed names.
     */
    private CommandResult buildCommandResult(List<String> deletedNames, List<String> updatedNames,
                                             List<String> conflictNames) {
        if (deletedNames.isEmpty() && updatedNames.isEmpty() && conflictNames.isEmpty()) {
            return new CommandResult(MESSAGE_NO_CHANGES);
        }

        StringBuilder result = new StringBuilder();
        if (!deletedNames.isEmpty()) {
            result.append(String.format(MESSAGE_DELETED,
                    deletedNames.size(), String.join(", ", deletedNames)));
        }
        if (!updatedNames.isEmpty()) {
            result.append(String.format(MESSAGE_UPDATED,
                    updatedNames.size(), String.join(", ", updatedNames)));
        }
        if (!conflictNames.isEmpty()) {
            result.append(String.format(MESSAGE_CONFLICTS,
                    conflictNames.size(), String.join(", ", conflictNames)));
        }
        return new CommandResult(result.toString().trim());
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
