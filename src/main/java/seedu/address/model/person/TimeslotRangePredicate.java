package seedu.address.model.person;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code TimeSlot} overlaps with the given time range.
 */
public class TimeslotRangePredicate implements Predicate<Person> {
    private final Optional<LocalDate> startDate;
    private final Optional<LocalDate> endDate;
    private final Optional<LocalTime> startTime;
    private final Optional<LocalTime> endTime;

    /**
     * Constructs a predicate with the given date and time boundaries.
     * All parameters are optional.
     */
    public TimeslotRangePredicate(Optional<LocalDate> startDate, Optional<LocalDate> endDate,
                                  Optional<LocalTime> startTime, Optional<LocalTime> endTime) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public boolean test(Person person) {
        if (person.getTimeSlot() == null) {
            return false;
        }
        return checkDateRange(person.getTimeSlot()) && checkTimeRange(person.getTimeSlot());
    }

    /**
     * Checks if the timeslot's date is within the specified date range.
     */
    private boolean checkDateRange(TimeSlot personSlot) {
        LocalDate personDate = personSlot.getDate();

        // Must be on or after startDate (if it exists)
        boolean afterStartDate = startDate.map(start -> !personDate.isBefore(start)).orElse(true);
        // Must be on or before endDate (if it exists)
        boolean beforeEndDate = endDate.map(end -> !personDate.isAfter(end)).orElse(true);

        return afterStartDate && beforeEndDate;
    }

    /**
     * Checks if the timeslot's time overlaps with the specified time range.
     */
    private boolean checkTimeRange(TimeSlot personSlot) {
        LocalTime personStart = personSlot.getStartTime();
        LocalTime personEnd = personSlot.getEndTime();

        // Person's start time must be on or after the filter's start time
        boolean afterStartTime = startTime.map(start -> !personStart.isBefore(start)).orElse(true);
        // Person's end time must be on or before the filter's end time
        boolean beforeEndTime = endTime.map(end -> !personEnd.isAfter(end)).orElse(true);

        return afterStartTime && beforeEndTime;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TimeslotRangePredicate)) {
            return false;
        }

        TimeslotRangePredicate otherPredicate = (TimeslotRangePredicate) other;
        return startDate.equals(otherPredicate.startDate)
                && endDate.equals(otherPredicate.endDate)
                && startTime.equals(otherPredicate.startTime)
                && endTime.equals(otherPredicate.endTime);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("startDate", startDate.map(LocalDate::toString).orElse("any"))
                .add("endDate", endDate.map(LocalDate::toString).orElse("any"))
                .add("startTime", startTime.map(LocalTime::toString).orElse("any"))
                .add("endTime", endTime.map(LocalTime::toString).orElse("any"))
                .toString();
    }

    public String getFilterDescription() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String filters = Stream.of(
                        startDate.map(d -> "starting from " + d.toString()),
                        startTime.map(t -> "starting from " + t.format(timeFormatter)),
                        endDate.map(d -> "ending by " + d.toString()),
                        endTime.map(t -> "ending by " + t.format(timeFormatter))
                )
                .filter(Optional::isPresent) // Filter out empty optionals
                .map(Optional::get) // Get the string value
                .collect(Collectors.joining(" and ")); // Join them with " and "

        if (filters.isEmpty()) {
            return "with no time filter applied.";
        }
        return "with timeslots " + filters;
    }
}
