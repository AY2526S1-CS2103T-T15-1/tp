package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindTimeslotCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.TimeslotStartTimeContainsKeywordsPredicate;

public class FindTimeslotCommandParserTest {

    private final FindTimeslotCommandParser parser = new FindTimeslotCommandParser();

    @Test
    public void parsevalidArgsreturnsFindTimeslotCommandsingleKeyword() throws Exception {
        String input = "2025-10-12";
        FindTimeslotCommand command = parser.parse(input);

        TimeslotStartTimeContainsKeywordsPredicate expectedPredicate =
                new TimeslotStartTimeContainsKeywordsPredicate(Arrays.asList("2025-10-12"));

        assertEquals(expectedPredicate, command.getPredicate());
    }

    @Test
    public void parsevalidArgsreturnsFindTimeslotCommandmultipleKeywords() throws Exception {
        String input = "2025-10-12 0900 1600";
        FindTimeslotCommand command = parser.parse(input);

        TimeslotStartTimeContainsKeywordsPredicate expectedPredicate =
                new TimeslotStartTimeContainsKeywordsPredicate(Arrays.asList("2025-10-12", "0900", "1600"));

        assertEquals(expectedPredicate, command.getPredicate());
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        String input = "   ";
        ParseException exception = assertThrows(ParseException.class, () -> parser.parse(input));
        assertEquals(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindTimeslotCommand.MESSAGE_USAGE),
                exception.getMessage()
        );
    }
}

