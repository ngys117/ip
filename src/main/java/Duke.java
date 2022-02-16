import duke.exception.MissingDateException;
import duke.exception.MissingDescriptionException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.ToDo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Duke {
    private static void parseInput(Scanner in, Task[] tasks) {
        String line;
        System.out.println("Storing Up to 100 Tasks");
        do {
            System.out.println("Waiting for your input");
            line = in.nextLine();
            String[] words = line.split(" ", 2);
            String command = words[0];

            switch (command) {
            case "list":
                printTasks(tasks);
                break;
            case "mark":
                markTask(tasks, words);
                break;
            case "unmark":
                unmarkTask(tasks, words);
                break;
            case "todo":
                addToDo(tasks, words);
                break;
            case "deadline":
                addDeadline(tasks, words);
                break;
            case "event":
                addEvent(tasks, words);
                break;
            case "help":
                printHelp();
                break;
            default:
                if (!line.equalsIgnoreCase("bye")) {
                    System.out.println("Sorry, I did not understand that command. Input help to find out more");
                }
                break;
            }
        } while ((!line.equalsIgnoreCase("bye")) && Task.getNumOfTasks() < 100);
    }

    private static void addEvent(Task[] tasks, String[] words) {
        int numOfTasks = Task.getNumOfTasks();
        try {
            String description = extractDescription(words);
            String at = extractDate(words);
            tasks[numOfTasks] = new Event(description, at);
            System.out.println("Event added!");
            printTasks(tasks);
        } catch (MissingDescriptionException e) {
            System.out.println("Description cannot be empty. Correct format: event <description> /at <date>");
        } catch (MissingDateException e) {
            System.out.println("Date cannot be empty. Correct format: deadline <description> /by <date>");
        }
    }

    private static void addDeadline(Task[] tasks, String[] words) {
        int numOfTasks = Task.getNumOfTasks();
        try {
            String description = extractDescription(words);
            String by = extractDate(words);
            tasks[numOfTasks] = new Deadline(description, by);
            System.out.println("Deadline added!");
            printTasks(tasks);
        } catch (MissingDescriptionException e) {
            System.out.println("Description cannot be empty. Correct format: deadline <description> /by <date>");
        } catch (MissingDateException e) {
            System.out.println("Date cannot be empty. Correct format: deadline <description> /by <date>");
        }
    }

    private static void addToDo(Task[] tasks, String[] words) {
        int numOfTasks = Task.getNumOfTasks();
        try {
            String description = extractDescription(words);
            tasks[numOfTasks] = new ToDo(description);
            System.out.println("Todo added!");
            printTasks(tasks);
        } catch (MissingDescriptionException e) {
            System.out.println("Description cannot be empty. Correct format: todo <description>");
        }
    }

    private static void unmarkTask(Task[] tasks, String[] unmarkTaskParameters) {
        try {
            int taskNumber = Integer.parseInt(unmarkTaskParameters[1]);
            int taskIndex = taskNumber - 1;
            if (taskNumber > Task.getNumOfTasks()) {
                System.out.println("You don't have that many tasks ><!");
                return;
            }
            tasks[taskIndex].setUndone();
            System.out.println("I have marked the task as not done!");
            System.out.println(tasks[taskIndex].getStatusIcon() + tasks[taskIndex].getTaskDescription() + "\n");
        } catch (NumberFormatException nfe) {
            System.out.println("Please enter a number after unmark");
        }
    }

    private static void markTask(Task[] tasks, String[] markTaskParameters) {
        try {
            int taskNumber = Integer.parseInt(markTaskParameters[1]);
            int taskIndex = taskNumber - 1;
            if (taskNumber > Task.getNumOfTasks()) {
                System.out.println("You don't have that many tasks ><!");
                return;
            }
            tasks[taskIndex].setDone();
            System.out.println("I have marked the task as done!");
            System.out.println(tasks[taskIndex].getStatusIcon() + tasks[taskIndex].getTaskDescription() + "\n");
        } catch (NumberFormatException nfe) {
            System.out.println("Please enter a number after mark");
        }
    }

    private static void printTasks(Task[] tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < Task.getNumOfTasks(); i += 1) {
            System.out.println(i + 1 + ". " + tasks[i]);
        }
        System.out.println("You have " + Task.getNumOfTasks() + " task(s) on the list.\n");
    }

    private static void printWelcomeMessage() {
        System.out.println("____________________________________________________________\n"
                + "Hello, nice to meet you. I'm Yae! (*^▽^*)\n"
                + "What can I do for you?\n"
                + "____________________________________________________________");
    }
    
    private static String extractDescription(String[] words) throws MissingDescriptionException {
        if (words.length < 2) {
            throw new MissingDescriptionException();
        }
        String[] parameters = words[1].split(" /", 2);
        if (parameters[0].isBlank()) {
            throw new MissingDescriptionException();
        }
        return parameters[0];
    }

    private static String extractDate(String[] words) throws MissingDateException {
        String[] parameters = words[1].split(" /", 2);
        parameters = parameters[1].split(" ", 2);
        if (parameters.length < 2 || parameters[1].isBlank()) {
            throw new MissingDateException();
        }
        return parameters[1];
    }

    private static void printHelp() {
        System.out.println("Here are the list of commands:");
        System.out.println("list: Lists current tasks.");
        System.out.println("mark: Marks task as done. (e.g. mark <task number>)\n"
                + "unmark: Marks task as not done. (e.g. unmark <task number>)");
        System.out.println("todo: adds a todo. (e.g. todo <description>)");
        System.out.println("deadline: adds a deadline. (e.g. deadline <description> /by <date>)");
        System.out.println("event: adds an event. (e.g. event <description> /at <date>)");
    }

    private static void readSaveData(Task[] tasks) throws FileNotFoundException {
        String taskType;
        String taskDescription;
        boolean isDone;
        String date = "";
        File loadData = new File("data/duke.txt");
        Scanner loadDataScanner = new Scanner(loadData);
        while (loadDataScanner.hasNext()) {
            String line = loadDataScanner.nextLine();
            String[] words = line.split(" \\| ");
            taskType = words[0];
            isDone = words[1].equals("[X]");
            taskDescription = words[2];
            if (words.length > 3) {
                date = words[3];
            }
            loadData(tasks, taskType, isDone, taskDescription, date);
        }
    }

    private static void loadData(Task[] tasks, String command, boolean isDone, String description, String date) {
        int numOfTasks = Task.getNumOfTasks();
        switch(command) {
        case "todo":
            tasks[numOfTasks] = new ToDo(description);
            if (isDone) {
                tasks[numOfTasks].setDone();
            }
            break;
        case "deadline":
            tasks[numOfTasks] = new Deadline(description, date);
            if (isDone) {
                tasks[numOfTasks].setDone();
            }
            break;
        case "event":
            tasks[numOfTasks] = new Event(description, date);
            if (isDone) {
                tasks[numOfTasks].setDone();
            }
            break;
        default:
            System.out.println("Cannot load line");
        }
    }

    private static void saveData(Task[] tasks) throws IOException {
        createSaveDirectory();
        createSaveFile();
        FileWriter writer = new FileWriter("data/duke.txt", true);
        for (int i = 0; i < Task.getNumOfTasks(); i += 1) {
            writer.write(tasks[i].getTaskType() + " | " + tasks[i].getStatusIcon() + "| "
                    + tasks[i].getTaskDescription());
            if (tasks[i] instanceof Deadline || tasks[i] instanceof Event) {
                writer.write(" | " + tasks[i].getTime());
            }
            writer.write("\n");
        }
        writer.close();
    }

    private static void createSaveDirectory() {
        File saveDirectory = new File("data");
        if (!saveDirectory.exists()) {
            if (!saveDirectory.mkdir()) {
                System.out.println("Failed to create new directory.");
            }
        }
    }

    private static void createSaveFile() throws IOException {
        File saveFile = new File("data/duke.txt");
        saveFile.createNewFile();
    }

    public static void main(String[] args) {
        printWelcomeMessage();
        Scanner in = new Scanner(System.in);
        Task[] tasks = new Task[100];
        try {
            readSaveData(tasks);
        } catch (FileNotFoundException e) {
            System.out.println(("Save File not found."));
        }
        parseInput(in, tasks);
        try {
            saveData(tasks);
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
        System.out.println("Goodbye, see you next time!");
    }
}
