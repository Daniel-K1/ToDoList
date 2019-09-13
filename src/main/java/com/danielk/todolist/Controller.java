package com.danielk.todolist;

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import com.danielk.todolist.dataModel.ToDoData;
import com.danielk.todolist.dataModel.ToDoItem;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Controller {

    @FXML
    private ListView<ToDoItem> listView;
    @FXML
    private TextArea textArea;
    @FXML
    private Label dueLabel;
    @FXML
    private BorderPane borderPaneMain;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterToggleButton;
    private FilteredList<ToDoItem> filteredList;
    private Predicate<ToDoItem> giveMeAllItems;
    private Predicate<ToDoItem> giveMeTodaysItems;

    private final Logger LOG = LoggerFactory.getLogger(Controller.class);

    public void initialize() {

        addContextMenu();
        supplyTextAreaWithData();
        applyFiltering();

    }

    private void addContextMenu() {

        listContextMenu = new ContextMenu();
        listContextMenu.getItems().addAll(addDeleteMenuItem());
        listContextMenu.getItems().addAll(addDummyMenuItem());
    }

    private MenuItem addDeleteMenuItem() {
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(actionEvent -> {
                    ToDoItem item = listView.getSelectionModel().getSelectedItem();
                    deleteItem(item);
                }
        );
        return deleteMenuItem;
    }

    private MenuItem addDummyMenuItem() {
        return new MenuItem("Dumy");
    }


    private void applyFiltering() {
        giveMeAllItems = toDoItem -> true;
        giveMeTodaysItems = toDoItem -> toDoItem.getDeadline().isEqual(LocalDate.now());

        filteredList = new FilteredList<>(ToDoData.getInstance().getToDoItems(), giveMeAllItems);

        SortedList<ToDoItem> sortedList = new SortedList<>(filteredList,
                Comparator.comparing(ToDoItem::getDeadline));

        listView.setItems(sortedList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.getSelectionModel().selectFirst();

        listView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {
                ListCell<ToDoItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(ToDoItem toDoItem, boolean b) {
                        super.updateItem(toDoItem, b);
                        if (b) {
                            setText(null);
                        } else {
                            setText(toDoItem.getShortDescription());
                            if (toDoItem.getDeadline().equals(LocalDate.now())) {
                                setTextFill(Color.GREEN);
                            }

                            if (toDoItem.getDeadline().isBefore(LocalDate.now())) {
                                setTextFill(Color.RED);
                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        });

                return cell;
            }
        });

    }

    private void supplyTextAreaWithData() {
        listView.getSelectionModel().selectedItemProperty().addListener((observableValue, toDoItem, t1) -> {
            if (t1 != null) {
                textArea.setText(t1.getDetails());
                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd MMMM YYYY (EEEE)");
                dueLabel.setText(df.format(t1.getDeadline()));
            }
        });
    }


    private void deleteItem(ToDoItem item) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete item");
        alert.setHeaderText("Deleting item: " + item.getShortDescription());
        alert.setContentText("Are you sure?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            ToDoData.getInstance().deleteItem(item);
        }
    }

    @FXML
    public void getDialogPane() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(borderPaneMain.getScene().getWindow());
        dialog.setTitle("Add new task");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/ToDoItemDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            LOG.error("Can't load the dialog" + e.getMessage(),e);
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            DialogController controller = fxmlLoader.getController();
            ToDoItem newItem = controller.processResults();
            listView.getSelectionModel().select(newItem);
        }
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        ToDoItem item = listView.getSelectionModel().getSelectedItem();

        if (keyEvent.getCode().equals(KeyCode.DELETE)) {
            deleteItem(item);
        }

    }

    @FXML
    public void actionToggleButton() {

        ToDoItem selectedItem = listView.getSelectionModel().getSelectedItem();

        if (filterToggleButton.isSelected()) {
            filteredList.setPredicate(giveMeTodaysItems);
            if (filteredList.isEmpty()) {
                textArea.clear();
                dueLabel.setText("");
            } else if (filteredList.contains(selectedItem)) {
                listView.getSelectionModel().select(selectedItem);
            } else {
                listView.getSelectionModel().selectFirst();
            }

        } else {
            filteredList.setPredicate(giveMeAllItems);
        }
    }

    @FXML
    public void handleExitMenuItem() {
        Platform.exit();
    }
}