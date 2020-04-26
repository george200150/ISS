package MVC;

import Domain.Bibliotecar;
import Domain.ExemplarCarte;
import Service.MasterService;
import Utils.ExemplarStateChangeEvent;
import Utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BibliotecarController implements Observer<ExemplarStateChangeEvent> {
    @FXML
    public TextField textFieldCodUnic;
    @FXML
    public TextField textFieldTitlu;
    @FXML
    public TextField textFieldISBN;
    @FXML
    public TextField textFieldAutor;
    @FXML
    public TextField textFieldEditura;
    @FXML
    public TextField textFieldAnAparitie;
    @FXML
    public TextField textFieldCodAbonat;
    @FXML
    public TextField textFieldCodExemplar;

    @FXML
    private Label labelBibliotecar;

    @FXML
    private TableView<ExemplarCarteDTO> tableExemplareBibliotecar;
    @FXML
    private TableColumn<ExemplarCarteDTO, String> tableExemplareBibliotecarColumnCodUnic;
    @FXML
    private TableColumn<ExemplarCarteDTO, String> tableExemplareBibliotecarColumnTitlu;
    @FXML
    private TableColumn<ExemplarCarteDTO, String> tableExemplareBibliotecarColumnISBN;
    @FXML
    private TableColumn<ExemplarCarteDTO, String> tableExemplareBibliotecarColumnAutor;
    @FXML
    private TableColumn<ExemplarCarteDTO, String> tableExemplareBibliotecarColumnEditura;
    @FXML
    private TableColumn<ExemplarCarteDTO, String> tableExemplareBibliotecarColumnAnAparitie;


    private Stage dialogStage;
    private Bibliotecar loggedInBibliotecar;
    private MasterService service;

    private ObservableList<ExemplarCarteDTO> model = FXCollections.observableArrayList();


    public void setService(MasterService masterService, Stage dialogStage, Bibliotecar loggedInBibliotecar) {
        this.dialogStage = dialogStage;
        this.loggedInBibliotecar = loggedInBibliotecar;
        this.service = masterService;
        service.addObserver(this);
        initModel();
        this.labelBibliotecar.setText(loggedInBibliotecar.toString());
    }

    private void initModel() {

        Iterable<ExemplarCarte> grades = service.getAllExemplareDisponibile(); //TODO: toate exemplarele se vor afisa.
        List<ExemplarCarte> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .collect(Collectors.toList());

        model.setAll(convertGradeToDTO(gradeList));
    }

    @FXML
    public void initialize() {
        tableExemplareBibliotecarColumnCodUnic.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTO, String>("codUnic"));
        tableExemplareBibliotecarColumnTitlu.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTO, String>("titlu"));
        tableExemplareBibliotecarColumnISBN.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTO, String>("ISBN"));
        tableExemplareBibliotecarColumnAutor.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTO, String>("autor"));
        tableExemplareBibliotecarColumnEditura.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTO, String>("editura"));
        tableExemplareBibliotecarColumnAnAparitie.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTO, String>("anAparitie"));

        tableExemplareBibliotecar.setItems(model);
    }

    private List<ExemplarCarteDTO> convertGradeToDTO(List<ExemplarCarte> gradeList) {
        return gradeList.stream()
                .map(exem -> { return new ExemplarCarteDTO(exem.getCodUnic(), exem.getRefer()); })
                .collect(Collectors.toList());
    }


    public void handleMoreDetails(MouseEvent mouseEvent) {
        ExemplarCarteDTO dto = this.tableExemplareBibliotecar.getSelectionModel().getSelectedItem(); // TODO: dto must a @NotNull object (hopefully...)
        this.textFieldCodUnic.setText(Integer.toString(dto.getCodUnic()));
        this.textFieldTitlu.setText(dto.getTitlu());
        this.textFieldISBN.setText(dto.getISBN());
        this.textFieldAutor.setText(dto.getAutor());
        this.textFieldEditura.setText(dto.getEditura());
        this.textFieldAnAparitie.setText(Integer.toString(dto.getAnAparitie()));
    }

//    private ExemplarCarte getFromDTO(ExemplarCarteDTO dto){
//        return new ExemplarCarte(dto.getCodUnic(),dto.getRefer());
//    }

    public void handleReturneaza(ActionEvent actionEvent) {
        //ExemplarCarteDTO dto = this.tableExemplareBibliotecar.getSelectionModel().getSelectedItem();
        int codAbonat = Integer.parseInt(this.textFieldCodAbonat.getText());
        int codExemplar = Integer.parseInt(this.textFieldCodExemplar.getText());
        this.service.returneaza(this.loggedInBibliotecar,codAbonat, codExemplar, LocalDate.now()); // TODO: check if deadline is not overdue
    }

    @Override
    public void update(ExemplarStateChangeEvent exemplarStateChangeEvent) {
        initModel();
    }
}
