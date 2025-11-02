package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.TimeslotStartTimeContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) and unit tests for FindTimeslotCommand.
 */
public class FindTimeslotCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        TimeslotStartTimeContainsKeywordsPredicate firstPredicate =
                new TimeslotStartTimeContainsKeywordsPredicate(Collections.singletonList("2025-10-12"));
        TimeslotStartTimeContainsKeywordsPredicate secondPredicate =
                new TimeslotStartTimeContainsKeywordsPredicate(Collections.singletonList("0900"));

        FindTimeslotCommand findFirstCommand = new FindTimeslotCommand(firstPredicate);
        FindTimeslotCommand findSecondCommand = new FindTimeslotCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindTimeslotCommand findFirstCommandCopy = new FindTimeslotCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different predicate -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String keywords = "";
        String expectedMessage = MessageFormat.format(
                Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_COMMAND, 0, "findtimeslot")
                + ", found by time matching: []";
        TimeslotStartTimeContainsKeywordsPredicate predicate = preparePredicate(keywords);
        FindTimeslotCommand command = new FindTimeslotCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        // This test depends on the actual timeslot data in your typical persons
        // You'll need to adjust the keywords based on your test data

        // Example: Search for a specific date that exists in test data
        String keywords = "2025-10-12";
        String expectedMessage = MessageFormat.format(
                Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_COMMAND, 1, "findtimeslot")
                + ", found by date matching: [" + keywords + "]";
        TimeslotStartTimeContainsKeywordsPredicate predicate = preparePredicate("2025-10-12");
        FindTimeslotCommand command = new FindTimeslotCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_timeKeyword_personsFound() {
        // Test searching by time format (HHmm)
        String keywords = "0900";
        String expectedMessage = MessageFormat.format(
                Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_COMMAND, 0, "findtimeslot")
                + ", found by time matching: [" + keywords + "]";
        TimeslotStartTimeContainsKeywordsPredicate predicate = preparePredicate("0900");
        FindTimeslotCommand command = new FindTimeslotCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_mixedDateAndTimeKeywords_personsFound() {
        // Test searching with both date and time keywords
        String keywords = "2025-10-12, 0900, 1400";
        String expectedMessage = MessageFormat.format(
                Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_COMMAND, 0, "findtimeslot")
                + ", found by date and time matching: [" + keywords + "]";
        TimeslotStartTimeContainsKeywordsPredicate predicate = preparePredicate("2025-10-12", "0900", "1400");
        FindTimeslotCommand command = new FindTimeslotCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noMatchingKeywords_noPersonFound() {
        String keywords = "9999-12-31";
        String expectedMessage = MessageFormat.format(
                Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_COMMAND, 0, "findtimeslot")
                + ", found by date matching: [" + keywords + "]";
        TimeslotStartTimeContainsKeywordsPredicate predicate = preparePredicate("9999-12-31");
        FindTimeslotCommand command = new FindTimeslotCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        TimeslotStartTimeContainsKeywordsPredicate predicate =
                new TimeslotStartTimeContainsKeywordsPredicate(Arrays.asList("2025-10-12", "0900"));
        FindTimeslotCommand findTimeslotCommand = new FindTimeslotCommand(predicate);
        String expected = FindTimeslotCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findTimeslotCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code TimeslotStartTimeContainsKeywordsPredicate}.
     */
    private TimeslotStartTimeContainsKeywordsPredicate preparePredicate(String userInput) {
        return new TimeslotStartTimeContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }

    /**
     * Parses multiple user inputs into a {@code TimeslotStartTimeContainsKeywordsPredicate}.
     */
    private TimeslotStartTimeContainsKeywordsPredicate preparePredicate(String... userInputs) {
        return new TimeslotStartTimeContainsKeywordsPredicate(Arrays.asList(userInputs));
    }
}

