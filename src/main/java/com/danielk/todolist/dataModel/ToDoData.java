package com.danielk.todolist.dataModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private final Logger LOG = LogManager.getLogger();


    private ToDoData() {

    }


    public ObservableList<ToDoItem> getToDoItems() {
        return toDoItems;
    }


    public void addToDoItem(ToDoItem toDoItem) {
        toDoItems.add(toDoItem);
    }

    public void loadToDoItems() {

        try (BufferedReader br = openFileForReading()) {
            loadItems(br);
        } catch (IOException e) {

            LOG.warn("IO Exception while loading data from file: "+e.getMessage());
            LOG.warn("empty database file: 'todolist.txt' will be created");
        }
    }

    private BufferedReader openFileForReading() throws IOException {
        toDoItems = FXCollections.observableArrayList();
        Path path = Paths.get(fileName);
        return Files.newBufferedReader(path);
    }

    private void loadItems(BufferedReader br) throws IOException {
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

    public void storeToDoItems() {

        try (BufferedWriter bw = openFileForWriting()) {
            storeItems(bw);
        } catch (IOException e) {
            LOG.error("IO Exception while saving data to a file: " + e.getMessage());
        }
    }

    private BufferedWriter openFileForWriting() throws IOException {
        Path path = Paths.get(fileName);
        return Files.newBufferedWriter(path);
    }

    private void storeItems(BufferedWriter bw) throws IOException {
        for (ToDoItem item : toDoItems) {
            bw.write(String.format("%s\t%s\t%s",
                    item.getShortDescription(),
                    item.getDetails(),
                    item.getDeadline().format(formatter)));
            bw.newLine();
        }
    }

    public void deleteItem(ToDoItem item) {

        toDoItems.remove(item);
    }
}