package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class ClearPastCommandTest {

    private Model model;
    private Model expectedModel;
    private Storage storage;

    // Define a fixed "now" for predictable tests
    // Note: This must be in the future relative to typical persons' past slots
    private final LocalDateTime now = LocalDateTime.now();

    // Helper to build a person with a specific past timeslot
    private Person buildPastPerson(String name) {
        return new PersonBuilder()
                .withName(name)
                .withPhone("11111111")
                .withTimeSlot("2020-01-01 1000-1100") // Definitely in the past
                .build();
    }

    // Helper to build a recurring person with a past timeslot
    private Person buildRecurringPastPerson(String name) {
        return new PersonBuilder()
                .withName(name)
                .withPhone("22222222")
                .withTimeSlot("2020-01-05 1200-1300") // Definitely in the past
                .withTags(ClearPastCommand.RECURRING_TAG_NAME)
                .build();
    }

    // Helper to build a person with a future timeslot
    private Person buildFuturePerson(String name) {
        String futureTime = now.plusDays(10).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return new PersonBuilder()
                .withName(name)
                .withPhone("33333333")
                .withTimeSlot(futureTime + " 1000-1100")
                .build();
    }

    @BeforeEach
    public void setUp() {
        // We need a real storage manager for conflict logic
        storage = new StorageManager(
                new JsonAddressBookStorage(Paths.get("data", "dummy-cleartest.json")),
                new JsonUserPrefsStorage(Paths.get("data", "dummyprefs-cleartest.json"))
        );
        // Start with a fresh model for each test
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs(), storage);
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs(), storage);
    }

    @Test
    public void execute_emptyAddressBook_success() {
        model = new ModelManager(); // Use a completely empty model
        expectedModel = new ModelManager();

        String expectedMessage = ClearPastCommand.MESSAGE_NO_CHANGES;
        assertCommandSuccess(new ClearPastCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_onlyFutureContacts_noChange() {
        model = new ModelManager(); // Use a completely empty model
        Person futurePerson = buildFuturePerson("Future Bob");
        model.addPerson(futurePerson);
        // Manually add slot to storage for this test model
        model.getStorage().addSlot(futurePerson.getTimeSlot());

        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs(), storage);

        String expectedMessage = ClearPastCommand.MESSAGE_NO_CHANGES;
        assertCommandSuccess(new ClearPastCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_pastNonRecurring_deletesContact() {
        model = new ModelManager();
        Person pastPerson = buildPastPerson("Past Alice");
        model.addPerson(pastPerson);
        model.getStorage().addSlot(pastPerson.getTimeSlot());

        // Expected model is empty
        expectedModel = new ModelManager();

        String expectedMessage = getExpectedClearPastMessage(
                List.of("Past Alice"),
                Collections.emptyList(),
                Collections.emptyList()
        );
        assertCommandSuccess(new ClearPastCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_pastRecurring_updatesContact() {
        model = new ModelManager();
        Person pastRecurring = buildRecurringPastPerson("Recurring Carl");
        model.addPerson(pastRecurring);
        model.getStorage().addSlot(pastRecurring.getTimeSlot());

        // Manually calculate the expected updated person
        Person expectedUpdatedPerson = new Person(
                pastRecurring.getName(),
                pastRecurring.getPhone(),
                pastRecurring.getEmail(),
                pastRecurring.getAddress(),
                pastRecurring.getTimeSlot().getNextOccurrence(now), // The new, future timeslot
                pastRecurring.getTags()
        );

        expectedModel = new ModelManager();
        expectedModel.addPerson(expectedUpdatedPerson);
        expectedModel.getStorage().addSlot(expectedUpdatedPerson.getTimeSlot());

        String expectedMessage = getExpectedClearPastMessage(
                Collections.emptyList(),
                List.of("Recurring Carl"),
                Collections.emptyList()
        );
        assertCommandSuccess(new ClearPastCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_mixedContacts_success() {
        model = new ModelManager();
        Person pastPerson = buildPastPerson("Past Alice");
        Person pastRecurring = buildRecurringPastPerson("Recurring Carl");
        Person futurePerson = buildFuturePerson("Future Bob");

        model.addPerson(pastPerson);
        model.getStorage().addSlot(pastPerson.getTimeSlot());
        model.addPerson(pastRecurring);
        model.getStorage().addSlot(pastRecurring.getTimeSlot());
        model.addPerson(futurePerson);
        model.getStorage().addSlot(futurePerson.getTimeSlot());

        // Calculate expected state
        Person expectedUpdatedPerson = new Person(
                pastRecurring.getName(), pastRecurring.getPhone(), pastRecurring.getEmail(),
                pastRecurring.getAddress(), pastRecurring.getTimeSlot().getNextOccurrence(now),
                pastRecurring.getTags()
        );

        expectedModel = new ModelManager();
        expectedModel.addPerson(expectedUpdatedPerson);
        expectedModel.getStorage().addSlot(expectedUpdatedPerson.getTimeSlot());
        expectedModel.addPerson(futurePerson);
        expectedModel.getStorage().addSlot(futurePerson.getTimeSlot());

        String expectedMessage = getExpectedClearPastMessage(
                List.of("Past Alice"),
                List.of("Recurring Carl"),
                Collections.emptyList()
        );
        assertCommandSuccess(new ClearPastCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_recurringConflict_reportsConflict() {
        model = new ModelManager();
        Person pastRecurring = buildRecurringPastPerson("Recurring Carl");

        // Create a conflicting person whose slot is *exactly* the next
        // occurrence of the recurring person
        Person conflictingPerson = new PersonBuilder()
                .withName("Blocker Bob")
                .withPhone("44444444")
                .withTimeSlot(pastRecurring.getTimeSlot().getNextOccurrence(now).toString())
                .build();

        model.addPerson(pastRecurring);
        model.getStorage().addSlot(pastRecurring.getTimeSlot());
        model.addPerson(conflictingPerson);
        model.getStorage().addSlot(conflictingPerson.getTimeSlot());

        // Expected model: The conflicting person is untouched, and the
        // recurring person is *also* untouched (it failed to update)
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs(), storage);

        String conflictError = Messages.MESSAGE_TIMESLOT_CONFLICT
                + " " + conflictingPerson.getName()
                + " [" + conflictingPerson.getTimeSlot() + "]";

        // 2. This is the new format from ClearPastCommand's helper method
        String conflictDetails = "Recurring Carl (Conflict: " + conflictError + ")";

        String expectedMessage = getExpectedClearPastMessage(
                Collections.emptyList(), // No deleted
                Collections.emptyList(), // No updated
                List.of(conflictDetails) // One conflict
        );

        assertCommandSuccess(new ClearPastCommand(), model, expectedMessage, expectedModel);
    }

    /**
     * Helper method to construct the expected success/result message for ClearPastCommand.
     * This mimics the logic used in ClearPastCommand itself.
     */
    private String getExpectedClearPastMessage(List<String> deletedNames, List<String> updatedNames,
                                               List<String> conflictDetails) {
        if (deletedNames.isEmpty() && updatedNames.isEmpty() && conflictDetails.isEmpty()) {
            return ClearPastCommand.MESSAGE_NO_CHANGES;
        }

        StringBuilder result = new StringBuilder();
        if (!deletedNames.isEmpty()) {
            result.append(String.format(ClearPastCommand.MESSAGE_DELETED,
                    deletedNames.size(), String.join(", ", deletedNames)));
        }
        if (!updatedNames.isEmpty()) {
            result.append(String.format(ClearPastCommand.MESSAGE_UPDATED,
                    updatedNames.size(), String.join(", ", updatedNames)));
        }
        if (!conflictDetails.isEmpty()) {
            result.append(String.format(ClearPastCommand.MESSAGE_CONFLICTS,
                    conflictDetails.size(), String.join(", ", conflictDetails)));
        }
        return result.toString();
    }
}
