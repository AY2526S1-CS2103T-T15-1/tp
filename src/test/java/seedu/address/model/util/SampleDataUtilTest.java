package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

public class SampleDataUtilTest {

    @Test
    public void getSamplePersons_validData_noExceptionThrown() {
        // This test's main purpose is to catch exceptions if your sample data
        // ever becomes invalid due to changes in validation rules (e.g., in
        // Phone, Email, or TimeSlot classes).
        assertDoesNotThrow(() -> SampleDataUtil.getSamplePersons());
    }

    @Test
    public void getSampleAddressBook_validData_correctSizeAndNoDuplicates() {
        // This test ensures that the sample address book is populated
        // and that the addPerson method didn't fail (e.g., due to duplicates).
        Person[] samplePersons = SampleDataUtil.getSamplePersons();
        ReadOnlyAddressBook sampleAb = SampleDataUtil.getSampleAddressBook();

        assertEquals(samplePersons.length, sampleAb.getPersonList().size());

        // Check if all persons were added correctly
        assertTrue(sampleAb.getPersonList().containsAll(Arrays.asList(samplePersons)));
    }

    @Test
    public void getTagSet_noStrings_emptySet() {
        Set<Tag> tagSet = SampleDataUtil.getTagSet();
        assertTrue(tagSet.isEmpty());
    }

    @Test
    public void getTagSet_multipleStrings_correctSet() {
        String[] tagStrings = {"Sec3Math", "recurring"};
        Set<Tag> tagSet = SampleDataUtil.getTagSet(tagStrings);

        Set<Tag> expectedSet = Arrays.stream(tagStrings)
                .map(Tag::new)
                .collect(Collectors.toSet());

        assertEquals(expectedSet, tagSet);
    }

    @Test
    public void getTagSet_duplicateStrings_singleTagInSet() {
        // Tests that duplicates are handled correctly by the Set
        String[] tagStrings = {"recurring", "recurring"};
        Set<Tag> tagSet = SampleDataUtil.getTagSet(tagStrings);

        assertEquals(1, tagSet.size());
        assertEquals(new Tag("recurring"), tagSet.iterator().next());
    }
}
