package MVC;

import Domain.Abonat;
import Domain.ExemplarCarte;
import Repository.UnavailableException;
import Service.ManagerService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


public class ImprumutHandlerController {
    @FXML
    private TextField textFieldExemplar;
    @FXML
    private DatePicker datePickerStop;
    @FXML
    private DatePicker datePickerStart;

    private Abonat loggedInAbonat;
    private ExemplarCarte exemplarCarte;
    private Stage thisStage;
    private Stage parentStage;
    private ManagerService service;

    public void setService(ManagerService service, Stage parentStage, Stage thisStage, ExemplarCarte exemplar, Abonat loggedInAbonat) {
        this.service = service;
        this.parentStage = parentStage;
        this.thisStage = thisStage;
        this.exemplarCarte = exemplar;
        this.loggedInAbonat = loggedInAbonat;

        this.textFieldExemplar.setText(exemplar.toString());
        this.datePickerStart.setValue(LocalDate.now());
        this.datePickerStop.setValue(LocalDate.now().plusWeeks(2));
    }

    public void handleImprumuta(ActionEvent actionEvent) {
        try {
            Date start = Date.from(datePickerStart.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(datePickerStop.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            // verificarea disponibilitatii exemplarului se verifica in urmatorul layer.
            this.service.imprumuta(loggedInAbonat, exemplarCarte, start, end);
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
