package todolist.dataModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ToDoData {

    private static ToDoData instance = new ToDoData();
    private static String fileName = "todolist.txt";

    private ObservableList<ToDoItem> toDoItems;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static ToDoData getInstance() {
        return instance;
    }

    private ToDoData() {
    }


    public ObservableList<ToDoItem> getToDoItems() {
        return toDoItems;
    }


    public void addToDoItem(ToDoItem toDoItem) {
        toDoItems.add(toDoItem);
    }

    public void loadToDoItems() throws IOException {

        try (BufferedReader br = openFileForReading()) {
            String input;
            while ((input = br.readLine()) != null) {
                String[] itemPieces = input.split("\t");

                String shortDescription = itemPieces[0];
                String longDescription = itemPieces[1];
                String dateDescription = itemPieces[2];

                LocalDate date = LocalDate.parse(dateDescription, formatter);

                ToDoItem item = new ToDoItem(shortDescription, longDescription, date);
                toDoItems.add(item);
            }
        }
    }

    private BufferedReader openFileForReading() throws IOException {
        toDoItems = FXCollections.observableArrayList();
        Path path = Paths.get(fileName);
        return Files.newBufferedReader(path);
    }

    public void storeToDoItems() throws IOException {

        try (BufferedWriter bw = openFileForWriting()) {

            for (ToDoItem item : toDoItems) {
                bw.write(String.format("%s\t%s\t%s",
                        item.getShortDescription(),
                        item.getDetails(),
                        item.getDeadline().format(formatter)));
                bw.newLine();
            }
        }
    }

    private BufferedWriter openFileForWriting() throws IOException {
        Path path = Paths.get(fileName);
        return Files.newBufferedWriter(path);
    }

    public void deleteItem(ToDoItem item) {

        toDoItems.remove(item);
    }
}