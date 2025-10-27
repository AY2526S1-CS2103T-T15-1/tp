package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.TimeslotRangePredicate;

/**
 * Filters and lists all persons in address book whose timeslot overlaps with the specified range.
 */
public class FilterTimeslotCommand extends Command {

    public static final String COMMAND_WORD = "filtertimeslot";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Filters all persons whose timeslots overlap with the specified date/time range.\n"
            + "Parameters: At least one of the following prefixes must be provided: "
            + "sd/START_DATE (YYYY-MM-DD) "
            + "ed/END_DATE (YYYY-MM-DD) "
            + "st/START_TIME (HHMM) "
            + "et/END_TIME (HHMM)\n"
            + "Example: " + COMMAND_WORD + " sd/2025-10-20 st/0900 et/1300";

    private final TimeslotRangePredicate predicate;

    public FilterTimeslotCommand(TimeslotRangePredicate predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof FilterTimeslotCommand
                && predicate.equals(((FilterTimeslotCommand) other).predicate));
    }
}
