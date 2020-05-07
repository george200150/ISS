import Domain.*;
import MVC.LoginController;
import Repository.DBRepositoryAbonat;
import Repository.DBRepositoryBibliotecar;
import Repository.DBRepositoryImprumut;
import Repository.postgres.*;
import Service.MasterService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MainApp extends Application {
    private MasterService masterService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Properties properties = new Properties();
        try {
            properties.load(JDBCInvariant.class.getResourceAsStream("/bd.config"));
            properties.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find bd.config " + e);
            return;
        }
        new JDBCInvariant(properties); // initialize static fields in object before using anything from app logic

        Carte carte = new Carte("ION", "isbn:1234RO","Marcel Avram", "Polina", 1969);
        ExemplarCarte exemplarCarte1 = new ExemplarCarte(1, carte);
        ExemplarCarte exemplarCarte2 = new ExemplarCarte(2, carte);
        ExemplarCarte exemplarCarte3 = new ExemplarCarte(3, carte);
        //ArrayList<ExemplarCarte> exemplare = new ArrayList<ExemplarCarte>(List.of(exemplarCarte1, exemplarCarte2, exemplarCarte3));

        ExemplarDataBaseRepository repoE = new ExemplarDataBaseRepository();
        /*repoE.save(exemplarCarte1);
        repoE.save(exemplarCarte2);
        repoE.save(exemplarCarte3);*/

        /*Abonat abonat1 = new Abonat("1990324240024", "Gheorghe Vasile", "Strada limbii", "08322323", 1, "1");
        Abonat abonat2 = new Abonat("1990324240024", "Gheorghe Vasile", "Strada limbii", "08322323", 2, "1");
        Abonat abonat3 = new Abonat("1990324240024", "Gheorghe Vasile", "Strada limbii", "08322323", 3, "1");
        ArrayList<Abonat> abonati = new ArrayList<Abonat>(List.of(abonat1, abonat2, abonat3));*/

        AbonatDataBaseRepository repoA = new AbonatDataBaseRepository();
        //repoA.save(abonat1);
        //repoA.save(abonat2);
        //repoA.save(abonat3);

        Bibliotecar bibliotecar1 = new Bibliotecar(0,"0");
        BibliotecarDataBaseRepository repoB = new BibliotecarDataBaseRepository();
        //repoB.save(bibliotecar1);


        ImprumutDataBaseRepository repoI = new ImprumutDataBaseRepository();

        DBRepositoryAbonat repoAbonat = new DBRepositoryAbonat(repoA);
        DBRepositoryBibliotecar repoBibliotecar = new DBRepositoryBibliotecar(repoB);
        DBRepositoryImprumut repoImprumut = new DBRepositoryImprumut(repoI);


        Biblioteca repoBiblioteca = new Biblioteca(repoA,repoE,bibliotecar1);

        masterService = new MasterService(repoBiblioteca, repoAbonat, repoBibliotecar, repoImprumut);

        init1(primaryStage);
        primaryStage.show();
    }

    private void init1(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/views/login.fxml"));
        AnchorPane gradeLayout = fxmlLoader.load();

        primaryStage.setScene(new Scene(gradeLayout));

        LoginController loginChoiceController = fxmlLoader.getController();
        loginChoiceController.setService(masterService, primaryStage);
    }
}
