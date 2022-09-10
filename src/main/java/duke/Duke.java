package duke;

import duke.error.ErrorHandler;
import duke.error.exceptions.*;
import duke.io.InputValidator;
import duke.tasks.TaskList;
import duke.tasks.tasktypes.DeadlineTask;
import duke.tasks.tasktypes.EventTask;
import duke.tasks.tasktypes.ToDoTask;
import duke.ui.StringFormatting;
import duke.ui.UserInterface;

public class Duke {
    /* Command list to check against */
    public static final String COMMAND_DEADLINE = "deadline";
    public static final String COMMAND_EVENT = "event";
    public static final String COMMAND_TODO = "todo";
    public static final String COMMAND_TERMINATE = "bye";
    public static final String COMMAND_LIST = "list";
    public static final String COMMAND_MARK = "mark";
    public static final String COMMAND_UNMARK = "unmark";
    public static final String COMMAND_DELETE = "delete";

    /** TaskList class that contains all items */
    public static final TaskList TASK_LIST = new TaskList();

    public static void main(String[] args) {
        greet();
        programBody();
        exit();
    }

    /**
     * Body of the program. Runs in a loop until terminated.
     */
    public static void programBody() {
        while (true) {
            String input = UserInterface.getInput();
            try {
                if (InputValidator.isTerminatingInput(input)) {
                    break;
                } else if (InputValidator.isListInput(input)) {
                    printList();
                } else if (InputValidator.isMarkInput(input)) {
                    markAndConfirm(input);
                } else if (InputValidator.isUnmarkInput(input)) {
                    unmarkAndConfirm(input);
                } else if (InputValidator.isAddInput(input)) {
                    addAndConfirm(input);
                } else if (InputValidator.isDeleteInput(input)){
                    deleteAndConfirm(input);
                } else {
                    throw new NotRecognizedException(input);
                }
            } catch (DukeException e) {
                ErrorHandler.printErrorMessage(e);
            }
        }
    }

    /**
     * Marks item given in the input string.
     *
     * @param input input
     * @throws NotAnIntegerException  If word after command is not an integer.
     * @throws ItemNotFoundException  If there is no item at given index.
     * @throws NoStateChangeException If item is already marked.
     */
    private static void markAndConfirm(String input) throws
            NotAnIntegerException, ItemNotFoundException, NoStateChangeException {
        UserInterface.print(StringFormatting.formatMarkOrUnmarkString(
                TASK_LIST.getTextOfItem(markItem(input) - 1), true));
    }

    /**
     * Unmarks item given in the input string.
     *
     * @param input input
     * @throws NotAnIntegerException  If word after command is not an integer.
     * @throws ItemNotFoundException  If there is no item at given index.
     * @throws NoStateChangeException If item is already not marked.
     */
    private static void unmarkAndConfirm(String input) throws
            NotAnIntegerException, ItemNotFoundException, NoStateChangeException {
        UserInterface.print(StringFormatting.formatMarkOrUnmarkString(
                TASK_LIST.getTextOfItem(unmarkItem(input) - 1), false));
    }

    /**
     * Adds item to {@link Duke#TASK_LIST} and prints confirmation.
     *
     * @param input input string
     */
    private static void addAndConfirm(String input) {
        UserInterface.print(StringFormatting.formatAddString(
                TASK_LIST.getTextOfItem(addItem(input))) + "\n"
                + StringFormatting.formatNumberOfTasksString(
                        TASK_LIST.getItemCount()));
    }

    /**
     * Deletes item and prints confirmation.
     *
     * @param input input
     * @throws NotAnIntegerException  If word after command is not an integer.
     * @throws ItemNotFoundException  If there is no item at given index.
     */
    private static void deleteAndConfirm(String input) throws
            NotAnIntegerException, ItemNotFoundException {
        UserInterface.print(StringFormatting.formatDeleteString(
                deleteItem(input)) + "\n"
                + StringFormatting.formatNumberOfTasksString(
                TASK_LIST.getItemCount()));
    }

    /**
     * Print all tasks stored in {@link Duke#TASK_LIST}
     *
     * @throws ListEmptyException Exception thrown if list is empty
     */
    private static void printList() throws ListEmptyException {
        if (TASK_LIST.getItemCount() <= 0) {
            throw new ListEmptyException();
        }
        UserInterface.print(TASK_LIST.toString());
    }

    /**
     * Add type of item, depending on command. Defaults to {@link ToDoTask} item type.
     *
     * @param input input string to check for command words
     * @return <b>0-based</b> index of added item.
     */
    private static int addItem(String input) {
        String task = input.split(" ", 2)[1];
        if (InputValidator.stringContains(input, COMMAND_DEADLINE)) {
            return TASK_LIST.addItem(new DeadlineTask(task));
        } else if (InputValidator.stringContains(input, COMMAND_EVENT)) {
            return TASK_LIST.addItem(new EventTask(task));
        } else {
            return TASK_LIST.addItem(new ToDoTask(task));
        }
    }

    /**
     * Deletes item in list.
     * <br><b>NOTE: Operates on 1-based indexing logic</b>, but converts it
     * to 0-based indexing for {@link TaskList} class
     *
     * @param input input string to find index
     * @return String of deleted item
     * @throws NotAnIntegerException If word after command is not an integer
     * @throws ItemNotFoundException If item is already marked
     */
    private static String deleteItem(String input) throws NotAnIntegerException, ItemNotFoundException {
        int itemIndex = extractNumber(input) - 1;
        return TASK_LIST.deleteItem(itemIndex);
    }

    /**
     * Marks item in To-Do list as done.
     * <br><b>NOTE: Operates on 1-based indexing logic</b>, but converts it
     * to 0-based indexing for {@link TaskList} class
     *
     * @param input input string to find index
     * @return index of item <b>(1-based index)</b>
     * @throws NotAnIntegerException If word after command is not an integer
     * @throws ItemNotFoundException If item is already marked
     */
    private static int markItem(String input) throws NotAnIntegerException, ItemNotFoundException,
            NoStateChangeException {
        int itemIndex = extractNumber(input) - 1;
        TASK_LIST.markItem(itemIndex);
        return itemIndex + 1;
    }

    /**
     * Removes mark on item in To-Do list, marking it as undone.
     * <br><b>NOTE: Operates on 1-based indexing logic</b>, but converts it
     * to 0-based indexing for {@link TaskList} class
     *
     * @param input input string to find index
     * @return index of item <b>(1-based index)</b>
     * @throws NotAnIntegerException If word after command is not an integer
     * @throws ItemNotFoundException If item is already marked
     */
    private static int unmarkItem(String input) throws NotAnIntegerException, ItemNotFoundException,
            NoStateChangeException {
        int itemIndex = extractNumber(input) - 1;
        TASK_LIST.unmarkItem(itemIndex);
        return itemIndex + 1;
    }

    /**
     * Extracts a number from a given string.
     *
     * @param input input string
     * @return extracted integer
     * @throws NotAnIntegerException If word after command is not an integer
     */
    private static int extractNumber(String input) throws NotAnIntegerException {
        String number = input.split(" ")[1];
        String command = input.split(" ")[0];
        if (InputValidator.isInteger(number)) {
            return Integer.parseInt(number);
        } else {
            throw new NotAnIntegerException(command);
        }
    }

    /**
     * Prints a greeting for when the program is run
     */
    private static void greet() {
        UserInterface.print(StringFormatting.getGreeting());
    }

    /**
     * Prints a goodbye message for when the program is terminated via user commands
     */
    private static void exit() {
        UserInterface.print(StringFormatting.getGoodbye());
    }
}
