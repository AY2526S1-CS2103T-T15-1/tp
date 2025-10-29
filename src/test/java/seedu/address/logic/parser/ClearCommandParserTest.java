package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ClearCommand;

public class ClearCommandParserTest {

    private ClearCommandParser parser = new ClearCommandParser();

    @Test
    public void parse_noArguments_success() {
        // --- Test with empty string ---
        assertParseSuccess(parser, "", new ClearCommand());

        // --- Test with whitespace ---
        assertParseSuccess(parser, "    ", new ClearCommand());
    }

    @Test
    public void parse_withArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE);

        // --- Test with numbers ---
        assertParseFailure(parser, " 1 2 3", expectedMessage);

        // --- Test with text (the "clear past" mistake) ---
        assertParseFailure(parser, " past", expectedMessage);

        // --- Test with random text ---
        assertParseFailure(parser, " some random arguments", expectedMessage);

        // --- Test with prefixes ---
        assertParseFailure(parser, " n/Alice", expectedMessage);
    }
}
