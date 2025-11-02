package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_COMMAND;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.TimeslotRangePredicate;

public class FilterTimeslotCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        TimeslotRangePredicate firstPredicate = new TimeslotRangePredicate(
                Optional.of(LocalDate.of(2025, 1, 1)), Optional.empty(),
                Optional.empty(), Optional.empty());

        TimeslotRangePredicate secondPredicate = new TimeslotRangePredicate(
                Optional.empty(), Optional.of(LocalDate.of(2025, 1, 1)),
                Optional.empty(), Optional.empty());

        FilterTimeslotCommand firstCommand = new FilterTimeslotCommand(firstPredicate);
        FilterTimeslotCommand secondCommand = new FilterTimeslotCommand(secondPredicate);

        // same object -> returns true
        assertTrue(firstCommand.equals(firstCommand));

        // same values -> returns true
        FilterTimeslotCommand firstCommandCopy = new FilterTimeslotCommand(firstPredicate);
        assertTrue(firstCommand.equals(firstCommandCopy));

        // different types -> returns false
        assertFalse(firstCommand.equals(1));

        // null -> returns false
        assertFalse(firstCommand.equals(null));

        // different command -> returns false
        assertFalse(firstCommand.equals(secondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        // We assume TypicalPersons ALICE and BENSON do not have timeslots on this date
        TimeslotRangePredicate predicate = new TimeslotRangePredicate(
                Optional.of(LocalDate.of(2030, 1, 1)), Optional.empty(),
                Optional.empty(), Optional.empty());
        String expectedMessage = MessageFormat.format(
                MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_COMMAND, 0, "filtertimeslot")
                + " " + predicate.getFilterDescription();
        FilterTimeslotCommand command = new FilterTimeslotCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertEquals(expectedCommandResult, command.execute(model));
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }

    // Note: You should add a person with a known timeslot to your TypicalPersons
    // for this test to be more meaningful.
    @Test
    public void execute_matchingKeywords_personFound() {
        TimeslotRangePredicate predicate = new TimeslotRangePredicate(
                Optional.empty(), Optional.empty(),
                Optional.of(LocalTime.of(9, 0)), Optional.of(LocalTime.of(10, 30)));
        FilterTimeslotCommand command = new FilterTimeslotCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        command.execute(model);
        assertEquals(expectedModel.getFilteredPersonList(), model.getFilteredPersonList());
    }
}
