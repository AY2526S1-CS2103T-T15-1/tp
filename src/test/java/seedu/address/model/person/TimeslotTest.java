package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

public class TimeslotTest {

    @Test
    public void constructor_validInput_success() {
        TimeSlot slot = new TimeSlot("2025-10-12 0800-0900");
        assertEquals(LocalDate.of(2025, 10, 12), slot.getDate());
        assertEquals(LocalTime.of(8, 0), slot.getStartTime());
        assertEquals(LocalTime.of(9, 0), slot.getEndTime());
        assertEquals("2025-10-12 0800-0900", slot.toString());
    }

    @Test
    public void constructor_invalidFormat_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot("2025/10/12 0800-0900"));
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot("2025-10-12"));
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot("0800-0900"));
    }

    @Test
    public void constructor_invalidTimeOrder_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot("2025-10-12 1000-0900"));
    }

    @Test
    public void constructor_shortDuration_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot("2025-10-12 0800-0800"));
    }

    @Test
    public void isValidTimeSlot_validInputs_returnTrue() {
        assertTrue(TimeSlot.isValidTimeSlot("2025-10-12 0800-0900"));
        assertTrue(TimeSlot.isValidTimeSlot("2024-01-01 1300-1400"));
    }

    @Test
    public void isValidTimeSlot_invalidInputs_returnFalse() {
        assertFalse(TimeSlot.isValidTimeSlot(null));
        assertFalse(TimeSlot.isValidTimeSlot("")); // empty
        assertFalse(TimeSlot.isValidTimeSlot("not-a-date 0800-0900"));
        assertFalse(TimeSlot.isValidTimeSlot("2025-10-12 9999-1200"));
        assertFalse(TimeSlot.isValidTimeSlot("2025-10-12 0900-0900"));
        //assertFalse(TimeSlot.isValidTimeSlot("2025-10-12 0850-0900")); // less than 30 min, but allowed for sake of PE
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        TimeSlot slot1 = new TimeSlot("2025-10-12 0800-0900");
        TimeSlot slot2 = new TimeSlot("2025-10-12 0800-0900");
        assertEquals(slot1, slot2);
    }

    @Test
    public void equals_differentValues_returnsFalse() {
        TimeSlot slot1 = new TimeSlot("2025-10-12 0800-0900");
        TimeSlot slot2 = new TimeSlot("2025-10-12 0900-1000");
        assertNotEquals(slot1, slot2);
    }

    @Test
    public void toString_validSlot_matchesExpectedFormat() {
        TimeSlot slot = new TimeSlot("2025-10-12 0800-0900");
        assertEquals("2025-10-12 0800-0900", slot.toString());
    }

    @Test
    public void isPast_test() {
        // Mock 'now' to be a fixed point in time for predictable tests
        LocalDateTime now = LocalDateTime.of(2025, 10, 22, 16, 0); // 4:00 PM on Oct 22

        // Case 1: Timeslot is fully in the past (yesterday)
        TimeSlot pastSlot = new TimeSlot("2025-10-21 1400-1500");
        assertTrue(pastSlot.isPast(now));

        // Case 2: Timeslot is fully in the past (today)
        TimeSlot pastTodaySlot = new TimeSlot("2025-10-22 1000-1100");
        assertTrue(pastTodaySlot.isPast(now));

        // Case 3: Timeslot is in progress (end time is in future)
        TimeSlot inProgressSlot = new TimeSlot("2025-10-22 1500-1700");
        assertFalse(inProgressSlot.isPast(now));

        // Case 4: Timeslot is fully in the future (today)
        TimeSlot futureTodaySlot = new TimeSlot("2025-10-22 1800-1900");
        assertFalse(futureTodaySlot.isPast(now));

        // Case 5: Timeslot is fully in the future (tomorrow)
        TimeSlot futureSlot = new TimeSlot("2025-10-23 1000-1100");
        assertFalse(futureSlot.isPast(now));

        // Case 6: Timeslot ends exactly 'now'
        TimeSlot edgeSlot = new TimeSlot("2025-10-22 1500-1600");
        assertFalse(edgeSlot.isPast(now)); // .isBefore() is strict, so this is not "past"
    }

    @Test
    public void getNextOccurrence_test() {
        LocalDateTime now = LocalDateTime.of(2025, 10, 22, 16, 0); // 4:00 PM on Wed, Oct 22

        // Case 1: Past slot (yesterday, Tue Oct 21)
        TimeSlot pastSlot = new TimeSlot("2025-10-21 1000-1100");
        TimeSlot expectedNext = new TimeSlot("2025-10-28 1000-1100"); // Next Tuesday
        assertEquals(expectedNext, pastSlot.getNextOccurrence(now));

        // Case 2: Past slot (today, Wed Oct 22 @ 3PM)
        TimeSlot pastTodaySlot = new TimeSlot("2025-10-22 1500-1530");
        TimeSlot expectedNextToday = new TimeSlot("2025-10-29 1500-1530"); // Next Wednesday
        assertEquals(expectedNextToday, pastTodaySlot.getNextOccurrence(now));

        // Case 3: Future slot (today, Wed Oct 22 @ 5PM)
        TimeSlot futureTodaySlot = new TimeSlot("2025-10-22 1700-1800");
        // Slot is in the future, so the "next" occurrence is itself
        assertEquals(futureTodaySlot, futureTodaySlot.getNextOccurrence(now));

        // Case 4: Future slot (tomorrow, Thu Oct 23)
        TimeSlot futureSlot = new TimeSlot("2025-10-23 1000-1100");
        // Slot is in the future, so the "next" occurrence is itself
        assertEquals(futureSlot, futureSlot.getNextOccurrence(now));
    }

    @Test
    public void constructor_invalidLength_throwsIllegalArgumentException() {
        // Test what happens if the string is too long or has extra parts

        // Extra space
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot("2025-10-12 0800-0900 extra"));

        // Missing space
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot("2025-10-120800-0900"));

        // Invalid time format (e.g., extra dash)
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot("2025-10-12 0800-0900-1000"));
    }

    @Test
    public void isValidTimeSlot_invalidLength_returnFalse() {
        // Extra space
        assertFalse(TimeSlot.isValidTimeSlot("2025-10-12 0800-0900 extra"));

        // Missing space
        assertFalse(TimeSlot.isValidTimeSlot("2025-10-120800-0900"));

        // Invalid time format (e.g., extra dash)
        assertFalse(TimeSlot.isValidTimeSlot("2025-10-12 0800-0900-1000"));
    }

    @Test
    public void constructor_endTimeEqualsStartTime_throwsIllegalArgumentException() {
        // This is explicitly checked in isValidTimeSlot but not tested
        assertThrows(IllegalArgumentException.class, () -> new TimeSlot("2025-10-12 0900-0900"));
    }

    @Test
    public void overlaps_test() {
        TimeSlot slot = new TimeSlot("2025-10-20 1000-1200");

        // Case 1: Different date
        TimeSlot differentDate = new TimeSlot("2025-10-21 1000-1200");
        assertFalse(slot.overlaps(differentDate));

        // Case 2: Identical slot
        TimeSlot identical = new TimeSlot("2025-10-20 1000-1200");
        assertTrue(slot.overlaps(identical));

        // Case 3: Other slot is contained within
        TimeSlot inside = new TimeSlot("2025-10-20 1030-1130");
        assertTrue(slot.overlaps(inside));

        // Case 4: Other slot contains this slot
        TimeSlot contains = new TimeSlot("2025-10-20 0900-1300");
        assertTrue(slot.overlaps(contains));

        // Case 5: Overlaps start
        TimeSlot overlapsStart = new TimeSlot("2025-10-20 0900-1100");
        assertTrue(slot.overlaps(overlapsStart));

        // Case 6: Overlaps end
        TimeSlot overlapsEnd = new TimeSlot("2025-10-20 1100-1300");
        assertTrue(slot.overlaps(overlapsEnd));

        // Case 7: No overlap (just before)
        TimeSlot before = new TimeSlot("2025-10-20 0800-1000"); // Ends exactly at start
        assertFalse(slot.overlaps(before));

        // Case 8: No overlap (just after)
        TimeSlot after = new TimeSlot("2025-10-20 1200-1400"); // Starts exactly at end
        assertFalse(slot.overlaps(after));
    }

    @Test
    public void compareTo_test() {
        TimeSlot slot1 = new TimeSlot("2025-10-20 1000-1200");
        TimeSlot slot2 = new TimeSlot("2025-10-20 1000-1200");
        TimeSlot slotEarlierTime = new TimeSlot("2025-10-20 0900-1000");
        TimeSlot slotLaterTime = new TimeSlot("2025-10-20 1400-1500");
        TimeSlot slotEarlierDate = new TimeSlot("2025-10-19 1400-1500");
        TimeSlot slotLaterDate = new TimeSlot("2025-10-21 0900-1000");

        // Same
        assertEquals(0, slot1.compareTo(slot2));

        // Different date
        assertTrue(slot1.compareTo(slotEarlierDate) > 0);
        assertTrue(slot1.compareTo(slotLaterDate) < 0);

        // Same date, different time
        assertTrue(slot1.compareTo(slotEarlierTime) > 0);
        assertTrue(slot1.compareTo(slotLaterTime) < 0);
    }
}
