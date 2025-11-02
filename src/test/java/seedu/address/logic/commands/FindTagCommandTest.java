package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.TagContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code FindTagCommand}.
 */
public class FindTagCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    }

    @Test
    public void equals() {
        TagContainsKeywordsPredicate firstPredicate =
                new TagContainsKeywordsPredicate(Collections.singletonList("friends"));
        TagContainsKeywordsPredicate secondPredicate =
                new TagContainsKeywordsPredicate(Collections.singletonList("owesMoney"));

        FindTagCommand firstCommand = new FindTagCommand(firstPredicate);
        FindTagCommand secondCommand = new FindTagCommand(secondPredicate);

        // same object -> returns true
        assertEquals(firstCommand, firstCommand);

        // same values -> returns true
        FindTagCommand firstCommandCopy = new FindTagCommand(firstPredicate);
        assertEquals(firstCommand, firstCommandCopy);

        // different types -> returns false
        assertNotEquals(1, firstCommand);

        // null -> returns false
        assertNotEquals(null, firstCommand);

        // different predicate -> returns false
        assertNotEquals(firstCommand, secondCommand);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        // This test covers the requireNonNull(model) line
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Collections.singletonList("test"));
        FindTagCommand command = new FindTagCommand(predicate);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }
    //==============================================================

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = MessageFormat.format(
                Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_COMMAND, 0, "findtag") + " with tag(s): []";
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Collections.emptyList());
        FindTagCommand command = new FindTagCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        // No persons should match an empty keyword list
        assertEquals(0, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_multipleKeywords_filtersListSuccessfully() {
        // This test checks that the predicate filters successfully without depending on specific persons
        var keywords = Arrays.asList("friends", "owesMoney");
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(keywords);
        FindTagCommand command = new FindTagCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = MessageFormat.format(
                Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_COMMAND,
                expectedModel.getFilteredPersonList().size(), "findtag");
        expectedMessage += " with tag(s): " + keywords;
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        // Ensure filtered list matches expected count
        assertEquals(expectedModel.getFilteredPersonList().size(), model.getFilteredPersonList().size());
    }

    @Test
    public void execute_singleKeyword_findsPersons() {
        // We use TypicalPersons data here. ALICE, BENSON, and DANIEL have the 'friends' tag.
        String keywords = "friends";
        String expectedMessage = MessageFormat.format(
                Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_COMMAND, 3, "findtag")
                + " with tag(s): [" + keywords + "]";
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Collections.singletonList(keywords));
        FindTagCommand command = new FindTagCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        // We check the contents, assuming ALICE, BENSON, DANIEL are the matches and sorted by timeslot
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_findsPersons() {
        // BENSON has 'owesMoney', ALICE and DANIEL have 'friends'.
        // The predicate finds contacts that match *any* keyword.
        String keywords = "owesMoney, friends";
        String expectedMessage = MessageFormat.format(
                Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_COMMAND, 3, "findtag")
                + " with tag(s): [" + keywords + "]";
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(
                Arrays.asList("owesMoney", "friends"));
        FindTagCommand command = new FindTagCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_noMatchingKeywords_noPersonFound() {
        String keywords = "nonexistent, tag";
        String expectedMessage = MessageFormat.format(
                Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_COMMAND, 0, "findtag")
                + " with tag(s): [" + keywords + "]";
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Arrays.asList("nonexistent", "tag"));
        FindTagCommand command = new FindTagCommand(predicate);

        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    //=========== NEW TEST =========================================
    @Test
    public void getPredicate_returnsCorrectPredicate() {
        // This test covers the getPredicate() method
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Collections.singletonList("test"));
        FindTagCommand command = new FindTagCommand(predicate);
        assertEquals(predicate, command.getPredicate());
    }

    @Test
    public void toStringMethod_correctString() {
        // This test covers the toString() method
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Arrays.asList("tag1", "tag2"));
        FindTagCommand command = new FindTagCommand(predicate);
        String expected = FindTagCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, command.toString());
    }
}
