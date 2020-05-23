package MVC;

import Domain.iss.Subscriber;
import Domain.iss.Librarian;
import Repository.UnavailableException;
import Service.LibraryService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;


public class LoginController {
    @FXML public TextField codUserTextField;
    @FXML private PasswordField passwordFieldUserPassword;
    private Stage dialogStage;
    private LibraryService libraryService;

    @FXML
    private void initialize() {
    }

    public void setService(LibraryService libraryService, Stage stage) {
        this.libraryService = libraryService;
        this.dialogStage = stage;
    }

    public void handleLogIn(ActionEvent actionEvent) {
        try {
            int codUser = Integer.parseInt(this.codUserTextField.getText());
            String password = this.passwordFieldUserPassword.getText();
            try {
                List<Object> response = this.libraryService.findEmployeeByCredentials(codUser, password);
                String grantedType = (String) response.get(1); // check if account access is granted
                if (grantedType.equals("subscriber")) {
                    Subscriber subscriber = (Subscriber) response.get(0);
                    try {
                        // create a new stage for the popup dialog.
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/views/SubscriberView.fxml"));
                        AnchorPane root = (AnchorPane) loader.load();

                        // Create the dialog Stage and set the data to the stage.
                        Stage dialogStage = new Stage();
                        dialogStage.setTitle("Subscriber Window");
                        dialogStage.initModality(Modality.WINDOW_MODAL);
                        Scene scene = new Scene(root);
                        dialogStage.setScene(scene);

                        SubscriberController studentAccountController = loader.getController();
                        studentAccountController.setService(libraryService, dialogStage, subscriber);
                        this.dialogStage.close();
                        dialogStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (grantedType.equals("librarian")) { // librarian is the admin
                    Librarian librarian = (Librarian) response.get(0);
                    try {
                        // create a new stage for the popup dialog.
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/views/LibrarianView.fxml"));
                        AnchorPane root = fxmlLoader.load();

                        // Create the dialog Stage and set the data to the stage.
                        Stage dialogStage = new Stage();
                        dialogStage.setTitle("Librarian Window");
                        dialogStage.initModality(Modality.WINDOW_MODAL);
                        Scene scene = new Scene(root);
                        dialogStage.setScene(scene);

                        LibrarianController studentAccountController = fxmlLoader.getController();
                        studentAccountController.setService(libraryService, dialogStage, librarian);
                        this.dialogStage.close();
                        dialogStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomAlert.showErrorMessage(null, "Credentialele de acces sunt gresite!");
                }
            } catch (UnavailableException valX) {
                CustomAlert.showErrorMessage(null, valX.getMessage());
            }
        } catch (NumberFormatException nfe) {
            CustomAlert.showErrorMessage(null, "Codul de autentificare trebuie sa fie un numar intreg!");
        }
    }
}
