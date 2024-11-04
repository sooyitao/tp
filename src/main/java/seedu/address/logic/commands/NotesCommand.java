package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADD;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DELETE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_VIEW;

import java.util.List;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Notes;
import seedu.address.model.person.Person;

/**
 * Views, adds, or deletes notes of a person identified using the person's name in the address book.
 */
public class NotesCommand extends Command {

    public static final String COMMAND_WORD = "notes";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Views, adds, or deletes the notes of the person identified by their name.\n"
            + "Parameters: \n"
            + "View: " + PREFIX_VIEW + "NAME\n"
            + "Add: " + PREFIX_ADD + "NAME " + PREFIX_NOTES + "NOTES\n"
            + "Delete: " + PREFIX_DELETE + "NAME\n"
            + "Example: \n"
            + COMMAND_WORD + " " + PREFIX_VIEW + "John Doe\n"
            + COMMAND_WORD + " " + PREFIX_ADD + "John Doe " + PREFIX_NOTES + "Prefers email contact\n"
            + COMMAND_WORD + " " + PREFIX_DELETE + "John Doe";

    public static final String MESSAGE_VIEW_NOTES_SUCCESS = "Notes for %1$s: %2$s";
    public static final String MESSAGE_DELETE_NOTES_SUCCESS = "Deleted notes for %1$s";
    public static final String MESSAGE_ADD_NOTES_SUCCESS = "Added notes for %1$s: %2$s";
    public static final String MESSAGE_PERSON_NOT_FOUND = "No person found with name: %1$s";

    private final Name targetName;
    private final Mode mode;
    private final Notes newNotes;

    /**
     * Represents the different modes of operation for the NotesCommand.
     * VIEW - displays the notes of a person
     * ADD - adds or updates the notes of a person
     * DELETE - removes the notes of a person
     */
    public enum Mode {
        /**
         * Displays the notes of the specified person.
         */
        VIEW,

        /**
         * Adds or updates the notes of the specified person.
         */
        ADD,

        /**
         * Removes the notes of the specified person.
         */
        DELETE
    }

    /**
     * Creates command to view or delete notes.
     */
    public NotesCommand(Name targetName, Mode mode) {
        this(targetName, mode, null);
    }

    /**
     * Creates command to add notes.
     */
    public NotesCommand(Name targetName, Mode mode, Notes notes) {
        requireNonNull(targetName);
        requireNonNull(mode);
        this.targetName = targetName;
        this.mode = mode;
        this.newNotes = notes;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        Person personToEdit = null;
        for (Person person : lastShownList) {
            if (person.getName().equals(targetName)) {
                personToEdit = person;
                break;
            }
        }

        if (personToEdit == null) {
            throw new CommandException(String.format(MESSAGE_PERSON_NOT_FOUND, targetName.toString()));
        }

        switch (mode) {
        case VIEW:
            return new CommandResult(String.format(MESSAGE_VIEW_NOTES_SUCCESS,
                    personToEdit.getName(), personToEdit.getNotes().toString()));

        case DELETE:
            Person personWithDeletedNotes = new Person(personToEdit.getName(), personToEdit.getPhone(),
                    personToEdit.getEmail(), personToEdit.getAddress(), Notes.createEmpty(),
                    personToEdit.getTags(), personToEdit.getIncome(), personToEdit.getAge());
            model.setPerson(personToEdit, personWithDeletedNotes);
            return new CommandResult(String.format(MESSAGE_DELETE_NOTES_SUCCESS,
                    personToEdit.getName()));

        case ADD:
            Person personWithNewNotes = new Person(personToEdit.getName(), personToEdit.getPhone(),
                    personToEdit.getEmail(), personToEdit.getAddress(), newNotes,
                    personToEdit.getTags(), personToEdit.getIncome(), personToEdit.getAge());
            model.setPerson(personToEdit, personWithNewNotes);
            return new CommandResult(String.format(MESSAGE_ADD_NOTES_SUCCESS,
                    personToEdit.getName(), newNotes.toString()));

        default:
            throw new AssertionError("Unknown mode: " + mode);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof NotesCommand)) {
            return false;
        }

        NotesCommand otherNotesCommand = (NotesCommand) other;
        return targetName.equals(otherNotesCommand.targetName)
                && mode == otherNotesCommand.mode
                && (newNotes == null ? otherNotesCommand.newNotes == null
                        : newNotes.equals(otherNotesCommand.newNotes));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetName", targetName)
                .add("mode", mode)
                .add("newNotes", newNotes)
                .toString();
    }
}