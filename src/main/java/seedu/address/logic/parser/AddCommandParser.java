package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIMESLOT;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.TimeSlot;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_TIMESLOT, PREFIX_TAG);

        checkRequiredPrefixes(argMultimap, AddCommand.MESSAGE_USAGE,
                PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TIMESLOT);
        verifyNoEmbeddedSlashes(argMultimap, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TIMESLOT, PREFIX_TAG);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_ADDRESS, PREFIX_TIMESLOT);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        TimeSlot timeSlot = ParserUtil.parseTimeSlot(argMultimap.getValue(PREFIX_TIMESLOT).get());
        LocalDateTime startTime = LocalDateTime.of(timeSlot.getDate(), timeSlot.getStartTime());
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new ParseException("Cannot add a timeslot that starts in the past.");
        }
        Person person = new Person(name, phone, email, address, timeSlot, tagList);
        return new AddCommand(person);
    }
    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Checks if all specified prefixes are present in the ArgumentMultimap.
     * Throws a ParseException with a specific "Missing prefix" message if any are missing.
     */
    private void checkRequiredPrefixes(ArgumentMultimap argMultimap, String usageMessage, Prefix... prefixes)
            throws ParseException {
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usageMessage));
        }
        if (!arePrefixesPresent(argMultimap, prefixes)) {
            // Check for preamble error first
            // Find the first missing prefix and report it
            Prefix missingPrefix = findFirstMissingPrefix(argMultimap, prefixes);
            throw new ParseException("Missing prefix: " + missingPrefix.getPrefix() + "\n" + usageMessage);
        }
    }

    /**
     * Finds the first prefix in the list that is not present in the ArgumentMultimap.
     */
    private Prefix findFirstMissingPrefix(ArgumentMultimap argMultimap, Prefix... prefixes) {
        for (Prefix prefix : prefixes) {
            if (argMultimap.getValue(prefix).isEmpty()) {
                return prefix;
            }
        }
        return null; // Should not be reached if arePrefixesPresent is false
    }

    /**
     * Verifies that the values for the given prefixes do not contain a '/' character.
     * @param argMultimap The multimap to check.
     * @param prefixesToCheck The prefixes whose values should be checked (e.g., all *except* n/ and t/).
     * @throws ParseException if an embedded slash is found.
     */
    private void verifyNoEmbeddedSlashes(ArgumentMultimap argMultimap, Prefix... prefixesToCheck)
            throws ParseException {
        for (Prefix prefix : prefixesToCheck) {
            Optional<String> value = argMultimap.getValue(prefix);
            if (value.isPresent() && value.get().contains("/")) {
                throw new ParseException(
                        String.format("Error in %s field: "
                                        + "This may be caused by an unknown prefix (e.g., 'r/').",
                                prefix.getPrefix()));
            }
        }
    }
}
