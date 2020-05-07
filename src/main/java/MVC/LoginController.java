package MVC;

import Domain.Abonat;
import Domain.Bibliotecar;
import Repository.UnavailableException;
import Service.ManagerService;
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
    @FXML
    public TextField codUserTextField;
    @FXML
    private PasswordField passwordFieldUserPassword;

    private Stage dialogStage;
    private ManagerService managerService;

    @FXML
    private void initialize() {
    }

    public void setService(ManagerService managerService, Stage stage) {
        this.managerService = managerService;
        this.dialogStage = stage;
    }


    public void handleLogIn(ActionEvent actionEvent) {
        try {
            int codUser = Integer.parseInt(this.codUserTextField.getText()); // this is actually an int
            String password = this.passwordFieldUserPassword.getText();

            try {
                List<Object> response = this.managerService.findAngajatByCredentials(codUser, password);
                String grantedType = (String) response.get(1); // check if account access is granted

                if (grantedType.equals("abonat")) {
                    Abonat abonat = (Abonat) response.get(0);
                    try {
                        // create a new stage for the popup dialog.
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/views/abonat.fxml"));
                        AnchorPane root = (AnchorPane) loader.load();

                        // Create the dialog Stage.
                        Stage dialogStage = new Stage();
                        dialogStage.setTitle("Fereastra Abonat");
                        dialogStage.initModality(Modality.WINDOW_MODAL);
                        Scene scene = new Scene(root);
                        dialogStage.setScene(scene);

                        AbonatController studentAccountController = loader.getController();
                        studentAccountController.setService(managerService, dialogStage, abonat);

                        this.dialogStage.close();
                        dialogStage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (grantedType.equals("bibliotecar")) { // bibliotecarul este si admin
                    Bibliotecar bibliotecar = (Bibliotecar) response.get(0);
                    try {
                        // create a new stage for the popup dialog.
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("/views/bibliotecar.fxml"));
                        AnchorPane root = fxmlLoader.load();

                        // Create the dialog Stage.
                        Stage dialogStage = new Stage();
                        dialogStage.setTitle("Fereastra  Bibliotecar");
                        dialogStage.initModality(Modality.WINDOW_MODAL);
                        Scene scene = new Scene(root);
                        dialogStage.setScene(scene);

                        BibliotecarController studentAccountController = fxmlLoader.getController();
                        studentAccountController.setService(managerService, dialogStage, bibliotecar);

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
        }
        catch (NumberFormatException nfe){
            CustomAlert.showErrorMessage(null, "Codul de autentificare trebuie sa fie un numar intreg!");
        }
    }
}
