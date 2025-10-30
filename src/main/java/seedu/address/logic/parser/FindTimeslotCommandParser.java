package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;

import seedu.address.logic.commands.FindTimeslotCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.TimeslotStartTimeContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindTimeslotCommand object.
 */
public class FindTimeslotCommandParser implements Parser<FindTimeslotCommand> {

    public static final String MESSAGE_INVALID_KEYWORD = "Invalid keyword: '%1$s'.\n"
            + "Keywords must be a valid date (YYYY-MM-DD) or a valid time (HHMM).";

    /**
     * Parses the given {@code String} of arguments in the context of the FindTimeslotCommand
     * and returns a FindTimeslotCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindTimeslotCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindTimeslotCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");
        for (String keyword : nameKeywords) {
            if (!isValidKeyword(keyword)) {
                throw new ParseException(String.format(MESSAGE_INVALID_KEYWORD, keyword));
            }
        }
        return new FindTimeslotCommand(new TimeslotStartTimeContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
    }

    /**
     * Checks if a keyword is either a valid date (YYYY-MM-DD) or a valid time (HHMM).
     * It does this by trying to parse them using ParserUtil.
     */
    private boolean isValidKeyword(String keyword) {
        // Try to parse as date
        try {
            ParserUtil.parseDate(keyword);
            return true; // It's a valid date
        } catch (ParseException eDate) {
            // Not a date, now try to parse as time
        }

        // Try to parse as time
        try {
            ParserUtil.parseTime(keyword);
            return true; // It's a valid time
        } catch (ParseException eTime) {
            //invalid input
        }
        return false; // Invalid keyword
    }
}
