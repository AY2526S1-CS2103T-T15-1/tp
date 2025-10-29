package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ExitCommand;

public class ExitCommandParserTest {
    private ExitCommandParser parser = new ExitCommandParser();

    @Test
    public void parse_noArguments_success() {
        assertParseSuccess(parser, "", new ExitCommand());
        assertParseSuccess(parser, "    ", new ExitCommand());
    }

    @Test
    public void parse_withArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExitCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " 123", expectedMessage);
    }
}
