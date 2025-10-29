package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.DuplicatePhoneException;
import seedu.address.model.person.exceptions.TimeSlotConflictException;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final SortedList<Person> sortedPersons;
    private final Storage storage;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs, Storage storage) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        sortedPersons = new SortedList<>(filteredPersons);
        this.storage = storage;
        if (storage instanceof StorageManager) {
            storage.loadExistingSlots(addressBook);
        }
    }

    /**
     * constrctor for backwards compatability
     * @param addressBook
     * @param userPrefs
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        this(addressBook, userPrefs, new StorageManager(
                new JsonAddressBookStorage(Path.of("data", "addressbook.json")),
                new JsonUserPrefsStorage(Path.of("data", "userprefs.json"))
        ));
    }

    /**
     * Empty constrctor for modelManager
     */
    public ModelManager() {
        this(new AddressBook(), new UserPrefs(), new StorageManager(
            new JsonAddressBookStorage(Path.of("data", "addressbook.json")),
            new JsonUserPrefsStorage(Path.of("data", "userprefs.json"))){
        });
    }

    @Override
    public Storage getStorage() {
        return storage;
    }

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
        storage.loadExistingSlots(this.addressBook);
        logger.info("Address book reset. Storage timeslots re-synced with address book.");
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        storage.removeSlot(target.getTimeSlot());
        addressBook.removePerson(target);
        logger.fine("Deleted person: " + target.getName());
    }

    @Override
    public void addPerson(Person person) {
        if (hasPerson(person)) {
            throw new DuplicatePersonException();
        }

        Optional<Person> phoneConflict = findDuplicatePhone(person, null); // Changed method name
        if (phoneConflict.isPresent()) {
            throw new DuplicatePhoneException(phoneConflict.get()); // Pass person to exception
        }

        Optional<Person> conflict = addressBook.getConflictingPerson(person.getTimeSlot());
        if (conflict.isPresent()) {
            throw new TimeSlotConflictException(conflict.get());
        }

        storage.addSlot(person.getTimeSlot());
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        logger.fine("Added person: " + person.getName());
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        if (!target.isSamePerson(editedPerson) && hasPerson(editedPerson)) {
            throw new DuplicatePersonException();
        }

        // Check if timeslot is being changed
        if (!target.getTimeSlot().equals(editedPerson.getTimeSlot())) {
            // Timeslot has changed. Must check for conflicts.
            Optional<Person> conflict = addressBook.getConflictingPerson(editedPerson.getTimeSlot(), target);
            if (conflict.isPresent()) {
                throw new TimeSlotConflictException(conflict.get());
            }
            storage.removeSlot(target.getTimeSlot());
            storage.addSlot(editedPerson.getTimeSlot());
        }

        if (!target.getPhone().equals(editedPerson.getPhone())) {
            Optional<Person> phoneConflict = findDuplicatePhone(editedPerson, target); // Changed method name
            if (phoneConflict.isPresent()) {
                throw new DuplicatePhoneException(phoneConflict.get()); // Pass person to exception
            }
        }

        // If we are here, either timeslot didn't change, or it did and it was successful.
        addressBook.setPerson(target, editedPerson);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return sortedPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager)) {
            return false;
        }

        ModelManager otherModelManager = (ModelManager) other;
        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && sortedPersons.equals(otherModelManager.sortedPersons);
    }

    @Override
    public Optional<Person> getConflictingPerson(seedu.address.model.person.TimeSlot timeSlot) {
        return addressBook.getConflictingPerson(timeSlot);
    }

    @Override
    public Optional<Person> getConflictingPerson(
            seedu.address.model.person.TimeSlot timeSlot, Person personToIgnore) {
        return addressBook.getConflictingPerson(timeSlot, personToIgnore);
    }

    /**
     * Finds if the given person's phone number conflicts with any existing person
     * in the address book, optionally ignoring one person.
     *
     * @param personToCheck The person whose phone number to check.
     * @param personToIgnore The person to ignore during the check (can be null for add).
     * @return An Optional containing the conflicting person if found, otherwise empty.
     */
    private Optional<Person> findDuplicatePhone(Person personToCheck, Person personToIgnore) {
        requireNonNull(personToCheck);
        for (Person existingPerson : addressBook.getPersonList()) {
            // Skip the person being ignored (if any)
            if (personToIgnore != null && existingPerson.isSamePerson(personToIgnore)) {
                continue;
            }
            // Check if phone numbers are the same
            if (existingPerson.getPhone().equals(personToCheck.getPhone())) {
                return Optional.of(existingPerson); // Found a duplicate, return the person
            }
        }
        return Optional.empty(); // No duplicates found
    }

}
