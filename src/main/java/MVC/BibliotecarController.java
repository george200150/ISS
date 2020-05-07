package MVC;

import Domain.Bibliotecar;
import Domain.Carte;
import Domain.ExemplarCarte;
import Domain.ExemplarCarteDTOWithStatus;
import Repository.OverdueError;
import Repository.UnavailableException;
import Service.MasterService;
import Utils.ExemplarStateChangeEvent;
import Utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class BibliotecarController implements Observer<ExemplarStateChangeEvent> {
    @FXML
    private TextField textFieldCodUnic;
    @FXML
    private TextField textFieldTitlu;
    @FXML
    private TextField textFieldISBN;
    @FXML
    private TextField textFieldAutor;
    @FXML
    private TextField textFieldEditura;
    @FXML
    private TextField textFieldAnAparitie;
    @FXML
    private TextField textFieldCodAbonat;
    @FXML
    private TextField textFieldCodExemplar;
    @FXML
    private TextField textFieldStatus;

    @FXML
    private Label labelBibliotecar;

    @FXML
    private TableView<ExemplarCarteDTOWithStatus> tableExemplareBibliotecar;
    @FXML
    private TableColumn<ExemplarCarteDTOWithStatus, String> tableExemplareBibliotecarColumnCodUnic;
    @FXML
    private TableColumn<ExemplarCarteDTOWithStatus, String> tableExemplareBibliotecarColumnTitlu;
    @FXML
    private TableColumn<ExemplarCarteDTOWithStatus, String> tableExemplareBibliotecarColumnISBN;
    @FXML
    private TableColumn<ExemplarCarteDTOWithStatus, String> tableExemplareBibliotecarColumnAutor;
    @FXML
    private TableColumn<ExemplarCarteDTOWithStatus, String> tableExemplareBibliotecarColumnEditura;
    @FXML
    private TableColumn<ExemplarCarteDTOWithStatus, String> tableExemplareBibliotecarColumnAnAparitie;
    @FXML
    private TableColumn<ExemplarCarteDTOWithStatus, String> tableExemplareBibliotecarColumnStatus;


    private Stage dialogStage;
    private Bibliotecar loggedInBibliotecar;
    private MasterService service;

    private ObservableList<ExemplarCarteDTOWithStatus> model = FXCollections.observableArrayList();


    public void handleOpereaza(ActionEvent actionEvent){ // generic method for any CRUD operation
        /**
         * "tipOperatie" specifies the following:
         * INSERT: all fields from the "exemplar" object are necessary. - just insert
         * SELECT: we need only the id from the "exemplar" object. - search by id and return the found object
         * UPDATE: all fields from the "exemplar" object are necessary. - search by id and update the found object
         * DELETE: we need only the id from the "exemplar" object. - search by id and delete the found object
         */
        int id = Integer.parseInt(textFieldCodUnic.getText());
        String titlu = textFieldTitlu.getText();
        String ISBN = textFieldISBN.getText();
        String autor = textFieldAutor.getText();
        String editura = textFieldEditura.getText();
        int anAparitie = Integer.parseInt(textFieldAnAparitie.getText());
        Carte carte = new Carte(titlu,ISBN,autor,editura,anAparitie);
        ExemplarCarte exemplar = new ExemplarCarte(id,carte);
        String tipOperatie = "INSERT/SELECT/UPDATE/DELETE";
        Button pressedButton = (Button) actionEvent.getSource();
        String text = pressedButton.getText(); // would work better with getId()
        switch (text) {
            case "Adauga":
                tipOperatie = "INSERT";
                break;
            case "Modifica":
                tipOperatie = "UPDATE";
                break;
            case "Sterge":
                tipOperatie = "DELETE";
                break;
        }
        this.service.opereaza(exemplar, tipOperatie); // may throw
        this.service.getAllExemplareExistente(); // get updated the state of the database
    }


    public void setService(MasterService masterService, Stage dialogStage, Bibliotecar loggedInBibliotecar) {
        this.dialogStage = dialogStage;
        this.loggedInBibliotecar = loggedInBibliotecar;
        this.service = masterService;
        service.addObserver(this);
        initModel();
        this.labelBibliotecar.setText(loggedInBibliotecar.toString());
    }

    private void initModel() {
        Iterable<ExemplarCarte> grades = service.getAllExemplareExistente(); // toate exemplarele se vor afisa.
        List<ExemplarCarte> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .collect(Collectors.toList());

        model.setAll(convertGradeToDTO(gradeList));
    }

    @FXML
    public void initialize() {
        tableExemplareBibliotecarColumnCodUnic.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTOWithStatus, String>("codUnic"));
        tableExemplareBibliotecarColumnTitlu.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTOWithStatus, String>("titlu"));
        tableExemplareBibliotecarColumnISBN.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTOWithStatus, String>("ISBN"));
        tableExemplareBibliotecarColumnAutor.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTOWithStatus, String>("autor"));
        tableExemplareBibliotecarColumnEditura.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTOWithStatus, String>("editura"));
        tableExemplareBibliotecarColumnAnAparitie.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTOWithStatus, String>("anAparitie"));
        tableExemplareBibliotecarColumnStatus.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTOWithStatus, String>("status"));

        tableExemplareBibliotecar.setItems(model);
    }

    private List<ExemplarCarteDTOWithStatus> convertGradeToDTO(List<ExemplarCarte> gradeList) {
        return gradeList.stream()
                .map(exem -> new ExemplarCarteDTOWithStatus(exem.getCodUnic(), exem.getRefer(), this.service.esteExemplarInchiriat(exem)))
                .collect(Collectors.toList());
    }


    public void handleMoreDetails(MouseEvent mouseEvent) {
        ExemplarCarteDTOWithStatus dto = this.tableExemplareBibliotecar.getSelectionModel().getSelectedItem();
        if(dto != null){
            this.textFieldCodUnic.setText(Integer.toString(dto.getCodUnic()));
            this.textFieldTitlu.setText(dto.getTitlu());
            this.textFieldISBN.setText(dto.getISBN());
            this.textFieldAutor.setText(dto.getAutor());
            this.textFieldEditura.setText(dto.getEditura());
            this.textFieldAnAparitie.setText(Integer.toString(dto.getAnAparitie()));
            this.textFieldStatus.setText(dto.getStatus());
        }
    }


    public void handleReturneaza(ActionEvent actionEvent) {
        try {
            int codAbonat = Integer.parseInt(this.textFieldCodAbonat.getText());
            int codExemplar = Integer.parseInt(this.textFieldCodExemplar.getText());
            Date now = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
            this.service.returneaza(this.loggedInBibliotecar,codAbonat, codExemplar, now); //if return deadline is overdue, then compute penalties
        }
        catch (NumberFormatException ignored){
            CustomAlert.showErrorMessage(null, "Nu ati introdus corespunzator codurile de identificare!");
        }
        catch (OverdueError over){
            CustomAlert.showErrorMessage(null, over.getMessage());
        }
        catch (UnavailableException unavailable){
            CustomAlert.showErrorMessage(null, unavailable.getMessage());
        }
    }

    @Override
    public void update(ExemplarStateChangeEvent exemplarStateChangeEvent) {
        initModel();
    }

}
