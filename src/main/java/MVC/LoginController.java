package MVC;

import Domain.Abonat;
import Domain.Bibliotecar;
import Service.MasterService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    @FXML
    private Button buttonLogIn;

    private Stage dialogStage;
    private MasterService masterService;

    @FXML
    private void initialize() {
    }

    public void setService(MasterService masterService, Stage stage){
        this.masterService = masterService;
        this.dialogStage = stage;
    }


    public void handleLogIn(ActionEvent actionEvent) {

        String codUser = this.codUserTextField.getText(); // TODO: this is actually an int (empty field for librarian)
        String password = this.passwordFieldUserPassword.getText();
        List<Object> response = this.masterService.findAngajatByCredentials(codUser, password);
        String grantedType = (String) response.get(1); //TODO: check if account acces is granted

        if(grantedType.equals("abonat")){
            Abonat abonat = (Abonat) response.get(0);
            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("abonat.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Log In Abonat");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                AbonatController studentAccountController = loader.getController();
                studentAccountController.setService(masterService,dialogStage, abonat);

                this.dialogStage.close();
                dialogStage.show();
                dialogStage.setMaximized(true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(grantedType.equals("bibliotecar")){ // bibliotecarul este si admin
            Bibliotecar bibliotecar = (Bibliotecar) response.get(0);
            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("bibliotecar.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Log In Bibliotecar");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                BibliotecarController studentAccountController = loader.getController();
                studentAccountController.setService(masterService,dialogStage, bibliotecar);

                this.dialogStage.close();
                dialogStage.show();
                dialogStage.setMaximized(true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            CustomAlert.showMessage(null, Alert.AlertType.ERROR,"Log In","Credentialele de acces sunt gresite!");
        }
    }


    public void handleKeyPress(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)) {
            this.buttonLogIn.fire();
        }
    }
}
