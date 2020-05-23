package MVC;

import Domain.iss.Book;
import Domain.iss.BookCopy;
import Domain.iss.Librarian;
import Domain.iss.BookCopyDTOWithStatus;
import Repository.OverdueError;
import Repository.UnavailableException;
import Repository.ValidationException;
import Service.LibraryService;
import Utils.BookCopyStateChangeEvent;
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


public class LibrarianController extends EmployeeController implements Observer<BookCopyStateChangeEvent> {
    @FXML private TextField textFieldCodUnic;
    @FXML private TextField textFieldTitlu;
    @FXML private TextField textFieldISBN;
    @FXML private TextField textFieldAutor;
    @FXML private TextField textFieldEditura;
    @FXML private TextField textFieldAnAparitie;
    @FXML private TextField textFieldCodAbonat;
    @FXML private TextField textFieldCodExemplar;
    @FXML private TextField textFieldStatus;
    @FXML private Label labelBibliotecar;
    @FXML private TableView<BookCopyDTOWithStatus> tableExemplareBibliotecar;
    @FXML private TableColumn<BookCopyDTOWithStatus, String> tableExemplareBibliotecarColumnCodUnic;
    @FXML private TableColumn<BookCopyDTOWithStatus, String> tableExemplareBibliotecarColumnTitlu;
    @FXML private TableColumn<BookCopyDTOWithStatus, String> tableExemplareBibliotecarColumnISBN;
    @FXML private TableColumn<BookCopyDTOWithStatus, String> tableExemplareBibliotecarColumnAutor;
    @FXML private TableColumn<BookCopyDTOWithStatus, String> tableExemplareBibliotecarColumnEditura;
    @FXML private TableColumn<BookCopyDTOWithStatus, String> tableExemplareBibliotecarColumnAnAparitie;
    @FXML private TableColumn<BookCopyDTOWithStatus, String> tableExemplareBibliotecarColumnStatus;
    private Stage dialogStage;
    private Librarian loggedInLibrarian;
    private LibraryService service;
    private ObservableList<BookCopyDTOWithStatus> model = FXCollections.observableArrayList();

    /**
     * "operationType" specifies the following:
     * INSERT: all fields from the "exemplar" object are necessary. - just insert
     * SELECT: we need only the id from the "exemplar" object. - search by id and return the found object
     * UPDATE: all fields from the "exemplar" object are necessary. - search by id and update the found object
     * DELETE: we need only the id from the "exemplar" object. - search by id and delete the found object
     */
    public void handleOperate(ActionEvent actionEvent) { // generic method for any CRUD operation
        try {
            int id = Integer.parseInt(textFieldCodUnic.getText()); // the new unique code (id) of the bookCopy
            String titlu = textFieldTitlu.getText();
            String ISBN = textFieldISBN.getText();
            String autor = textFieldAutor.getText();
            String editura = textFieldEditura.getText();
            int anAparitie = Integer.parseInt(textFieldAnAparitie.getText());
            Book book = new Book(titlu, ISBN, autor, editura, anAparitie);
            String operationType = "INSERT/SELECT/UPDATE/DELETE";
            Button pressedButton = (Button) actionEvent.getSource();
            String text = pressedButton.getText(); // would work better with getId()
            switch (text) {
                case "Adauga":
                    operationType = "INSERT";
                    break;
                case "Modifica":
                    operationType = "UPDATE";
                    break;
                case "Sterge":
                    operationType = "DELETE";
                    break;
            }
            this.service.operate(id, book, operationType); // may throw
            this.service.getAllExistingCopies(); // get updated the state of the database
            CustomAlert.showMessage(this.dialogStage, Alert.AlertType.CONFIRMATION, "Succes!", text + "re efectuata cu succes!");
        } catch (ValidationException | UnavailableException ex) {
            CustomAlert.showErrorMessage(this.dialogStage, ex.getMessage());
        } catch (NumberFormatException nex) {
            CustomAlert.showErrorMessage(this.dialogStage, "Introduceti corect datele pentru operatie!");
        }
    }

    public void setService(LibraryService libraryService, Stage dialogStage, Librarian loggedInLibrarian) {
        this.dialogStage = dialogStage;
        this.loggedInLibrarian = loggedInLibrarian;
        this.service = libraryService;
        service.addObserver(this);
        initModel();
        this.labelBibliotecar.setText(loggedInLibrarian.toString());
    }

    private void initModel() {
        Iterable<BookCopy> grades = service.getAllExistingCopies(); // all the copies will be displayed
        List<BookCopy> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(convertGradeToDTO(gradeList));
    }

    @FXML
    public void initialize() {
        tableExemplareBibliotecarColumnCodUnic.setCellValueFactory(new PropertyValueFactory<BookCopyDTOWithStatus, String>("codUnic"));
        tableExemplareBibliotecarColumnTitlu.setCellValueFactory(new PropertyValueFactory<BookCopyDTOWithStatus, String>("titlu"));
        tableExemplareBibliotecarColumnISBN.setCellValueFactory(new PropertyValueFactory<BookCopyDTOWithStatus, String>("ISBN"));
        tableExemplareBibliotecarColumnAutor.setCellValueFactory(new PropertyValueFactory<BookCopyDTOWithStatus, String>("autor"));
        tableExemplareBibliotecarColumnEditura.setCellValueFactory(new PropertyValueFactory<BookCopyDTOWithStatus, String>("editura"));
        tableExemplareBibliotecarColumnAnAparitie.setCellValueFactory(new PropertyValueFactory<BookCopyDTOWithStatus, String>("anAparitie"));
        tableExemplareBibliotecarColumnStatus.setCellValueFactory(new PropertyValueFactory<BookCopyDTOWithStatus, String>("status"));
        tableExemplareBibliotecar.setItems(model);
    }

    private List<BookCopyDTOWithStatus> convertGradeToDTO(List<BookCopy> gradeList) {
        return gradeList.stream()
                .map(exem -> {Book found = this.service.findBookById(exem.getRefer()); return new BookCopyDTOWithStatus(exem.getCodUnic(), found, this.service.isHiredCopy(exem.getCodUnic()));})
                .collect(Collectors.toList());
    }

    public void handleMoreDetails(MouseEvent mouseEvent) {
        BookCopyDTOWithStatus dto = this.tableExemplareBibliotecar.getSelectionModel().getSelectedItem();
        if (dto != null) {
            this.textFieldCodUnic.setText(Integer.toString(dto.getCodUnic()));
            this.textFieldTitlu.setText(dto.getTitlu());
            this.textFieldISBN.setText(dto.getISBN());
            this.textFieldAutor.setText(dto.getAutor());
            this.textFieldEditura.setText(dto.getEditura());
            this.textFieldAnAparitie.setText(Integer.toString(dto.getAnAparitie()));
            this.textFieldStatus.setText(dto.getStatus());
        }
    }

    public void handleReturn(ActionEvent actionEvent) {
        try {
            int codAbonat = Integer.parseInt(this.textFieldCodAbonat.getText());
            int codExemplar = Integer.parseInt(this.textFieldCodExemplar.getText());
            Date now = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
            this.service.returnCopy(this.loggedInLibrarian, codAbonat, codExemplar, now); //if return deadline is overdue, then compute penalties
            CustomAlert.showMessage(this.dialogStage, Alert.AlertType.CONFIRMATION, "Succes!", "Ati returnat cartea cu succes!");
        } catch (NumberFormatException ignored) {
            CustomAlert.showErrorMessage(null, "Nu ati introdus corespunzator codurile de identificare!");
        } catch (OverdueError | UnavailableException over) {
            CustomAlert.showErrorMessage(null, over.getMessage());
        }
    }

    @Override
    public void update(BookCopyStateChangeEvent bookCopyStateChangeEvent) {
        initModel();
    }

    private void shutdown(){
        this.service.shutdown();
        this.dialogStage.close();
    }

    public void handleExit(ActionEvent actionEvent) {this.shutdown();
    }
}
