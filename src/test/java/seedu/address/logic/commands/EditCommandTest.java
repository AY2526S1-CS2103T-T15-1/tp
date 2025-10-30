package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Storage storage = new StorageManager(
            new JsonAddressBookStorage(Paths.get("data", "dummy.json")),
            new JsonUserPrefsStorage(Paths.get("data", "dummyprefs.json"))
    );
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs(), storage);

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        // Note: This test now uses the fixed ModelManager.
        // It edits ALICE (index 1) to be a new person.
        Person personToEdit = model.getFilteredPersonList().get(0);
        Person editedPerson = new PersonBuilder()
                .withTimeSlot("2030-01-01 1000-1100") // Give a new, free timeslot
                .build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = getExpectedSuccessMessage(personToEdit, editedPerson, descriptor);

        // 1. Create a *new, separate* storage for the expected model.
        // This assumes you have 'Paths' and other storage classes imported.
        // The paths can be anything, as long as it's a new instance.
        Storage expectedStorage = new StorageManager(
                new JsonAddressBookStorage(Paths.get("data", "dummy-expected.json")),
                new JsonUserPrefsStorage(Paths.get("data", "dummyprefs-expected.json"))
        );

        // 2. Create expectedModel using the *new* storage
        Model expectedModel = new ModelManager(
                new AddressBook(model.getAddressBook()), new UserPrefs(), expectedStorage);
        expectedModel.setPerson(personToEdit, editedPerson);
        expectedModel.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person personToEdit = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(personToEdit);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = getExpectedSuccessMessage(personToEdit, editedPerson, descriptor);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = personToEdit; // No changes
        EditPersonDescriptor descriptor = new EditPersonDescriptor(); // Empty descriptor

        // --- USE HELPER ---
        String expectedMessage = getExpectedSuccessMessage(personToEdit, editedPerson, descriptor);
        // --- END HELPER ---

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withName(VALID_NAME_BOB).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = getExpectedSuccessMessage(personToEdit, editedPerson, descriptor);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_timeslotConflict_failure() {
        // Get person 1 (ALICE) and person 2 (BENSON)
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person conflictingPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        // Try to edit ALICE to have BENSON's timeslot
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withTimeSlot(conflictingPerson.getTimeSlot().toString())
                .build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = Messages.MESSAGE_TIMESLOT_CONFLICT
                + " " + conflictingPerson.getName()
                + " [" + conflictingPerson.getTimeSlot() + "]";

        // Expect a command failure with the conflict message
        // The message comes from the TimeSlotConflictException we created
        assertCommandFailure(editCommand, model,
                expectedMessage);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void execute_duplicatePhone_failure() {
        // Model contains ALICE (94351253) and BENSON (98765432)

        // Descriptor to change BENSON's phone to ALICE's phone
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(ALICE.getPhone().value) // ALICE's phone
                .build();

        // Create the command targeting BENSON (INDEX_SECOND_PERSON)
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);
        String expectedError = "This phone number already exists in the address book, assigned to: "
                + ALICE.getName();
        // Expect a command failure with the duplicate phone message
        assertCommandFailure(editCommand, model, expectedError);
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    /**
     * Helper method to construct the expected success message for EditCommand,
     * including the "Edited Person" details and the "Changes" list.
     * This mimics the logic used in EditCommand itself.
     */
    private String getExpectedSuccessMessage(Person personToEdit, Person editedPerson,
                                             EditPersonDescriptor descriptor) {
        // --- Replicate the logic from EditCommand's buildEditedFieldsString ---
        String changesString = java.util.stream.Stream.of(
                        descriptor.getName().map(val -> "Name: '" + personToEdit.getName()
                                + "' -> '" + editedPerson.getName() + "'"),
                        descriptor.getPhone().map(val -> "Phone: '" + personToEdit.getPhone()
                                + "' -> '" + editedPerson.getPhone() + "'"),
                        descriptor.getEmail().map(val -> "Email: '" + personToEdit.getEmail()
                                + "' -> '" + editedPerson.getEmail() + "'"),
                        descriptor.getAddress().map(val -> "Address: '" + personToEdit.getAddress()
                                + "' -> '" + editedPerson.getAddress() + "'"),
                        descriptor.getTimeSlot().map(val -> "Timeslot: '" + personToEdit.getTimeSlot()
                                + "' -> '" + editedPerson.getTimeSlot() + "'"),
                        descriptor.getTags().map(val -> "Tags: '" + formatTagsForDisplay(personToEdit.getTags())
                                + "' -> '" + formatTagsForDisplay(editedPerson.getTags()) + "'")
                )
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(java.util.stream.Collectors.joining(", "));

        String changesSummary;
        if (changesString.isEmpty()) {
            changesSummary = "No changes were applied.";
        } else {
            changesSummary = "Changes: [" + changesString + "]";
        }

        return String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson))
                + "\n" + changesSummary;
    }

    /**
     * Formats a Set of Tags for display, without the outer Set brackets.
     */
    private String formatTagsForDisplay(java.util.Set<seedu.address.model.tag.Tag> tags) {
        final StringBuilder builder = new StringBuilder();
        tags.forEach(builder::append);
        return builder.toString();
    }
}
