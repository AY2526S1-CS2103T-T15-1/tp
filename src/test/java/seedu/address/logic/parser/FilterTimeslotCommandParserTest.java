package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterTimeslotCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.TimeslotRangePredicate;

public class FilterTimeslotCommandParserTest {

    private FilterTimeslotCommandParser parser = new FilterTimeslotCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterTimeslotCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noPrefixes_throwsParseException() {
        assertParseFailure(parser, "2025-10-10 0900",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterTimeslotCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_preamblePresent_throwsParseException() {
        assertParseFailure(parser, "preamble " + PREFIX_START_TIME + "0900",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterTimeslotCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidValues_throwsParseException() {
        // Invalid date
        assertParseFailure(parser, " " + PREFIX_START_DATE + "10-10-2025",
                "Date should be in YYYY-MM-DD format.");

        // Invalid time
        assertParseFailure(parser, " " + PREFIX_START_TIME + "9:00",
                "Time should be in HHMM format.");

        // Invalid date range
        assertParseFailure(parser, " " + PREFIX_START_DATE + "2025-10-11 " + PREFIX_END_DATE + "2025-10-10",
                "Start date must be before or on end date.");

        // Invalid time range
        assertParseFailure(parser, " " + PREFIX_START_TIME + "1200 " + PREFIX_END_TIME + "1000",
                "Start time must be before or on end time.");
    }

    @Test
    public void parse_validArgs_returnsFilterTimeslotCommand() {
        // One prefix (st/)
        TimeslotRangePredicate predicateSt = new TimeslotRangePredicate(
                Optional.empty(), Optional.empty(),
                Optional.of(LocalTime.of(9, 0)), Optional.empty());
        FilterTimeslotCommand expectedCommandSt = new FilterTimeslotCommand(predicateSt);
        assertParseSuccess(parser, " " + PREFIX_START_TIME + "0900", expectedCommandSt);

        // One prefix (sd/)
        TimeslotRangePredicate predicateSd = new TimeslotRangePredicate(
                Optional.of(LocalDate.of(2025, 10, 10)), Optional.empty(),
                Optional.empty(), Optional.empty());
        FilterTimeslotCommand expectedCommandSd = new FilterTimeslotCommand(predicateSd);
        assertParseSuccess(parser, " " + PREFIX_START_DATE + "2025-10-10", expectedCommandSd);

        // Two prefixes (sd/ and et/)
        TimeslotRangePredicate predicateSdEt = new TimeslotRangePredicate(
                Optional.of(LocalDate.of(2025, 10, 10)), Optional.empty(),
                Optional.empty(), Optional.of(LocalTime.of(17, 0)));
        FilterTimeslotCommand expectedCommandSdEt = new FilterTimeslotCommand(predicateSdEt);
        assertParseSuccess(parser, " " + PREFIX_START_DATE + "2025-10-10 " + PREFIX_END_TIME + "1700",
                expectedCommandSdEt);

        // All four prefixes
        TimeslotRangePredicate predicateAll = new TimeslotRangePredicate(
                Optional.of(LocalDate.of(2025, 10, 10)), Optional.of(LocalDate.of(2025, 10, 11)),
                Optional.of(LocalTime.of(9, 0)), Optional.of(LocalTime.of(17, 0)));
        FilterTimeslotCommand expectedCommandAll = new FilterTimeslotCommand(predicateAll);
        assertParseSuccess(parser,
                " " + PREFIX_START_DATE + "2025-10-10 " + PREFIX_END_DATE + "2025-10-11"
                        + " " + PREFIX_START_TIME + "0900 " + PREFIX_END_TIME + "1700",
                expectedCommandAll);

        // Args with extra whitespace
        assertParseSuccess(parser,
                " " + PREFIX_START_DATE + "  2025-10-10   " + PREFIX_END_DATE + " 2025-10-11"
                        + " " + PREFIX_START_TIME + " 0900 " + PREFIX_END_TIME + "  1700  ",
                expectedCommandAll);
    }
}