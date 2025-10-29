package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Tag} matches any of the keywords given.
 */
public class TagContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public TagContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        // Returns true if any of the person's tags match any of the keywords (case-insensitive)
        return person.getTags().stream()
                .map(tag -> tag.tagName)
                .anyMatch(tagName ->
                        keywords.stream().anyMatch(keyword ->
                                StringUtil.containsWordIgnoreCase(tagName, keyword)));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof TagContainsKeywordsPredicate
                && keywords.equals(((TagContainsKeywordsPredicate) other).keywords));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }

    public List<String> getKeywords() {
        return keywords;
    }
}
