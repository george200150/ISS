package MVC;

import Domain.iss.Subscriber;
import Domain.iss.BookCopy;
import Repository.UnavailableException;
import Service.LibraryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


public class HiringHandlerController {
    @FXML
    private TextField textFieldExemplar;
    @FXML
    private DatePicker datePickerStop;
    @FXML
    private DatePicker datePickerStart;

    private Subscriber loggedInSubscriber;
    private BookCopy bookCopy;
    private Stage thisStage;
    private Stage parentStage;
    private LibraryService service;

    public void setService(LibraryService service, Stage parentStage, Stage thisStage, BookCopy exemplar, Subscriber loggedInSubscriber) {
        this.service = service;
        this.parentStage = parentStage;
        this.thisStage = thisStage;
        this.bookCopy = exemplar;
        this.loggedInSubscriber = loggedInSubscriber;

        this.textFieldExemplar.setText(exemplar.toString());
        this.datePickerStart.setValue(LocalDate.now());
        this.datePickerStop.setValue(LocalDate.now().plusWeeks(2));
    }

    public void handleHire(ActionEvent actionEvent) {
        try {
            Date start = Date.from(datePickerStart.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(datePickerStop.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            // check copy availability in next application layer
            this.service.hireCopy(loggedInSubscriber, bookCopy, start, end);
            this.parentStage.show();
            this.thisStage.close();
            CustomAlert.showMessage(this.thisStage, Alert.AlertType.CONFIRMATION, "confirmare", "Ati imprumutat cu succes exemplarul!");
        }
        catch (UnavailableException unavX){
            CustomAlert.showErrorMessage(this.thisStage, unavX.getMessage());
        }
    }

    public void handleBack(ActionEvent actionEvent) {
        this.parentStage.show();
        this.thisStage.close();
    }
}
