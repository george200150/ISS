package MVC;

import Domain.Abonat;
import Domain.Carte;
import Domain.ExemplarCarte;
import Service.MasterService;
import Utils.ExemplarStateChangeEvent;
import Utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class ExemplarCarteDTO{
    private int codUnic;
    private Carte refer;
    private ExemplarCarte exemplarCarte;

    private String titlu;
    private String ISBN;
    private String autor;
    private String editura;
    private int anAparitie;

    public ExemplarCarteDTO(int codUnic, Carte refer) {
        this.codUnic = codUnic;
        this.refer = refer;
        this.exemplarCarte = new ExemplarCarte(codUnic, refer);

        this.titlu = refer.getTitlu();
        this.ISBN = refer.getISBN();
        this.autor = refer.getAutor();
        this.editura = refer.getEditura();
        this.anAparitie = refer.getAnAparitie();
    }
    public ExemplarCarte getExemplarCarte() { return exemplarCarte; }
    public void setExemplarCarte(ExemplarCarte exemplarCarte) { this.exemplarCarte = exemplarCarte; }
    public int getCodUnic() { return codUnic; }
    public void setCodUnic(int codUnic) { this.codUnic = codUnic; }
    public Carte getRefer() { return refer; }
    public void setRefer(Carte refer) { this.refer = refer; }
    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }
    public String getISBN() { return ISBN; }
    public void setISBN(String ISBN) { this.ISBN = ISBN; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getEditura() { return editura; }
    public void setEditura(String editura) { this.editura = editura; }
    public int getAnAparitie() { return anAparitie; }
    public void setAnAparitie(int anAparitie) { this.anAparitie = anAparitie; }
    @Override
    public String toString() { return "codUnic=" + codUnic + ", titlu='" + titlu + ", ISBN='" + ISBN + ", autor='" + autor + ", editura='" + editura + ", anAparitie=" + anAparitie; }
}


public class AbonatController implements Observer<ExemplarStateChangeEvent> {

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
    private Label labelAbonat;

    @FXML
    private TableView<ExemplarCarteDTO> tableExemplareAbonat;

    @FXML
    private TableColumn<ExemplarCarteDTO, String> tableExemplareAbonatColumnTitlu;
    @FXML
    private TableColumn<ExemplarCarteDTO, String> tableExemplareAbonatColumnISBN;
    @FXML
    private TableColumn<ExemplarCarteDTO, String> tableExemplareAbonatColumnAutor;
    @FXML
    private TableColumn<ExemplarCarteDTO, String> tableExemplareAbonatColumnEditura;
    @FXML
    private TableColumn<ExemplarCarteDTO, String> tableExemplareAbonatColumnAnAparitie;

    private Stage dialogStage;
    private Abonat loggedInAbonat;
    private MasterService service;

    private ObservableList<ExemplarCarteDTO> model = FXCollections.observableArrayList();

    public void setService(MasterService masterService, Stage stage, Abonat loggedInAbonat) {
        this.dialogStage = stage;
        this.loggedInAbonat = loggedInAbonat;
        service = masterService;
        service.addObserver(this);
        initModel();
        this.labelAbonat.setText(loggedInAbonat.toString());
    }

    private void initModel() {

        Iterable<ExemplarCarte> grades = service.getAllExemplareDisponibile(); //TODO: doar exemplarele ce pot fi inchiriate se vor afisa.
        List<ExemplarCarte> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .collect(Collectors.toList());

        model.setAll(convertGradeToDTO(gradeList));
    }

    @FXML
    public void initialize() {
        tableExemplareAbonatColumnTitlu.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTO, String>("titlu"));
        tableExemplareAbonatColumnISBN.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTO, String>("ISBN"));
        tableExemplareAbonatColumnAutor.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTO, String>("autor"));
        tableExemplareAbonatColumnEditura.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTO, String>("editura"));
        tableExemplareAbonatColumnAnAparitie.setCellValueFactory(new PropertyValueFactory<ExemplarCarteDTO, String>("anAparitie"));

        tableExemplareAbonat.setItems(model);
    }

    private List<ExemplarCarteDTO> convertGradeToDTO(List<ExemplarCarte> gradeList) {
        return gradeList.stream()
                .map(exem -> new ExemplarCarteDTO(exem.getCodUnic(), exem.getRefer()))
                .collect(Collectors.toList());
    }


    public void handleMoreDetails(MouseEvent mouseEvent) {
        ExemplarCarteDTO dto = this.tableExemplareAbonat.getSelectionModel().getSelectedItem(); // TODO: dto must a @NotNull object (hopefully...)
        this.textFieldTitlu.setText(dto.getTitlu());
        this.textFieldISBN.setText(dto.getISBN());
        this.textFieldAutor.setText(dto.getAutor());
        this.textFieldEditura.setText(dto.getEditura());
        this.textFieldAnAparitie.setText(Integer.toString(dto.getAnAparitie()));
    }


    public void handleImprumuta(ActionEvent actionEvent) {
        ExemplarCarteDTO dto = this.tableExemplareAbonat.getSelectionModel().getSelectedItem();
        // open new window for special hiring form
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("imprumut.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Log In Abonat");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            ImprumutHandlerController imprumutHandlerController = loader.getController();
            imprumutHandlerController.setService(this.service, this.dialogStage, dialogStage, dto, this.loggedInAbonat);

            this.dialogStage.hide(); // TODO: hide here and show in opened window on close.
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(ExemplarStateChangeEvent exemplarStateChangeEvent) {
        initModel();
    }
}
