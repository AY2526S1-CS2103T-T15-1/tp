---
  layout: default.md
  title: "User Guide"
  pageNav: 3
---

# AB-3 User Guide

AddressBook Level 3 (AB3) is a **desktop app for managing contacts, optimized for use via a  Line Interface** (CLI) while still having the benefits of a Graphical User Interface (GUI). If you can type fast, AB3 can get your contact management tasks done faster than traditional GUI apps.

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `17` or above installed in your Computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar addressbook.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts by timeslot.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<box type="info" seamless>

**Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

* Items in square brackets are optional.<br>
  e.g `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[t/TAG]…​` can be used as ` ` (i.e. 0 times), `t/friend`, `t/friend t/family` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `exit` and `clear`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</box>

### Viewing help : `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`

<box type="tip" seamless>

**Tip:** A person can have any number of tags (including 0)
</box>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

### Listing all persons : `list`

Shows a list of all persons in the address book.

Format: `list`

### Editing a person : `edit`

Edits an existing person in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, the existing tags of the person will be removed i.e adding of tags is not cumulative.
* You can remove all the person’s tags by typing `t/` without
    specifying any tags after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

### Locating persons by name: `find`

Finds persons whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive. e.g `hans` will match `Hans`
* The order of the keywords does not matter. e.g. `Hans Bo` will match `Bo Hans`
* Only the name is searched.
* Only full words will be matched e.g. `Han` will not match `Hans`
* Persons matching at least one keyword will be returned (i.e. `OR` search).
  e.g. `Hans Bo` will return `Hans Gruber`, `Bo Yang`

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a person : `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Clearing all entries : `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program : `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

### Editing the data file

AddressBook data are saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<box type="warning" seamless>

**Caution:**
If your changes to the data file makes its format invalid, AddressBook will discard all data and start with an empty data file at the next run.  Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</box>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

### What's new (Combined changes from v1.3 and v1.4)

Disclaimer, we have not added the corresponding images to the user guide

### New timeSlot Field for Contacts

* You can now add a dedicated time slot to each person in the address book. This is perfect for scheduling meetings, appointments, or consultations; in our case, it would be used for lessons.

* The field is added using the add command. (this is a compulsory field)

Format: `ts/YYYY-MM-DD HHMM-HHMM` (e.g., ts/2025-10-12 1200-1400)

Updated addCommmand is as below:

* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal ts/2025-10-12 1200-1400`

### Findtimeslot Command

We've added a flexible new command, findtimeslot, to easily find contacts based on their scheduled time slots. The command supports three types of searches:

1. Find by Date: Lists all persons who have a time slot on a specific date.

Format: `findtimeslot YYYY-MM-DD`

* This will list all contacts (tutees) in EduTrack with timeslots that fall on that day

Example: `findtimeslot 2025-10-12`

2.  Find by Time: Lists all persons whose time slot starts at a specific time, regardless of the date.

Format: `findtimeslot HHMM`

* This will list all contacts (tutees) in EduTrack with timeslots that start from the specified time

Example: `findtimeslot 1200`

3.  Find by Date and Time: Narrows the search to a specific date and start time.

Format: `findtimeslot YYYY-MM-DD HHMM`

* This is the most specific, and will list all contacts (tutees) in EduTrack with
  timeslots that start from the specified time, and fall on that day

Example: `findtimeslot 2025-10-12 1200`

### Automatic Time Slot Conflict Detection

* To prevent double-booking, the application now automatically checks for scheduling conflicts.

* You will be prevented from adding or editing a contact if their specified time slot 
overlaps with another existing time slot. An error message will be shown to alert you of the conflict.

### Prefix search for default findCommand

With reference from the original find, where 

* Only full words will be matched e.g. `Han` will not match `Hans`;

* we have now improved it such that searching `find Ha` would return `Han`, `Hans`, and `Hansen` (assuming in addressbook)

### New findtag Command

Format: `findtag [TAG]`

* Finds contacts that match one or more tags.

Example: `findtag Math`

* List is filtered to show all students with tag Math

### New filtertimeslot Command

Format: `filtertimeslot` sd/[START_DATE] ed/[END_DATE] st/[START_TIME] et/[END_TIME]

* You must provide at least one of the following prefixes: 
* sd/ (start date), ed/ (end date), st/ (start time), or et/ (end time); the rest are optional fields (like tag)

Example: `filtertimeslot sd/2025-10-27 ed 2025-10-27 st/0800 et/1200`

Result: Only contacts with timeslots that are on 27 Oct 2025, with timeslots in between 0800 to 1200 are shown

Example: `filtertimeslot sd/2025-10-27 st/0800 et/1200`

Result: Only contacts with timeslots that are on/after 27 Oct 2025, with timeslots in between 0800 to 1200 are shown

Example: `filtertimeslot sd/2025-10-20 ed/2025-10-21 st/0800`

Result: Only contacts with timeslots that are between 20-21 Oct 2025, with timeslots on/after 0800 are shown

### New clearpast Command

* For any long-term tutees that the user may have (weekly lessons), he can tag them as "recurring"
* Clearpast will use the current time to retrieve all contacts with timeslots in the past
* Amongst these timeslots, those that are not marked recurring will be deleted (cleared from addressbook)
* For recurring timeslots, the contact will have its timeslot automatically updated to 7 days in the future, 
provided that there is no conflict with a future timeslot; if there is, the update will fail with an error message

Format: `clearpast`

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action     | Format, Examples
-----------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Add**    | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS ts/TIMESLOT [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 ts/2025-10-27 1400-1600 t/friend t/colleague`
**Clear**  | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit**   | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`
**Find**   | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List**   | `list`
**Help**   | `help`
**Findtag**| `find TAG [MORE_TAGS}` <br> e.g., `find Math English`
**FindTimeSlot** | `findtimeslot [DATE] [TIME]` <br> e.g. `findtimeslot 2025-10-27 1400`
**Filtertimeslot** | `filtertimeslot [sd/START_DATE] [ed/END_DATE] [st/START_TIME] [et/END_TIME]` <br> e.g `filtertimeslot 2025-10-27 2025-10-27 1400`
**Clearpast** | `clearpast`