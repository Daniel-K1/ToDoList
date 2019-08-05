package todolist;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import todolist.dataModel.ToDoData;
import todolist.dataModel.ToDoItem;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    private List<ToDoItem> todoItems;

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

    public void initialize() {

        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                                       @Override
                                       public void handle(ActionEvent actionEvent) {
                                           ToDoItem item = listView.getSelectionModel().getSelectedItem();
                                           deleteItem(item);
                                       }
                                   }
        );

        MenuItem dummyMenuItem = new MenuItem("Dumy");

        listContextMenu.getItems().addAll(deleteMenuItem);
        listContextMenu.getItems().addAll(dummyMenuItem);

        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observableValue, ToDoItem toDoItem, ToDoItem t1) {
                if (t1 != null) {
                    textArea.setText(t1.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd MMMM YYYY (EEEE)");
                    dueLabel.setText(df.format(t1.getDeadline()));
                }
            }
        });

        giveMeAllItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem toDoItem) {
                return true;
            }
        };

        giveMeTodaysItems = new Predicate<ToDoItem>() {
            @Override
            public boolean test(ToDoItem toDoItem) {
                return toDoItem.getDeadline().isEqual(LocalDate.now());
            }
        };

        filteredList = new FilteredList<ToDoItem>(ToDoData.getInstance().getToDoItems(), giveMeAllItems);

        SortedList<ToDoItem> sortedList = new SortedList<ToDoItem>(filteredList,
                new Comparator<ToDoItem>() {
                    @Override
                    public int compare(ToDoItem o1, ToDoItem o2) {

                        return o1.getDeadline().compareTo(o2.getDeadline());
                    }
                });

        listView.setItems(sortedList);

        // listView.setItems(ToDoData.getInstance().getToDoItems());
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.getSelectionModel().selectFirst();

        listView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {
                ListCell<ToDoItem> cell = new ListCell<ToDoItem>() {
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
        fxmlLoader.setLocation(getClass().getResource("ToDoItemDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Can't load the dialog");
            System.out.println(e.getStackTrace());
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
    public void actionToggleButton(ActionEvent actionEvent) {

        ToDoItem selectedItem = listView.getSelectionModel().getSelectedItem();

        if (filterToggleButton.isSelected()) {
            filteredList.setPredicate(giveMeTodaysItems);
            if(filteredList.isEmpty()){
                textArea.clear();
                dueLabel.setText("");
            }else if(filteredList.contains(selectedItem)) {
                listView.getSelectionModel().select(selectedItem);
            }else {
                listView.getSelectionModel().selectFirst();
            }

        } else {
            filteredList.setPredicate(giveMeAllItems);
        }
    }

    @FXML
    public void handleExitMenuItem(ActionEvent actionEvent) {

        Platform.exit();
    }
}