package com.danielk.todolist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.danielk.todolist.dataModel.ToDoData;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/mainWindow.fxml"));
        primaryStage.setTitle("ToDo List");
        primaryStage.setScene(new Scene(root, 900, 500));
        primaryStage.show();
    }

    @Override
    public void stop() {

        ToDoData.getInstance().storeToDoItems();
    }

    @Override
    public void init() {

        ToDoData.getInstance().loadToDoItems();
    }

    public static void main(String[] args) {
        launch(args);
    }
}