package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import seedu.address.logic.Messages;
import seedu.address.model.person.exceptions.PastTimeSlotException;

/**
 * Represents a Person's lesson time slot in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidTimeSlot(String)}
 */
public class TimeSlot implements Comparable<TimeSlot> {

    private static final int MIN_DURATION_MINUTES = 30;
    public static final String MESSAGE_CONSTRAINTS =
            "TimeSlot should be in the format YYYY-MM-DD HHMM-HHMM, "
                    + "where start time is before end time and the duration is at least "
                    + MIN_DURATION_MINUTES + " minutes.\n"
                    + "Example: 2025-10-12 1600-1800";

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public final LocalDate date;
    public final LocalTime startTime;
    public final LocalTime endTime;
    public final String value;

    /**
     * Constructs a {@code TimeSlot}.
     *
     * @param timeSlotString A valid time slot string in the format "YYYY-MM-DD HHMM-HHMM".
     */
    public TimeSlot(String timeSlotString) {
        requireNonNull(timeSlotString);
        checkArgument(isValidTimeSlot(timeSlotString), MESSAGE_CONSTRAINTS);

        String[] parts = timeSlotString.trim().split(" ");
        this.date = LocalDate.parse(parts[0], DATE_FORMATTER);

        String[] times = parts[1].split("-");
        this.startTime = LocalTime.parse(times[0], TIME_FORMATTER);
        this.endTime = LocalTime.parse(times[1], TIME_FORMATTER);
        value = timeSlotString;
    }

    /**
     * Private constructor to create a new TimeSlot from its components.
     * Used by getNextOccurrence.
     */
    private TimeSlot(LocalDate date, LocalTime startTime, LocalTime endTime) {
        assert date != null : "Date cannot be null";
        assert startTime != null : "Start time cannot be null";
        assert endTime != null : "End time cannot be null";
        assert startTime.isBefore(endTime) : "Start time must be before end time";
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;

        // Format the 'value' string to be consistent
        this.value = date.format(DATE_FORMATTER) + " " + startTime.format(TIME_FORMATTER)
                + "-" + endTime.format(TIME_FORMATTER);

        // We can skip validation checks as we trust the internal method logic
    }

    /**
     * Check whether the timeslot is in the past
     * @return a boolean value
     */
    public boolean isPast() {
        if (this.date.isBefore(LocalDate.now())) {
            return true;
        } else if (this.date.isEqual(LocalDate.now())) {
            return this.startTime.isBefore(LocalTime.now());
        } else {
            return false;
        }
    }

    /**
     * Checks if this timeslot's end time is before the given time.
     */
    public boolean isPast(LocalDateTime now) {
        LocalDateTime endDateTime = LocalDateTime.of(this.date, this.endTime);
        return endDateTime.isBefore(now);
    }

    /**
     * Helper that throws an exception
     * @throws PastTimeSlotException
     */
    public void checkPast() throws PastTimeSlotException {
        if (isPast()) {
            throw new PastTimeSlotException(Messages.MESSAGE_PAST_TIMESLOT);
        }
    }

    /**
     * Returns true if a given string is a valid time slot.
     */
    public static boolean isValidTimeSlot(String test) {
        if (test == null) {
            return false;
        }

        String[] parts = test.trim().split(" ");
        if (parts.length != 2) {
            return false;
        }

        // Validate date
        try {
            LocalDate.parse(parts[0], DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return false;
        }

        // Validate times
        String[] times = parts[1].split("-");
        if (times.length != 2) {
            return false;
        }

        try {
            LocalTime start = LocalTime.parse(times[0], TIME_FORMATTER);
            LocalTime end = LocalTime.parse(times[1], TIME_FORMATTER);

            if (!start.isBefore(end)) {
                return false;
            }

            Duration duration = Duration.between(start, end);
            if (duration.toMinutes() < MIN_DURATION_MINUTES) {
                return false;
            }

        } catch (DateTimeParseException e) {
            return false;
        }

        return true;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Calculates the next recurring timeslot (weekly) after the given time.
     */
    public TimeSlot getNextOccurrence(LocalDateTime now) {
        LocalDateTime currentStartDateTime = LocalDateTime.of(this.date, this.startTime);
        Duration duration = Duration.between(this.startTime, this.endTime);

        LocalDateTime newStartDateTime = currentStartDateTime;

        // This loop fast-forwards the timeslot until it's in the future
        while (newStartDateTime.isBefore(now)) {
            newStartDateTime = newStartDateTime.plusWeeks(1);
        }

        LocalDate newDate = newStartDateTime.toLocalDate();
        LocalTime newStartTime = newStartDateTime.toLocalTime();
        LocalTime newEndTime = newStartDateTime.plus(duration).toLocalTime();

        return new TimeSlot(newDate, newStartTime, newEndTime);
    }

    /**
     * Checks for overlapping timeslots
     * @param other
     * @return
     */
    public boolean overlaps(TimeSlot other) {
        if (!this.date.equals(other.date)) {
            return false; // Different dates cannot overlap
        }
        return this.endTime.isAfter(other.startTime) && this.startTime.isBefore(other.endTime);
    }

    @Override
    public String toString() {
        return date.format(DATE_FORMATTER) + " " + startTime.format(TIME_FORMATTER)
                + "-" + endTime.format(TIME_FORMATTER);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TimeSlot)) {
            return false;
        }

        TimeSlot otherSlot = (TimeSlot) other;
        return date.equals(otherSlot.date)
                && startTime.equals(otherSlot.startTime)
                && endTime.equals(otherSlot.endTime);
    }

    @Override
    public int compareTo(TimeSlot other) {
        if (!this.date.equals(other.date)) {
            return this.date.compareTo(other.date);
        }
        if (!this.startTime.equals(other.startTime)) {
            return this.startTime.compareTo(other.startTime);
        }
        return this.endTime.compareTo(other.endTime);
    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + startTime.hashCode();
        result = 31 * result + endTime.hashCode();
        return result;
    }
}

