package MVC;

import Domain.Abonat;
import Domain.ExemplarCarte;
import Service.MasterService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;

public class ImprumutHandlerController {
    //private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
    private MasterService service;

    public void setService(MasterService service, Stage parentStage, Stage thisStage, ExemplarCarte exemplar, Abonat loggedInAbonat) {
        this.service = service;
        this.parentStage = parentStage;
        this.thisStage = thisStage;
        this.exemplarCarte = exemplar;
        this.loggedInAbonat = loggedInAbonat;

        this.textFieldExemplar.setText(exemplar.toString());
        this.datePickerStart.setValue(LocalDate.now());
        this.datePickerStart.setValue(LocalDate.now().plusWeeks(2));
    }

    public void handleInchiriaza(ActionEvent actionEvent) {
        this.service.imprumuta(loggedInAbonat, exemplarCarte, datePickerStart.getValue(), datePickerStop.getValue());
        this.parentStage.show();
        this.thisStage.close();
        CustomAlert.showMessage(this.thisStage, Alert.AlertType.CONFIRMATION, "confirmare", "Ati imprumutat cu succes exemplarul!");
    }

    public void handleBack(ActionEvent actionEvent) {
        this.parentStage.show();
        this.thisStage.close();
    }
}
