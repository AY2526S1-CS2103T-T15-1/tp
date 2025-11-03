---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# EduTrack Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

This project is based on the AddressBook-Level3 (AB3) project created by the SE-EDU initiative.

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

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/AY2526S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
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

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"><puml>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2526S1-CS2103T-T15-1/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.




### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

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
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, `ClearCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2526S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/model/Model.java)

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

**API** : [`Storage.java`](https://github.com/AY2526S1-CS2103T-T15-1/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550"></puml>

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section provides an overview of the key implementation details for major features and design improvements. Each subsection highlights the rationale, core logic, and design considerations behind the feature, with reference diagrams where appropriate.

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

The `clearpast` feature introduces an intelligent cleanup mechanism to manage outdated or recurring timeslots which is specifically designed for tutors. It iterates through the entire person list and performs actions on contacts whose timeslots are in the past (i.e., `TimeSlot.isPast(LocalDateTime.now())` is true).

The command follows two main logic paths:
1.  **Non-recurring contacts:** If a past contact does **not** have the `t/recurring` tag, it is deleted from the `Model` using `model.deletePerson()`.
2.  **Recurring contacts:** If a past contact **has** the `t/recurring` tag, the command calculates its next weekly occurrence using `TimeSlot.getNextOccurrence(now)`. It then attempts to update the contact with the new timeslot using `model.setPerson()`.

This `execute` method is designed to be "all-or-nothing" for each contact but not for the whole command. It builds lists of successfully deleted, successfully updated, and failed-to-update (conflicted) contacts and presents this summary to the user in the `CommandResult`.

<box type="info" seamless>

**Note:** Conflict handling is critical. When `clearpast` calls `model.setPerson()` with a recurring contact's new timeslot, the `ModelManager`'s `setPerson` logic handles the conflict check. If the new timeslot is already taken, `setPerson` throws a `TimeSlotConflictException` which contains details about the conflicting person. The `ClearPastCommand` catches this specific exception, formats a detailed error message including the conflicting person's name and timeslot, adds this to the `conflictNames` list, and continues processing the rest of the contacts.

</box>

At the end of the operation, `clearpast` forces the UI to refresh and re-sort by the default timeslot order by calling `model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS)`.

The following activity diagram summarizes the logic flow for the `clearpast` command:

<puml src="diagrams/HighLevelClearPastActivityDiagram.puml" alt="HighLevelClearPastActivityDiagram"></puml>

#### Design considerations:

**Aspect: How to handle recurring timeslots:**

* **Alternative 1 (current choice):** A manual `clearpast` command that checks for a `t/recurring` tag.
    * Pros: Simple to implement and understand. The user retains full control over when their schedule is cleaned up. Fits well within a command-line application.
    * Cons: The user needs to remember to run the command. Only supports one type of recurrence (weekly).

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
* **Alternative 2:** Transactional approach. If *any* update caused a conflict, the *entire* command would fail and make no changes.
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

| Priority | As a …                      | I want to …                                                                | So that I can…                                                               |
|----------|-----------------------------|----------------------------------------------------------------------------|------------------------------------------------------------------------------|
| `***`    | busy tutor                  | add a new student with contact info, time slot, and tags                   | quickly store lesson details without wasting time on manual records          |
| `***`    | busy tutor                  | view all my students sorted by their time slots                            | see my upcoming lessons at a glance                                          |
| `***`    | busy tutor                  | use the `filtertimeslot` command to show only students within a time range | plan my teaching day efficiently without scrolling through unrelated entries |
| `***`    | busy tutor                  | clear past appointments automatically using `clearpast`                    | keep my student list focused on current and upcoming sessions                |
| `**`     | busy tutor                  | find students by name, tag, or time slot                                   | locate a specific student or class quickly without searching manually        |
| `**`     | busy tutor                  | edit a student’s details when their contact info or schedule changes       | keep my records updated without re-adding the student                        |
| `*`      | busy tutor                  | open the help command when I forget a command format                       | avoid wasting time guessing syntax                                           |
| `***`    | careless tutor              | receive conflict warnings when adding or editing overlapping time slots    | avoid double-booking myself by accident                                      |
| `**`     | careless tutor              | clear past lessons while keeping recurring ones                            | maintain recurring classes without losing future slots                       |
| `*`      | careless tutor              | rely on automatic saving after each command                                | avoid losing data if I forget to save manually                               |
| `***`    | tutor with regular students | tag students as “recurring” or by subject group                            | easily organize and find my regular students                                 |
| `***`    | tutor with regular students | automatically update recurring time slots via `clearpast`                  | ensure future sessions are rescheduled automatically                         |
| `**`     | tutor with regular students | filter students by subject tag using `findtag`                             | focus on students in the same subject group                                  |
| `*`      | tutor with regular students | edit multiple tags at once                                                 | adjust class groupings easily when students change subjects                  |
| `**`     | new tutor                   | list all sample data on startup                                            | learn how the app works with examples before adding real data                |
| `**`     | new tutor                   | view command usage instructions                                            | learn new commands without needing to refer to the manual constantly         |
| `*`      | new tutor                   | exit the app cleanly                                                       | close EduTrack without worrying about unsaved changes                        |




### Use cases

**Use case 1: Delete a person**

**MSS**

1.  User requests to list persons.
2.  EduTrack shows a list of persons.
3.  User requests to delete a specific person in the list by indicating the index of the person. 
4.  EduTrack deletes the person.

    Use case ends.

**Extensions**

* 3a. The given index is invalid.
    * 3a1. EduTrack shows an error message `The person index provided is invalid`.

      Use case resumes at step 2.


**Use case 2: Add a person**

**MSS**

1.  User requests to add a person.
2.  EduTrack requests for contact details (name, phone number, email address, address, tags, appointment slot) where tags are optional.
3.  User enters the requested details.
4.  EduTrack saves the contact to a local file.

   Use case ends.

**Extensions**

* 3a. User leaves required fields blank.
    * 3a1. EduTrack requests the missing fields by showing an error message `Missing prefix: p/`.
    * 3a2. User re-enters details.
    Steps 3a1–3a2 repeat until all required details are valid.

      Use case resumes at step 4.

* 3b. User enters invalid data that is of wrong format.
    * 3b1. EduTrack shows an error message `Emails should be of the format local-part@domain`.

      Use case resumes at step 2.

* 2a. User enters conflicting timeslot.
    * 2a1. EduTrack shows an error message `This time slot conflicts with another existing time slot! John Doe [2025-10-31 1500-1800]`
    
      Use case resumes at step 2.

    Use case ends.


**Use case 3: Edit a person**

**MSS**

1.  User requests to list persons.
2.  EduTrack shows a list of persons.
3.  User requests to edit a contact at a given index.
4.  User provides new details based on the supported fields for the contact.
5.  EduTrack updates the contact with the new details.

    Use case ends.

**Extensions**

* 3a. The given index is invalid.
    * 3a1. EduTrack shows an error message `The person index provided is invalid`.

      Use case resumes at step 2.

* 4a. New details are invalid (e.g., bad email format).
    * 4a1. EduTrack shows an error message.

      Use case resumes at step 4.

Here are the improved use cases for your `DeveloperGuide.md`, updated to reflect your application's precise logic, error messages, and command outputs based on our conversation.

---

**Use case 4: Find persons by tag**

**MSS**

1.  User requests to list persons.
2.  EduTrack shows a list of persons.
3.  User requests to find persons by a specific tag (e.g., `findtag Sec3Math`).
4.  EduTrack filters the list and shows only the persons who have the matching tag. A success message is shown, e.g., `3 persons listed! with tag(s): [Sec3Math]`.
    Use case ends.

**Extensions**

* 3a. User provides the command with no keywords (e.g., `findtag`).
    * 3a1. EduTrack shows an error message: `Invalid command format! findtag: Finds all persons...`.
      Use case resumes at step 2.
* 4a. No persons are found with the given tag.
    * 4a1. EduTrack shows a message `0 persons listed! with tag(s): []` and displays an empty list.
      Use case ends.

---

**Use case 5: Find persons by time slot**

**MSS**

1.  User requests to list persons.
2.  EduTrack shows a list of persons.
3.  User requests to find persons with a time slot on a specific date and time (e.g., `findtimeslot 2025-11-06 1000`).
4.  EduTrack performs an **AND** search, filtering the list to show only persons who have a time slot on that date **AND** starting at that time.
5.  EduTrack shows a success message, e.g., `1 persons listed! with timeslot starting on/at: [2025-11-06, 1000]`.
    Use case ends.

**Extensions**

* 3a. User provides only date keywords (e.g., `findtimeslot 2025-11-05 2025-11-06`).
    * 4a1. EduTrack performs an **OR** search, showing persons matching *either* date.
      Use case resumes at step 5.
* 3b. User provides only time keywords (e.g., `findtimeslot 1000 1400`).
    * 4b1. EduTrack performs an **OR** search, showing persons whose slot starts at *either* time.
      Use case resumes at step 5.
* 3c. User provides an invalid keyword format (e.g., `findtimeslot 7837e832e` or `findtimeslot 10:00`).
    * 3c1. EduTrack shows a specific error message: `Invalid keyword: '7837e832e'. Keywords must be a valid date (YYYY-MM-DD) or a valid time (HHMM).`.
      Use case resumes at step 2.
* 5a. No persons are found.
    * 5a1. EduTrack shows a message `0 persons listed! with timeslot starting on/at: [2025-11-06, 0900]` and displays an empty list.
      Use case ends.

---

**Use case 6: Filter persons by time slot range**

**MSS**

1.  User requests to list persons.
2.  EduTrack shows a list of persons.
3.  User requests to filter the list by a time slot range (e.g., `filtertimeslot st/1500 et/1800`).
4.  EduTrack filters the list, showing only persons whose time slots are **fully contained** within the specified range (i.e., start time $\geq$ `st/` and end time $\leq$ `et/`).
5.  EduTrack shows a success message detailing the filter, e.g., `2 persons listed! with timeslots starting from 15:00 and ending by 18:00`.
    Use case ends.

**Extensions**

* 3a. User provides a keyword for a date prefix (e.g., `filtertimeslot sd/now` or `filtertimeslot ed/today`).
    * 4a1. EduTrack parses `now` or `today` into the current system date and applies the filter.
      Use case resumes at step 4.
* 3b. User provides an invalid date format (e.g., `sd/20-11-2025`).
    * 3b1. EduTrack shows an error message, e.g., `Date should be in YYYY-MM-DD format.`
      Use case resumes at step 2.
* 3c. User provides an invalid date range (e.g., `sd/2025-11-20 ed/2025-11-19`).
    * 3c1. EduTrack shows an error message: `Start date must be before or on end date.`.
      Use case resumes at step 2.
* 5a. No persons are found within the filter range.
    * 5a1. EduTrack shows a message `0 persons listed! with timeslots...` and displays an empty list.
      Use case ends.

---

**Use case 7: Clear all past time slots**

**MSS**

1.  User requests to clear all past time slots by running `clearpast`.
2.  EduTrack gets the current system time (`now`).
3.  EduTrack identifies all contacts with time slots ending before `now`.
4.  For each identified contact:
    * If the contact does **not** have the `t/recurring` tag, it is **deleted** from EduTrack.
    * If the contact **has** the `t/recurring` tag, EduTrack calculates the next weekly occurrence. It then attempts to update the contact to this new time slot.
5.  EduTrack shows a multi-part success message summarizing the actions, e.g.:
    `ClearPast command successful.`
    `Deleted 1 past contact(s): Charlie Goh`
    `Updated 1 recurring contact(s): Diana Heng`
    `Could not update 1 recurring contact(s) due to conflicts: Ethan Yeo (Conflict: This time slot conflicts with: Ben Lim [2025-11-06 1000-1200])`.
    Use case ends.

**Extensions**

* 1a. User provides unexpected arguments to the command (e.g., `clearpast 1` or `clearpast extra`).
    * 1a1. EduTrack shows an error message: `Invalid command format! clearpast: Clears all past timeslots...`.
      Use case ends.
* 5a. No past time slots are found.
    * 5a1. EduTrack shows the message: `No past time slots found to clear or update.`.
      Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2.  Should be able to hold up to 1000 contacts without a noticeable sluggishness in performance for typical usage.
3.  Searching for a student or appointment slot should return results in under 1.6s.
4.  Should load interface within 5 seconds of launch.
5. Error messages must be clear and concise, and guide the user towards correcting their input.
6. The app should consume less than 600MB of memory during its operation.


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
* **Timeslot**: The specific period of time allocated to a student's lesson (e.g., 2025-10-12 1000-1200)
* **Recurring lesson/Recurring timeslot**: A lesson or timeslot that automatically repeats at a fixed interval (e.g., weekly)
* **Timeslot conflict**: A situation where two students have overlapping or identical lesson times
* **Predicate**: A logical condition used by the system to filter or search for students (e.g., based on timeslot or name)
* **Filter**: A command that limits the displayed student list based on specified criteria, such as timeslot 
* **Command**: A line of text entered by the user into the CLI to perform an action
* **Command parser**: A component responsible for interpreting user input and converting it into a command that the system can execute
* **Parameter prefix**: A short label used before command arguments (e.g., n/ for name, st/ for start time) to identify their purpose


--------------------------------------------------------------------------------------------------------------------
-----

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

\<box type="info" seamless\>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing. The sample data loaded on startup is used for these test cases.

\</box\>

### Launch and shutdown

1.  **Initial launch**

    1.  Download the jar file and copy it into an empty folder.
    2.  Double-click the jar file.
    3.  **Expected:** Shows the GUI with a set of sample contacts. The window size may not be optimum.

2.  **Saving window preferences**

    1.  Resize the window to an optimum size. Move the window to a different location. Close the window.
    2.  Re-launch the app by double-clicking the jar file.
    3.  **Expected:** The most recent window size and location is retained.

### Adding a person (`add`)

1.  **Adding a person with a time slot conflict**

    1.  **Prerequisites:** Sample data is loaded. Note Alice Tan's slot: `2025-11-05 1400-1600`.
    2.  **Test case:** `add n/New Student p/12345678 e/new@email.com a/New Address ts/2025-11-05 1500-1700` (This slot overlaps with Alice Tan)
    3.  **Expected:** No person is added. An error message is shown in the result display, identifying the conflict: `This time slot conflicts with: Alice Tan [2025-11-05 1400-1600]`.

2.  **Adding a person with invalid fields**

    1.  **Test case (invalid time):** `add n/New Student p/12345678 e/new@email.com a/New Address ts/2025-11-05 1500-1400`
    2.  **Expected:** No person is added. Error message about invalid time slot range.
    3.  **Test case (missing name):** `add p/12345678 e/new@email.com a/New Address ts/2025-11-05 1500-1700`
    4.  **Expected:** No person is added. Error message about missing `n/` prefix.

### Locating by tag (`findtag`)

1.  **Find by a single tag**

    1.  **Prerequisites:** Sample data is loaded.
    2.  **Test case:** `findtag Sec3Math`
    3.  **Expected:** The list filters to show 3 persons: `Diana Heng`, `Alice Tan`, and `Fiona Wee`.

2.  **Find by multiple tags (OR search)**

    1.  **Prerequisites:** Sample data is loaded.
    2.  **Test case:** `findtag recurring NeedsHelp`
    3.  **Expected:** The list filters to show 3 persons: `Diana Heng` and `Ethan Yeo` (for `recurring`) and `Ben Lim` (for `NeedsHelp`).

3.  **Find by tag (case-insensitive and substring)**

    1.  **Prerequisites:** Sample data is loaded.
    2.  **Test case:** `findtag sec3`
    3.  **Expected:** The list filters to show 3 persons: `Diana Heng`, `Alice Tan`, and `Fiona Wee`.

4.  **Find by tag (no results)**

    1.  **Prerequisites:** Sample data is loaded.
    2.  **Test case:** `findtag NonExistentTag`
    3.  **Expected:** The list is empty. Status message indicates `0 persons listed!`.

5.  **Invalid `findtag` command**

    1.  **Test case:** `findtag` (no arguments)
    2.  **Expected:** Error message: `Invalid command format! findtag: ...`

### Finding by time slot (`findtimeslot`)

1.  **Find by date**

    1.  **Prerequisites:** Sample data is loaded.
    2.  **Test case:** `findtimeslot 2025-10-30`
    3.  **Expected:** Shows `Ethan Yeo`.

2.  **Find by time (multiple results)**

    1.  **Prerequisites:** Sample data is loaded.
    2.  **Test case:** `findtimeslot 1000`
    3.  **Expected:** Shows `Ethan Yeo` and `Ben Lim` (both start at 10:00).

3.  **Find by date and time (AND search)**

    1.  **Prerequisites:** Sample data is loaded.
    2.  **Test case:** `findtimeslot 2025-11-06 1000`
    3.  **Expected:** Shows `Ben Lim` only.

4.  **Find by date and time (no results)**

    1.  **Prerequisites:** Sample data is loaded.
    2.  **Test case:** `findtimeslot 2025-10-22 1600`
    3.  **Expected:** Empty list. (Charlie matches the date but not the time; Diana matches the time but not the date).

5.  **Invalid `findtimeslot` command**

    1.  **Test case:** `findtimeslot 10:00`
    2.  **Expected:** Error message: `Invalid keyword: '10:00'. Keywords must be a valid date (YYYY-MM-DD) or a valid time (HHMM).`
    3.  **Test case:** `findtimeslot` (no arguments)
    4.  **Expected:** Error message: `Invalid command format! ... At least one parameter (date or time) must be provided.`

### Filtering by time slot range (`filtertimeslot`)

1.  **Filter by time range**

    1.  **Prerequisites:** Sample data is loaded.
    2.  **Test case:** `filtertimeslot st/1500 et/1800`
    3.  **Expected:** Shows `Charlie Goh` (15:00-17:00) and `Diana Heng` (16:00-18:00), as their slots are *fully contained* within the range.

2.  **Filter by date range**

    1.  **Prerequisites:** Sample data is loaded.
    2.  **Test case:** `filtertimeslot sd/2025-11-01 ed/2025-11-10`
    3.  **Expected:** Shows `Alice Tan` (Nov 5), `Ben Lim` (Nov 6), and `George Png` (Nov 10).

3.  **Filter by combined date and time**

    1.  **Prerequisites:** Sample data is loaded.
    2.  **Test case:** `filtertimeslot sd/2025-11-05 ed/2025-11-12 st/1300 et/1700`
    3.  **Expected:** Shows `Alice Tan` (Nov 5, 14:00-16:00). Fiona Wee (Nov 12, 11:00-13:00) is excluded by the time range.

4.  **Filter using `now` or `today`**

    1.  **Prerequisites:** Note the current system date and time.
    2.  **Test case:** `filtertimeslot sd/today`
    3.  **Expected:** Shows all students with time slots on or after the current system date.
    4.  **Test case:** `filtertimeslot st/now`
    5.  **Expected:** Shows all students (regardless of date) whose time slots start and end *after* the current system time.

5.  **Invalid `filtertimeslot` command**

    1.  **Test case:** `filtertimeslot` (no arguments)
    2.  **Expected:** Error message: `Invalid command format! ... At least one prefix must be provided.`
    3.  **Test case:** `filtertimeslot sd/2025-11-10 ed/2025-11-01`
    4.  **Expected:** Error message: `Start date must be before or on end date.`
    5.  **Test case:** `filtertimeslot st/1400 et/1200`
    6.  **Expected:** Error message: `Start time must be before end time.`

__________________________________________

### Clearing past appointments (`clearpast`)

\<box type="info" seamless\>

**Note on testing `clearpast`:**

This command is challenging to test because the application **prevents you from adding time slots that are already in the past**.

To test this feature, you must add contacts with time slots scheduled for the **near future** (e.g., starting in 1-2 minutes). You must then **wait** for those time slots to end, turning them into "past" appointments. Only then can you run `clearpast` to observe the results.

The test case below is **time-sensitive**. You will need to copy and paste the `add` commands quickly.
\</box\>

1.  **Test setup (Full Scenario)**

    1.  Note your current system date and time. For this example, let's assume it's `2025-11-02 15:30:00`.
    2.  First, add a "blocker" contact for *next week*. This contact will be the conflict target for one of our recurring students.
        * `add n/Future Blocker p/333 e/block@e.com a/block ts/2025-11-09 15:31-15:33 t/TestBlock`
    3.  **Quickly**, add the following three contacts. Their time slots are set 1-2 minutes in the future and will all be in the past in about 4-5 minutes.
        * **(For Delete):** `add n/Past Student p/111 e/past@e.com a/past ts/2025-11-02 15:31-15:32 t/TestDelete`
        * **(For Update - OK):** `add n/Update Student p/222 e/rec@e.com a/rec ts/2025-11-02 15:32-15:33 t/recurring t/TestUpdate` (Next week's slot, `2025-11-09 15:32-15:33`, is free).
        * **(For Update - Conflict):** `add n/Conflict Student p/444 e/old@e.com a/old ts/2025-11-02 15:31-15:33 t/recurring t/TestConflict` (Next week's slot, `2025-11-09 15:31-15:33`, conflicts with `Future Blocker`).

2.  **Wait for the slots to pass**

    * Wait for your system time to pass `2025-11-02 15:33:00` (e.g., wait 4-5 minutes). All three students added in step 1.3 are now in the past.

3.  **Test case: Execute `clearpast`**

    1.  Run the command: `clearpast`
    2.  **Expected:** A composite success message should appear, similar to:
        * `Deleted 1 past contact(s): Past Student`
        * `Updated 1 recurring contact(s): Update Student`
        * `Could not update 1 recurring contact(s) due to conflicts: Conflict Student (Conflict: This time slot conflicts with: Future Blocker [2025-11-09 15:31-15:33])`

4.  **Test case: Verify state with `list`**

    1.  Run the command: `list`
    2.  **Expected:**
        * `Past Student` is gone.
        * `Update Student` is still present, but their time slot is now in the future (e.g., `2025-11-09 15:32-15:33`).
        * `Conflict Student` is still present with their *old* past time slot (`2025-11-02 15:31-15:33`), as the update failed.
        * `Future Blocker` is unaffected.

5.  **Invalid `clearpast` command**

    1.  **Test case:** `clearpast 123`
    2.  **Expected:** Error message: `Invalid command format! clearpast: Clears all past timeslots...`

### Deleting a person (`delete`)

1.  **Deleting a person while all persons are being shown**
    1.  **Prerequisites:** List all persons using the `list` command. Multiple persons in the list.
    2.  **Test case:** `delete 1`
    3.  **Expected:** The first contact in the *currently displayed list* is deleted. Details of the deleted contact are shown in the result display.
    4.  **Test case:** `delete 0`
    5.  **Expected:** No person is deleted. Error details shown: `The person index provided is invalid`.
    6.  **Other incorrect delete commands to try:** `delete`, `delete x`, `...` (where x is larger than the list size)
    7.  **Expected:** Similar error message about an invalid index.

### Editing a person (`edit`)

1.  **Editing tags (removing all)**

    1.  **Prerequisites:** Sample data is loaded. Note Diana Heng (index 2) has tags `Sec3Math` and `recurring`.
    2.  **Test case:** `edit 2 t/`
    3.  **Expected:** Diana Heng's tags are removed. The success message confirms the edit, and her card in the UI now shows no tags.

2.  **Editing time slot (causing conflict)**

    1.  **Prerequisites:** Sample data is loaded. Note Alice Tan's slot (index 5) is `2025-11-05 1400-1600` and Ben Lim's (index 4) is `2025-11-06 1000-1200`.
    2.  **Test case:** `edit 5 ts/2025-11-06 1100-1300` (This overlaps with Ben Lim)
    3.  **Expected:** Alice Tan is not edited. Error message: `This time slot conflicts with: Ben Lim [2025-11-06 1000-1200]`.

### Clearing all entries (`clear`)

1.  **Clear all**

    1.  **Prerequisites:** The list is populated with sample data.
    2.  **Test case:** `clear`
    3.  **Expected:** All contacts are deleted. The list is now empty.

2.  **Invalid clear command**

    1.  **Test case:** `clear 123`
    2.  **Expected:** Error message: `Invalid command format! clear: ...`

### Planned Enhancements

**Team Size:** 5

1.  **Support for Multiple Lessons per Person:** The current data model supports only a one-to-one relationship between a `Person` and a `TimeSlot`. This will be refactored to a one-to-many relationship, allowing a single `Person` to be associated with a list of `TimeSlot` objects. Business logic will be added to ensure these timeslots do not overlap.
2.  **Support for Multi-Person Timeslots (Group Tuition):** The data model will be enhanced to support a many-to-many relationship, allowing multiple `Person` objects to be associated with a single `TimeSlot`.
3.  **Unique, Immutable Student ID:** We plan to implement a system to generate a unique, non-editable Student ID (e.g., `S-0001`) for every `Person` created.
    * **Justification:** This ID will serve as the stable primary key for each student. This prevents data ambiguity when two students have the same name and ensures data integrity if a student's name changes. This ID will be crucial for stable integration with other systems, such as payment portals or external academic record databases.
4.  **Timeslots Spanning Across Midnight:** The `TimeSlot` model will be re-designed to support start and end `LocalDateTime` objects instead of just `LocalDate` and `LocalTime`. This will allow a `TimeSlot` to correctly span across calendar days (e.g., 23:00 on Monday to 01:00 on Tuesday).
5.  **Flexible Recurrence Intervals:** The recurrence logic is currently hardcoded for weekly intervals (e.g., the `clearpast` command advances a `t/recurring` lesson in intervals of 7 days until it is no longer in the past). This will be refactored to support more versatile intervals, such as daily, bi-weekly, and monthly.


