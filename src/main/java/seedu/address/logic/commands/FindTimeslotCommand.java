package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.text.MessageFormat;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.TimeslotStartTimeContainsKeywordsPredicate;

/**
 * Finds and lists all persons in address book whose timeslot start time matches any of the argument keywords.
 * Keyword matching is case insensitive
 */
public class FindTimeslotCommand extends Command {

    public static final String COMMAND_WORD = "findtimeslot";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose timeslot "
            + "matches any of the specified dates or start times (case-insensitive). "
            + "You can search using either date (YYYY-MM-DD) or time (HHmm).\n"
            + "Parameters: DATE_OR_START_TIME [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " 2025-10-12 0900 1600";

    private final TimeslotStartTimeContainsKeywordsPredicate predicate;

    public FindTimeslotCommand(TimeslotStartTimeContainsKeywordsPredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        String keywords = String.join(", ", predicate.getKeywords());
        String resultMessage = MessageFormat.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_COMMAND,
                model.getFilteredPersonList().size(), "findtimeslot")
                + " with timeslot starting on/at: [" + keywords + "]";
        return new CommandResult(resultMessage);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindTimeslotCommand)) {
            return false;
        }

        FindTimeslotCommand otherFindTimeslotCommand = (FindTimeslotCommand) other;
        return predicate.equals(otherFindTimeslotCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }

    public TimeslotStartTimeContainsKeywordsPredicate getPredicate() {
        return predicate;
    }
}
