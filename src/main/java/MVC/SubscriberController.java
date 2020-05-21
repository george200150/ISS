package MVC;

import Domain.iss.Book;
import Domain.iss.BookCopy;
import Domain.iss.Subscriber;
import Domain.iss.BookCopyDTO;
import Service.LibraryService;
import Utils.BookCopyStateChangeEvent;
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


public class SubscriberController extends EmployeeController implements Observer<BookCopyStateChangeEvent> {
    @FXML public TextField textFieldTitlu;
    @FXML public TextField textFieldISBN;
    @FXML public TextField textFieldAutor;
    @FXML public TextField textFieldEditura;
    @FXML public TextField textFieldAnAparitie;
    @FXML private Label labelAbonat;
    @FXML private TableView<BookCopyDTO> tableExemplareAbonat;
    @FXML private TableColumn<BookCopyDTO, String> tableExemplareAbonatColumnTitlu;
    // @FXML private TableColumn<BookCopyDTO, String> tableExemplareAbonatColumnISBN;
    @FXML private TableColumn<BookCopyDTO, String> tableExemplareAbonatColumnAutor;
    // @FXML private TableColumn<BookCopyDTO, String> tableExemplareAbonatColumnEditura;
    // @FXML private TableColumn<BookCopyDTO, String> tableExemplareAbonatColumnAnAparitie;
    private Stage dialogStage;
    private Subscriber loggedInSubscriber;
    private LibraryService service;
    private ObservableList<BookCopyDTO> model = FXCollections.observableArrayList();

    public void setService(LibraryService libraryService, Stage stage, Subscriber loggedInSubscriber) {
        this.dialogStage = stage;
        this.loggedInSubscriber = loggedInSubscriber;
        service = libraryService;
        service.addObserver(this);
        initModel();
        this.labelAbonat.setText(loggedInSubscriber.toString());
    }

    private void initModel() {
        // only available copies will be displayed
        Iterable<BookCopy> grades = service.getAllAvailableCopies();
        List<BookCopy> gradeList = StreamSupport
                .stream(grades.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(convertGradeToDTO(gradeList));
    }

    @FXML
    public void initialize() {
        tableExemplareAbonatColumnTitlu.setCellValueFactory(new PropertyValueFactory<BookCopyDTO, String>("titlu"));
        // tableExemplareAbonatColumnISBN.setCellValueFactory(new PropertyValueFactory<BookCopyDTO, String>("ISBN"));
        tableExemplareAbonatColumnAutor.setCellValueFactory(new PropertyValueFactory<BookCopyDTO, String>("autor"));
        // tableExemplareAbonatColumnEditura.setCellValueFactory(new PropertyValueFactory<BookCopyDTO, String>("editura"));
        // tableExemplareAbonatColumnAnAparitie.setCellValueFactory(new PropertyValueFactory<BookCopyDTO, String>("anAparitie"));
        tableExemplareAbonat.setItems(model);
    }

    private List<BookCopyDTO> convertGradeToDTO(List<BookCopy> gradeList) {
        return gradeList.stream()
                .map(exem -> { Book found = this.service.findBookById(exem.getRefer()); return new BookCopyDTO(exem.getCodUnic(), found);})
                .collect(Collectors.toList());
    }

    public void handleMoreDetails(MouseEvent mouseEvent) {
        BookCopyDTO dto = this.tableExemplareAbonat.getSelectionModel().getSelectedItem();
        if (dto != null) {
            this.textFieldTitlu.setText(dto.getTitlu());
            this.textFieldISBN.setText(dto.getISBN());
            this.textFieldAutor.setText(dto.getAutor());
            this.textFieldEditura.setText(dto.getEditura());
            this.textFieldAnAparitie.setText(Integer.toString(dto.getAnAparitie()));
        }
    }

    public void handleHire(ActionEvent actionEvent) {
        BookCopyDTO dto = this.tableExemplareAbonat.getSelectionModel().getSelectedItem();
        if (dto == null) {
            CustomAlert.showErrorMessage(this.dialogStage, "Nu ati selectat un exemplar!");
        } else { // open new window for special hiring form
            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/imprumut.fxml"));
                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Log In Subscriber");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                HiringHandlerController hiringHandlerController = loader.getController();
                hiringHandlerController.setService(this.service, this.dialogStage, dialogStage, dto.getBookCopy(), this.loggedInSubscriber);
                this.dialogStage.hide(); // hide the window here and create the new form; do the opposite on the form in order to unhide the window.
                dialogStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update(BookCopyStateChangeEvent bookCopyStateChangeEvent) {
        initModel();
    }

    public void handleExit(ActionEvent actionEvent) {
        this.service.shutdown();
        this.dialogStage.close();
    }
}
