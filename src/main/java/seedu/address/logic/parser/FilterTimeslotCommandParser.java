package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.commands.FilterTimeslotCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.TimeslotRangePredicate;

/**
 * Parses input arguments and creates a new FilterTimeslotCommand object
 */
public class FilterTimeslotCommandParser implements Parser<FilterTimeslotCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FilterTimeslotCommand
     * and returns a FilterTimeslotCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */

    public static final String MESSAGE_INVALID_DATE_RANGE = "Start date must be before or on end date.";
    public static final String MESSAGE_INVALID_TIME_RANGE = "Start time must be before or on end time.";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmm");

    /**
     * Parses the overall command string with helper methods and returns a FilterTimeSlotCommand
     * @param args
     * @return
     * @throws ParseException
     */
    public FilterTimeslotCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_START_DATE, PREFIX_END_DATE,
                        PREFIX_START_TIME, PREFIX_END_TIME);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterTimeslotCommand.MESSAGE_USAGE));
        }

        if (Stream.of(PREFIX_START_DATE, PREFIX_END_DATE, PREFIX_START_TIME, PREFIX_END_TIME)
                .noneMatch(prefix -> argMultimap.getValue(prefix).isPresent())) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterTimeslotCommand.MESSAGE_USAGE));
        }

        try {
            // --- REFACTORED: Parsing logic moved to helper methods ---
            Optional<LocalDate> startDate = parseDate(argMultimap.getValue(PREFIX_START_DATE));
            Optional<LocalDate> endDate = parseDate(argMultimap.getValue(PREFIX_END_DATE));
            Optional<LocalTime> startTime = parseTime(argMultimap.getValue(PREFIX_START_TIME));
            Optional<LocalTime> endTime = parseTime(argMultimap.getValue(PREFIX_END_TIME));

            if (startDate.isPresent() && endDate.isPresent() && startDate.get().isAfter(endDate.get())) {
                throw new ParseException(MESSAGE_INVALID_DATE_RANGE);
            }

            if (startTime.isPresent() && endTime.isPresent() && startTime.get().isAfter(endTime.get())) {
                throw new ParseException(MESSAGE_INVALID_TIME_RANGE);
            }

            TimeslotRangePredicate predicate = new TimeslotRangePredicate(
                    startDate, endDate, startTime, endTime);

            return new FilterTimeslotCommand(predicate);

        } catch (ParseException pe) {
            // Catch exceptions from helper methods
            throw new ParseException(pe.getMessage(), pe);
        }
    }

    /**
     * Parses an Optional date string, handling "now"/"today" keywords.
     */
    private Optional<LocalDate> parseDate(Optional<String> dateStr) throws ParseException {
        if (dateStr.isEmpty()) {
            return Optional.empty();
        }
        String str = dateStr.get();
        if (str.equalsIgnoreCase("now") || str.equalsIgnoreCase("today")) {
            return Optional.of(LocalDate.now());
        }
        return Optional.of(ParserUtil.parseDate(str));
    }

    /**
     * Parses an Optional time string, handling "now" keyword.
     */
    private Optional<LocalTime> parseTime(Optional<String> timeStr) throws ParseException {
        if (timeStr.isEmpty()) {
            return Optional.empty();
        }
        String str = timeStr.get();
        if (str.equalsIgnoreCase("now")) {
            // Use a formatter to avoid ParseException if LocalTime.now() has nanoseconds
            String formattedNow = LocalTime.now().format(TIME_FORMATTER);
            return Optional.of(LocalTime.parse(formattedNow, TIME_FORMATTER));
        }
        return Optional.of(ParserUtil.parseTime(str));
    }
}
