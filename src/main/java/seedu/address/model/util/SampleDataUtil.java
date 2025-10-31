package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.TimeSlot;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[] {
                // 1. Past, non-recurring (for clearpast delete)
                new Person(new Name("Charlie Goh"), new Phone("93456789"), new Email("charlie.g@email.com"),
                        new Address("3 Jurong East"), new TimeSlot("2025-10-22 1500-1700"),
                        getTagSet("Sec4Physics")),

                // 2. Past, recurring (for clearpast update)
                new Person(new Name("Diana Heng"), new Phone("94567890"), new Email("diana.h@email.com"),
                        new Address("4 Bishan Street"), new TimeSlot("2025-10-23 1600-1800"),
                        getTagSet("Sec3Math", "recurring")),

                // 3. Past-Today, recurring (for clearpast conflict)
                // Assumes "now" is 2025-10-30 15:30. This slot is past.
                new Person(new Name("Ethan Yeo"), new Phone("95678901"), new Email("ethan.y@email.com"),
                        new Address("5 Serangoon Way"), new TimeSlot("2025-10-30 1000-1200"),
                        getTagSet("JC2Physics", "recurring")),

                // 4. Future (causes conflict for Ethan's update)
                new Person(new Name("Ben Lim"), new Phone("92345678"), new Email("ben.l@email.com"),
                        new Address("2 Clementi Ave"), new TimeSlot("2025-11-06 1000-1200"),
                        getTagSet("JC1Chem", "NeedsHelp")),

                // 5. Future
                new Person(new Name("Alice Tan"), new Phone("91234567"), new Email("alice.t@email.com"),
                        new Address("1 Orchard Road"), new TimeSlot("2025-11-05 1400-1600"),
                        getTagSet("Sec3Math")),

                // 6. Future (no tags)
                new Person(new Name("George Png"), new Phone("97890123"), new Email("george.p@email.com"),
                        new Address("7 Pasir Ris Drive"), new TimeSlot("2025-11-10 0900-1100"),
                        getTagSet()),

                // 7. Future
                new Person(new Name("Fiona Wee"), new Phone("96789012"), new Email("fiona.w@email.com"),
                        new Address("6 Tampines Link"), new TimeSlot("2025-11-12 1100-1300"),
                        getTagSet("Sec3Math"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
