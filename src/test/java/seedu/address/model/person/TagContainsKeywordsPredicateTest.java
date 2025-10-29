package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

/**
 * Unit tests for {@link TagContainsKeywordsPredicate}.
 */
public class TagContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        var firstPredicate = new TagContainsKeywordsPredicate(Collections.singletonList("friends"));
        var secondPredicate = new TagContainsKeywordsPredicate(Arrays.asList("friends", "owesMoney"));

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        var firstPredicateCopy = new TagContainsKeywordsPredicate(Collections.singletonList("friends"));
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_tagContainsKeywords_returnsTrue() {
        // Single keyword matches one tag
        var predicate = new TagContainsKeywordsPredicate(Collections.singletonList("friends"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friends").build()));

        // Multiple tags, one matches
        predicate = new TagContainsKeywordsPredicate(Collections.singletonList("owesMoney"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friends", "owesMoney").build()));

        // Multiple keywords, one matches
        predicate = new TagContainsKeywordsPredicate(Arrays.asList("friends", "colleagues"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friends").build()));

        // Mixed-case match
        predicate = new TagContainsKeywordsPredicate(Collections.singletonList("FrIeNdS"));
        assertTrue(predicate.test(new PersonBuilder().withTags("friends").build()));
    }

    @Test
    public void test_tagDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        var predicate = new TagContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PersonBuilder().withTags("friends").build()));

        // Non-matching keyword
        predicate = new TagContainsKeywordsPredicate(Collections.singletonList("colleague"));
        assertFalse(predicate.test(new PersonBuilder().withTags("friends").build()));

        // Person has no tags
        predicate = new TagContainsKeywordsPredicate(Collections.singletonList("friends"));
        assertFalse(predicate.test(new PersonBuilder().withTags().build()));
    }

    @Test
    public void toString_returnsCorrectString() {
        var predicate = new TagContainsKeywordsPredicate(Arrays.asList("friends", "owesMoney"));
        String expected = TagContainsKeywordsPredicate.class.getCanonicalName()
                + "{keywords=[friends, owesMoney]}";
        assertEquals(expected, predicate.toString());
    }
}
