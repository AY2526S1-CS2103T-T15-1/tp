package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        Path tempAddressBookPath = Path.of("data", "addressbook.json");
        Path tempUserPrefsPath = Path.of("data", "userprefs.json");

        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(tempAddressBookPath);
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(tempUserPrefsPath);

        StorageManager storageManager = new StorageManager(addressBookStorage, userPrefsStorage);

        model = new ModelManager(new AddressBook(), new UserPrefs(), storageManager);
    }



    @Test
    public void execute_newPerson_success() {
        Path tempAddressBookPath = Path.of("data", "addressbook.json");
        Path tempUserPrefsPath = Path.of("data", "userprefs.json");

        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(tempAddressBookPath);
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(tempUserPrefsPath);

        StorageManager storageManager = new StorageManager(addressBookStorage, userPrefsStorage);

        Model expectedModel = new ModelManager(new AddressBook(), new UserPrefs(), storageManager);

        Person validPerson = new PersonBuilder().build();
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person validPerson = new PersonBuilder().build();
        model.addPerson(validPerson); // add to model first

        // Attempt to add the same person again
        assertCommandFailure(new AddCommand(validPerson), model,
                AddCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePhone_throwsCommandException() {
        // Add ALICE to the model first
        model.addPerson(ALICE);
        model.getStorage().addSlot(ALICE.getTimeSlot()); // Ensure storage knows

        // Create BOB but give him ALICE's phone number
        Person personWithDuplicatePhone = new PersonBuilder(BOB)
                .withPhone(ALICE.getPhone().value) // ALICE's phone
                .build();

        String expectedError = "This phone number already exists in the address book, assigned to: "
                + ALICE.getName();
        // Expect the command to fail with the specific user message
        assertCommandFailure(new AddCommand(personWithDuplicatePhone), model,
                expectedError);
    }
}

