package todolist;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import todolist.dataModel.ToDoData;
import todolist.dataModel.ToDoItem;
import java.time.LocalDate;

public class DialogController {

    @FXML
    private DatePicker deadlinePicker;
    @FXML
    private TextArea detailsDescriptionArea;
    @FXML
    private TextField shortDescriptionField;

    ToDoItem processResults(){

        String shortDescription = shortDescriptionField.getText().trim();
        String detailsDescription = detailsDescriptionArea.getText().trim();
        LocalDate deadlineDate = deadlinePicker.getValue();

        ToDoItem newItem = new ToDoItem(shortDescription,detailsDescription,deadlineDate);
        ToDoData.getInstance().addToDoItem(newItem);
        return newItem;
    }
}