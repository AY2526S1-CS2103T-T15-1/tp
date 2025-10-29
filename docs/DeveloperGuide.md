---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# AB-3 Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280"></puml>

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574"></puml>

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300"></puml>

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"><puml>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"></puml>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command"></puml>

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"></puml>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class determines the command word and creates the appropriate parser (e.g., `AddCommandParser`, `DeleteCommandParser`, or simpler ones like `ClearCommandParser`, `ListCommandParser`). This specific parser uses the other utility classes shown above to parse the arguments (if any) and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* Parsers for commands requiring specific arguments (like `AddCommandParser`) also provide detailed error messages if mandatory prefixes are missing, while `EditCommandParser` success message confirms with the user the edited fields.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser` `ClearCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450"></puml>


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* Provides methods to check for potential timeslot conflicts (`getConflictingPerson`), abstracting this logic away from individual commands
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450"></puml>

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550"></puml>

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Implemented\] `filtertimeslot` feature

#### Proposed Implementation

The `filtertimeslot` feature allows users to filter the displayed list of persons to only those whose timeslots fall within a specified date and/or time range. This mechanism is facilitated by a new `TimeslotRangePredicate` class.

The `FilterTimeslotCommand`'s `execute` method passes this predicate to the `Model`'s `updateFilteredPersonList(predicate)` method. The `ModelManager` then uses this predicate to update its `FilteredList`, which in turn updates the UI.

The `FilterTimeslotCommandParser` is responsible for parsing the user's input, which must contain at least one of the four optional prefixes: `sd/` (start date), `ed/` (end date), `st/` (start time), and `et/` (end time). It also supports the keywords now and today (e.g., `sd/today`, `st/now`) for date and time fields to filter relative to the current system time.These are used to construct the `TimeslotRangePredicate` which contains the logic to check if a `Person`'s `TimeSlot` overlaps with the specified range.

The following sequence diagram shows how a `filtertimeslot` operation goes through the components:

<puml src="diagrams/FilterTimeslotSequenceDiagram.puml" alt="FilterTimeslotSequenceDiagram"></puml>

#### Design considerations:

**Aspect: How to parse the filter parameters:**

* **Alternative 1 (current choice):** Use flexible, optional prefixes (e.g., `sd/`, `st/`, `et/`).
    * Pros: Highly flexible. Allows users to filter by just a start time (`st/0900`), just a date range (`sd/2025-10-20 ed/2025-10-22`), or a specific time on a specific day (`sd/2025-10-20 st/0900 et/1700`).
    * Cons: Parsing logic is more complex as many combinations are valid.

* **Alternative 2:** A single, fixed-format argument (e.g., `filtertimeslot YYYY-MM-DD to YYYY-MM-DD`).
    * Pros: Very simple to parse.
    * Cons: Much less flexible. It's difficult to filter for "all 9am-12pm slots on any date," which is a key use case.

**Aspect: Handling of Relative Time ("Now"/"Today")**

* **Alternative 1 (Current Choice):** Special keywords (`now`, `today`) used as *values* for existing date/time prefixes (`sd/`, `st/`, etc.).
    * **Pros:** Highly flexible and **composable**. Allows combining relative times with specific dates/times (e.g., `sd/now ed/2025-11-30`). Consistent with the existing prefix-based command structure.
    * **Cons:** Requires slightly more complex logic within the parser to detect and handle these keywords.
* **Alternative 2:** Introduce a dedicated, mutually exclusive prefix or flag (e.g., `filtertimeslot /future` or `filtertimeslot mode=future`).
    * **Pros:** Might seem conceptually simpler for a single use case (like "show only future slots").
    * **Cons:** Much less flexible. Cannot be easily combined with other date/time filters. Adds complexity to the parser to handle mutual exclusion rules. Violates the consistency of using prefixes for parameters.

**Aspect: Definition of Time Range Overlap**

* **Alternative 1 (Current Choice):** Overlap requires actual intersection of time intervals. Slots ending exactly when the filter starts, or starting exactly when the filter ends, are *not* considered overlapping.
    * **Pros:** Mathematically clear definition of overlap.
    * **Cons:** May not match user intuition for back-to-back schedules (e.g., filter `et/1000` might not include a person scheduled `1000-1100`).
* **Alternative 2:** Inclusive boundaries. Consider slots overlapping if they touch at the boundaries (e.g., filter `et/1000` *would* include `1000-1100`).
    * **Pros:** Catches adjacent schedules, potentially matching user expectation better.
    * **Cons:** Requires careful adjustment of the boundary checking logic in `TimeslotRangePredicate` (e.g., using `!isBefore` instead of `isAfter` and `!isAfter` instead of `isBefore`).

### \[Implemented\] `clearpast` feature

#### Proposed Implementation

The `clearpast` feature provides a "smart-clear" function designed for tutors. It iterates through the entire person list and performs actions on contacts whose timeslots are in the past (i.e., `TimeSlot.isPast(LocalDateTime.now())` is true).

The command follows two main logic paths:
1.  **Non-recurring contacts:** If a past contact does **not** have the `t/recurring` tag, it is deleted from the `Model` using `model.deletePerson()`.
2.  **Recurring contacts:** If a past contact **has** the `t/recurring` tag, the command calculates its next weekly timeslot using `TimeSlot.getNextOccurrence(now)`. It then attempts to update the contact with this new timeslot using `model.setPerson()`.

This `execute` method is designed to be "all-or-nothing" for each contact but not for the whole command. It builds lists of successfully deleted, successfully updated, and failed-to-update (conflicted) contacts and presents this summary to the user in the `CommandResult`.

<box type="info" seamless>

**Note:** Conflict handling is critical. When `clearpast` calls `model.setPerson()` with a recurring contact's new timeslot, the `ModelManager`'s `setPerson` logic handles the conflict check. If the new timeslot is already taken, `setPerson` throws a `TimeSlotConflictException` which contains details about the conflicting person. The `ClearPastCommand` catches this specific exception, formats a detailed error message including the conflicting person's name and timeslot, adds this to the `conflictNames` list, and continues processing the rest of the contacts.

</box>

At the end of the operation, `clearpast` forces the UI to refresh and re-sort by the default timeslot order by calling `model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS)`.

The following activity diagram summarizes the logic flow for the `clearpast` command:

<puml src="diagrams/HighLevelClearPastActivityDiagram.puml" alt="HighLevelClearPastActivityDiagram"></puml>

Detailed Logic Flow for `clearpast`

<puml src="diagrams/ClearPastActivityDiagram.puml" alt="ClearPastActivityDiagram"></puml>

#### Design considerations:

**Aspect: How to handle recurring timeslots:**

* **Alternative 1 (current choice):** A manual `clearpast` command that checks for a `t/recurring` tag.
    * Pros: Simple to implement and understand. The user retains full control over when their schedule is cleaned up. Fits well within a command-line application.
    * Cons: The user must remember to run the command. Only supports one type of recurrence (weekly).

* **Alternative 2:** A fully abstract `Schedule` class with `OneTimeSlot` and `RecurringSlot` subclasses.
    * Pros: Far more powerful. Could support complex schedules (e.g., "every Monday and Wednesday"). The `list` command could show all future occurrences.
    * Cons: Massive architectural change. It would require rewriting all time-based commands (`add`, `edit`, `findtimeslot`, `filtertimeslot`, `list` sorting) and the conflict detection logic.

* **Alternative 3:** An automatic background service that "watches" the clock.
    * Pros: Fully automated.
    * Cons: Not feasible for a simple command-line application. It's impossible to resolve conflicts (like in Alternative 1) without user input, leading to data being lost or updates failing silently.

**Aspect: Conflict Resolution Strategy for Recurring Slots**

* **Alternative 1 (Current Choice):** Report conflict and skip update for that specific contact.
    * **Pros:** Safe, prevents accidental data loss or overwriting. Provides clear feedback to the user about which updates failed.
    * **Cons:** Requires the user to manually resolve the conflict later. The address book can be left in a partially updated state.
* **Alternative 2:** Force overwrite. Delete the conflicting contact to make space for the recurring one.
    * **Pros:** Ensures the recurring appointment is always scheduled. Fully automated.
    * **Cons:** High risk of unintended data loss. Could be confusing for the user.
* **Alternative 3:** Automatic rescheduling. Find the *next available* slot for the recurring contact.
    * **Pros:** Attempts to preserve both contacts.
    * **Cons:** Complex logic. The recurring contact might end up scheduled at an unexpected time.

**Aspect: Granularity of Operation (All-or-Nothing)**

* **Alternative 1 (Current Choice):** Process each past contact individually. Deletions happen, and updates happen or fail one by one.
    * **Pros:** Robust against errors. If one update fails, others can still succeed. Provides detailed feedback.
    * **Cons:** The command is not atomic. State might be inconsistent until conflicts are resolved.
* **Alternative 2:** Transactional approach. If *any* update would cause a conflict, fail the *entire* command and make no changes.
    * **Pros:** Ensures the address book state remains consistent.
    * **Cons:** Less user-friendly if one conflict prevents many valid changes. More complex to implement.

### General Design Improvements

Beyond specific features, several architectural improvements were made to enhance code quality, maintainability, and user experience across the application.

#### Smarter Conflict Detection (in `Model`)

To adhere to the **Single Level of Abstraction (SLA)** principle, the business logic for detecting timeslot conflicts and duplicate persons is centralized within the `Model` component, specifically in `ModelManager#addPerson` and `ModelManager#setPerson`. Commands like `AddCommand` and `EditCommand` now delegate these checks to the `Model`. They simply call the appropriate `Model` method and handle any `TimeSlotConflictException` or `DuplicatePersonException` that arises. This separation makes the commands simpler and ensures consistent validation logic.

#### Safer & Consistent Commands (Argument Handling)

To improve user experience and ensure consistent command behavior, commands that are not designed to accept arguments (such as `clear`, `list`, `exit`, and `help`) now utilize dedicated parsers (e.g., `ClearCommandParser`, `ListCommandParser`). These parsers strictly check for the absence of arguments and throw a `ParseException` if any unexpected input is provided after the command word. This prevents potentially confusing situations (e.g., `list 123` silently ignoring "123") and provides immediate, clear feedback to the user, adhering to the principle of least surprise.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0"></puml>

Step 2. The user executes `delete 3` command to delete the 3rd person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1"></puml>

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2"></puml>

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3"></puml>


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic"></puml>

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model"></puml>

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4"></puml>

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5"></puml>

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250"></puml>

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of tutees
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps
* wants to stay organized by storing student details, lesson schedules, and progress notes in one fast, CLI-based system
* wants to search for upcoming students in a particular appointment slot
* wants to sort all students by timeslot, earlier first

**Value proposition**: manage contacts faster than a typical mouse/GUI driven app


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                 | I want to …​                                                      | So that I can…​                                                     |
|----------|--------------------------------------------|----------------------------------------------------------------------|------------------------------------------------------------------------|
| `* * *`  | new user                                   | see usage instructions                                               | refer to instructions when I forget how to use the App                 |
| `* * *`  | user                                       | add a new person                                                     |                                                                        |
| `* * *`  | user                                       | delete a person                                                      | remove entries that I no longer need                                   |
| `* * *`  | user                                       | find a person by name                                                | locate details of persons without having to go through the entire list |
| `* *`    | user                                       | hide private contact details                                         | minimize chance of someone else seeing them by accident                |
| `*`      | user with many persons in the address book | sort persons by name                                                 | locate a person easily                                                 |
| `***`    | tutor                                      | open EduTrack                                                        | start managing my students and lessons quickly                         |
| `***`    | tutor                                      | exit EduTrack                                                        | stop using it and free system resources                                |
| `**`     | tutor                                      | minimize or switch EduTrack (background)                             | return later and continue exactly where I left off                     |
| `**`     | tutor                                      | resume EduTrack from background                                      | continue entering or reviewing student data without losing progress    |
| `*`      | tutor                                      | receive notifications while EduTrack is running in the background    | stay informed about upcoming lessons or reminders                      |
| `***`    | tutor                                      | add a new student (CLI) with name, email, phone number, and subjects | quickly store my students’ details                                     |
| `***`    | tutor                                      | delete a student record (CLI)                                        | remove students who no longer attend my lessons                        |
| `*`      | tutor                                      | update a student’s information (CLI)                                 | correct or add new details like email or subject enrollment            |
| `***`    | tutor                                      | view detailed student information (CLI)                              | see all contact info, enrolled subjects, and progress notes            |
| `***`    | tutor                                      | view student list in compact format (CLI)                            | quickly browse all students without scrolling through long records     |
| `***`    | tutor                                      | access help/usage guide (CLI)                                        | know all available commands and how to use them efficiently            |
| `***`    | tutor                                      | search for a student by name or keyword (CLI)                        | quickly locate a student without going through the entire list         |
| `*`      | tutor                                      | filter students starting with a given letter (CLI)                   | easily browse students alphabetically                                  |
| `**`     | tutor                                      | assign students to groups (e.g., Year 1, Math, Physics) (CLI)        | organize students by class, subject, or other category                 |
| `**`     | tutor                                      | filter and view students within one group (CLI)                      | quickly focus on a subset of students                                  |
| `*`      | tutor                                      | delete all student records (CLI)                                     | clear the system before a new semester or year (confirmation required) |
| `*`      | tutor                                      | export student data to CSV or JSON (CLI)                             | integrate with other tools or share student info with colleagues       |
| `*`      | admin                                      | view usage logs / audit trail (CLI, identity confirmation)           | track changes made to student records for accountability               |
| `*`      | admin                                      | back up all student data (CLI, identity confirmation)                | ensure student info is safe and can be restored if necessary           |
| `*`      | admin                                      | restore student data from backup (CLI, identity confirmation)        | recover from accidental deletion or system failure                     |
| `*`      | admin                                      | force terminate EduTrack (CLI, identity confirmation)                | reclaim resources or enforce system policies                           |


[//]: # (*{More to be added}*)

### Use cases

(For all use cases below, the **System** is the `AddressBook` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Delete a person**

**MSS**

1.  User requests to list persons.
2.  EduTrack shows a list of persons.
3.  User requests to delete a specific person in the list.
4.  EduTrack requests the user to confirm the deletion.
5.  User confirms the deletion.
6.  EduTrack deletes the person.

    Use case ends.

**Extensions**

* 2a. The list is empty.
    * 2a1. EduTrack informs user that the contact list is empty.

      Use case ends.

* 3a. The given index is invalid.
    * 3a1. EduTrack shows an error message.

      Use case resumes at step 2.


**Use case: Add a person**

**MSS**

1.  User requests to add a person.
2.  EduTrack requests for contact details (name, phone number, email address, subject, appointment slot).
3.  User enters the requested details.
4.  EduTrack saves the contact to a local file.

   Use case ends.

**Extensions**

* 3a. User leaves required fields blank.
    * 3a1. System requests the missing fields.
    * 3a2. User re-enters details.
    Steps 3a1–3a2 repeat until all required details are valid.

      Use case resumes at step 4.

* 3b. User enters invalid data (e.g., wrong email format).

    * 3b1. System shows an error message.

      Use case resumes at step 2.

* *a. At any time, User cancels the operation.
    * *a1. System requests to confirm the cancellation.
    * *a2. User confirms the cancellation
    * *a3. System discards input and returns to idle state.

      Use case ends.


**Use case: Edit a person**

**MSS**

1.  User requests to list persons.
2.  EduTrack shows a list of persons.
3.  User requests to edit a contact at a given index.
4.  User provides new details based on the supported fields for the contact.
5.  EduTrack updates the contact with the new details.

    Use case ends.

**Extensions**

* 3a. The given index is invalid.
    * 3a1. EduTrack shows an error message.

      Use case resumes at step 2.

* 4a. New details are invalid (e.g., bad email format).
    * 4a1. AddressBook shows an error message.

      Use case resumes at step 4.

*{More to be added}*

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 contacts without a noticeable sluggishness in performance for typical usage.
3.  Searching for a student or appointment slot should return results in under 1.6s.
4.  Should load interface within 2 seconds of launch.
5.  Audit logs of tutors and admins should be maintained for at least 1 year.

*{More to be added}*

### Glossary

* **Admin**: Administrative figure in charge of the app data, access and usage
* **Appointment slot**: The time frame in which the contact has booked an appointment
* **CLI**: Command Line Interface
* **GUI**: Graphical User Interface
* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Student**: The student of a tutor
* **Tutor**: A private hire tuition teacher
* **User logs/Audit trail**: Recorded details of user usage such as the editing of student records





--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
