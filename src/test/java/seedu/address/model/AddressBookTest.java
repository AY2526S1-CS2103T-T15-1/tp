package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.TimeSlot;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), addressBook.getPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = Arrays.asList(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{persons=" + addressBook.getPersonList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    @Test
    public void getConflictingPerson_withConflict_returnsPerson() {
        addressBook.addPerson(ALICE); // ALICE has timeslot "2025-10-12 1600-1800"
        TimeSlot conflictingSlot = new TimeSlot("2025-10-12 1500-1700"); // Overlaps
        assertTrue(addressBook.getConflictingPerson(conflictingSlot).isPresent());
        assertEquals(ALICE, addressBook.getConflictingPerson(conflictingSlot).get());
    }

    @Test
    public void getConflictingPerson_noConflict_returnsEmpty() {
        addressBook.addPerson(ALICE);
        TimeSlot nonConflictingSlot = new TimeSlot("2099-01-01 1200-1300");
        assertFalse(addressBook.getConflictingPerson(nonConflictingSlot).isPresent());
    }

    @Test
    public void getConflictingPerson_personHasNullTimeslot_noConflict() {
        // PersonBuilder with no timeslot
        Person noSlotPerson = new PersonBuilder().withName("No Slot").build();
        addressBook.addPerson(noSlotPerson);
        TimeSlot someSlot = new TimeSlot("2099-01-01 1200-1300");

        // Ensures the null check in getConflictingPerson works
        assertFalse(addressBook.getConflictingPerson(someSlot).isPresent());
    }

    @Test
    public void getConflictingPerson_nullTimeslot_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.getConflictingPerson(null));
    }

    @Test
    public void getConflictingPerson_ignoreSelf_returnsEmpty() {
        addressBook.addPerson(ALICE);
        TimeSlot alicesSlot = ALICE.getTimeSlot();

        // Check for conflict, but ignore ALICE. Should find no conflict.
        assertFalse(addressBook.getConflictingPerson(alicesSlot, ALICE).isPresent());
    }

    @Test
    public void getConflictingPerson_conflictWithOther_returnsOtherPerson() {
        addressBook.addPerson(ALICE);
        addressBook.addPerson(BENSON); // BENSON has timeslot "2025-10-13 1000-1100"
        TimeSlot bensonSlot = BENSON.getTimeSlot();

        // Check for conflict with Benson's slot, while (hypothetically) editing ALICE
        assertTrue(addressBook.getConflictingPerson(bensonSlot, ALICE).isPresent());
        assertEquals(BENSON, addressBook.getConflictingPerson(bensonSlot, ALICE).get());
    }

    @Test
    public void getConflictingPerson_nullInputs_throwsNullPointerException() {
        TimeSlot someSlot = new TimeSlot("2099-01-01 1200-1300");
        assertThrows(NullPointerException.class, () -> addressBook.getConflictingPerson(null, ALICE));
        assertThrows(NullPointerException.class, () -> addressBook.getConflictingPerson(someSlot, null));
    }

    @Test
    public void addPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.addPerson(null));
    }

    @Test
    public void addPerson_duplicatePerson_throwsDuplicatePersonException() {
        addressBook.addPerson(ALICE);
        assertThrows(DuplicatePersonException.class, () -> addressBook.addPerson(ALICE));
    }

    @Test
    public void setPerson_nullTargetPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.setPerson(null, ALICE));
    }

    @Test
    public void setPerson_nullEditedPerson_throwsNullPointerException() {
        addressBook.addPerson(ALICE);
        assertThrows(NullPointerException.class, () -> addressBook.setPerson(ALICE, null));
    }

    @Test
    public void setPerson_targetPersonNotInList_throwsPersonNotFoundException() {
        assertThrows(PersonNotFoundException.class, () -> addressBook.setPerson(ALICE, ALICE));
    }

    @Test
    public void removePerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.removePerson(null));
    }

    @Test
    public void removePerson_personDoesNotExist_throwsPersonNotFoundException() {
        assertThrows(PersonNotFoundException.class, () -> addressBook.removePerson(ALICE));
    }

    @Test
    public void removePerson_personExists_removesPerson() {
        addressBook.addPerson(ALICE);
        addressBook.removePerson(ALICE);
        AddressBook expectedAddressBook = new AddressBook();
        assertEquals(expectedAddressBook, addressBook);
    }

    @Test
    public void equals() {
        addressBook.addPerson(ALICE);

        // same object -> returns true
        assertTrue(addressBook.equals(addressBook));

        // same values -> returns true
        AddressBook addressBookCopy = new AddressBook();
        addressBookCopy.addPerson(ALICE);
        assertTrue(addressBook.equals(addressBookCopy));

        // different types -> returns false
        assertFalse(addressBook.equals(5));

        // null -> returns false
        assertFalse(addressBook.equals(null));

        // different person -> returns false
        AddressBook differentAddressBook = new AddressBook();
        differentAddressBook.addPerson(BENSON);
        assertFalse(addressBook.equals(differentAddressBook));
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }
    }

}
