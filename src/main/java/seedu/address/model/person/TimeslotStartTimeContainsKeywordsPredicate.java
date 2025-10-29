package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code TimeSlot}'s date or start time matches
 * the specified keywords. If both date and time are provided, both must match.
 */
public class TimeslotStartTimeContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public TimeslotStartTimeContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        if (person.getTimeSlot() == null) {
            return false;
        }

        String dateString = person.getTimeSlot().getDate().toString(); // e.g. "2025-10-12"
        String startTimeString = person.getTimeSlot().getStartTime().toString(); // e.g. "08:00"
        String normalizedStart = startTimeString.replace(":", ""); // e.g. "0800"

        // Determine which keywords look like dates or times
        boolean hasDateKeyword = keywords.stream().anyMatch(k -> k.contains("-")); // crude but effective check
        boolean hasTimeKeyword = keywords.stream().anyMatch(k -> !k.contains("-"));

        boolean dateMatch = keywords.stream().anyMatch(k -> dateString.equalsIgnoreCase(k));
        boolean timeMatch = keywords.stream()
                .map(k -> k.replace(":", ""))
                .anyMatch(k -> normalizedStart.equalsIgnoreCase(k));

        // Logic:
        // If both a date and a time keyword are present → require both to match
        // Otherwise → match if either matches
        if (hasDateKeyword && hasTimeKeyword) {
            return dateMatch && timeMatch;
        } else {
            return dateMatch || timeMatch;
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof TimeslotStartTimeContainsKeywordsPredicate
                && keywords.equals(((TimeslotStartTimeContainsKeywordsPredicate) other).keywords));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }

    public List<String> getKeywords() {
        return keywords;
    }
}
