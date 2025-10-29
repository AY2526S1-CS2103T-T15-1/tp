package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Messages;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.DuplicatePhoneException;
import seedu.address.model.person.exceptions.TimeSlotConflictException;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.PersonBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();
    private Storage storage;
    private AddressBook addressBook;
    private UserPrefs userPrefs;

    @BeforeEach // ADD THIS WHOLE METHOD
    public void setUp() {
        // We need a real storage manager for these tests
        storage = new StorageManager(
                new JsonAddressBookStorage(Paths.get("data", "dummy.json")),
                new JsonUserPrefsStorage(Paths.get("data", "dummyprefs.json"))
        );
        addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        userPrefs = new UserPrefs();
        modelManager = new ModelManager(addressBook, userPrefs, storage);
    }

    @Test
    public void constructor() {
        modelManager = new ModelManager();
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setAddressBookFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookFilePath(path);
        assertEquals(path, modelManager.getAddressBookFilePath());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void addPerson_duplicatePerson_throwsDuplicatePersonException() {
        assertThrows(DuplicatePersonException.class, () -> modelManager.addPerson(ALICE));
    }

    @Test
    public void addPerson_timeslotConflict_throwsTimeSlotConflictException() {
        // Create a new person (BOB) but give them ALICE's timeslot
        Person conflictingPerson = new PersonBuilder(BOB)
                .withTimeSlot(ALICE.getTimeSlot().toString()) // ALICE's slot
                .build();

        // 1. Calculate the expected error message
        String expectedError = Messages.MESSAGE_TIMESLOT_CONFLICT
                + " " + ALICE.getName()
                + " [" + ALICE.getTimeSlot() + "]";

        // 2. Use the 3-argument assertThrows helper
        assertThrows(TimeSlotConflictException.class, expectedError, ()
                -> modelManager.addPerson(conflictingPerson));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        // ALICE and BENSON are in the setUp model
        assertFalse(modelManager.hasPerson(new PersonBuilder().withName("Carl").build()));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        // ALICE is added in setUp
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void deletePerson_personExists_removesPersonAndFreesSlot() {
        // 1. ALICE exists and her slot is "taken"
        assertTrue(modelManager.hasPerson(ALICE));

        // 2. A new person with ALICE's slot would conflict
        Person aliceClone = new PersonBuilder(ALICE).withName("Alice Clone").build();
        // We test conflict by trying to add her slot to storage directly
        // Note: We remove it first because loadExistingSlots added it
        modelManager.getStorage().removeSlot(ALICE.getTimeSlot());
        assertTrue(modelManager.getStorage().addSlot(ALICE.getTimeSlot())); // Slot is now taken
        assertFalse(modelManager.getStorage().addSlot(aliceClone.getTimeSlot())); // Fails to add

        // 3. Delete ALICE
        modelManager.deletePerson(ALICE);
        assertFalse(modelManager.hasPerson(ALICE));

        // 4. ALICE's slot should now be free
        // The storage.addSlot should now succeed
        assertTrue(modelManager.getStorage().addSlot(aliceClone.getTimeSlot()));
    }

    @Test
    public void setPerson_timeslotConflict_throwsTimeSlotConflictException() {
        // BENSON's timeslot is already taken (by BENSON)
        Person editedAlice = new PersonBuilder(ALICE)
                .withTimeSlot(BENSON.getTimeSlot().toString())
                .build();

        // Try to edit ALICE to have BENSON's timeslot
        assertThrows(TimeSlotConflictException.class, () -> modelManager.setPerson(ALICE, editedAlice));

        // Ensure ALICE was not changed
        assertEquals(ALICE, modelManager.getAddressBook().getPersonList().get(0));
    }

    @Test
    public void setPerson_validTimeslotChange_success() {
        String newSlotString = "2030-01-01 1000-1100";
        Person editedAlice = new PersonBuilder(ALICE).withTimeSlot(newSlotString).build();

        // 1. Edit ALICE to have a new, free timeslot
        modelManager.setPerson(ALICE, editedAlice);
        //assertEquals(editedAlice, modelManager.getAddressBook().getPersonList().get(0));

        // 2. Check that ALICE's *old* slot is now free
        // We can do this by adding a new person with her old slot
        Person personWithOldSlot = new PersonBuilder().withTimeSlot(ALICE.getTimeSlot().toString()).build();
        assertTrue(modelManager.getStorage().addSlot(personWithOldSlot.getTimeSlot()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void addPerson_duplicatePhone_throwsDuplicatePhoneException() {
        // Create BOB but give him ALICE's phone number
        Person conflictingPerson = new PersonBuilder(BOB)
                .withPhone(ALICE.getPhone().value) // ALICE's phone
                .build();
        String expectedError = "This phone number already exists in the address book, assigned to: "
                + ALICE.getName();
        // Use the 2-argument assertThrows (since DuplicatePhoneException has no message)
        assertThrows(DuplicatePhoneException.class, expectedError, () -> modelManager.addPerson(conflictingPerson));
    }

    @Test
    public void setPerson_duplicatePhone_throwsDuplicatePhoneException() {
        // modelManager already contains ALICE (94351253) and BENSON (98765432)

        // Try to edit BENSON to have ALICE's phone number
        Person editedBenson = new PersonBuilder(BENSON)
                .withPhone(ALICE.getPhone().value) // ALICE's phone
                .build();
        String expectedError = "This phone number already exists in the address book, assigned to: "
                + ALICE.getName();
        assertThrows(DuplicatePhoneException.class, expectedError, () -> modelManager.setPerson(BENSON, editedBenson));
    }

    @Test
    public void setPerson_noPhoneChange_allowsEdit() {
        // Edit BENSON's name but keep his phone number
        Person editedBenson = new PersonBuilder(BENSON)
                .withName("Benson V2")
                // Phone number remains BENSON's original phone
                .build();

        // This should succeed without throwing DuplicatePhoneException
        modelManager.setPerson(BENSON, editedBenson);

        // Verify the change was made (optional but good)
        Person personInList = modelManager.getAddressBook().getPersonList().get(1); // Assuming BENSON is at index 1
        assertEquals("Benson V2", personInList.getName().toString());
        assertEquals(BENSON.getPhone(), personInList.getPhone());
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        Storage storage = new StorageManager(
                new JsonAddressBookStorage(Paths.get("data", "addressbook.json")),
                new JsonUserPrefsStorage(Paths.get("data", "userprefs.json"))
        );

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs, storage);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs, storage);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs, storage)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs, storage)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs, storage)));
    }

}
