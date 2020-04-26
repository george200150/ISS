import Domain.*;
import MVC.AbonatController;
import MVC.LoginController;
import Repository.DBRepositoryAbonat;
import Repository.DBRepositoryBibliotecar;
import Repository.DBRepositoryImprumut;
import Service.MasterService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

//    private CrudRepository<String, Student> studentRepository;
//    private StudentService studentService;
//    private CrudRepository<String, Tema> temaRepository;
//    private TemaService temaService;
//    private CrudRepository<String, Nota> notaRepository;
//    private NotaService notaService;
//
//    private CrudRepository<String, Profesor> profesorRepository;
//    private ProfesorService profesorService;
//
//    private CrudRepository<String, Motivation> motivationRepository;
//    private MotivationService motivationService;

    private MasterService masterService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        //T.O.D.O.: NOT WORKING !!! - */studentRepository = new StudentRepository(StudentValidator.getInstance(), ApplicationContext.getPROPERTIES().getProperty("data.txt.studenti"));
        //studentRepository = new StudentRepository(StudentValidator.getInstance(), "data/STUDENTI.txt");
//        studentRepository = new StudentDataBaseRepository(StudentValidator.getInstance());
//        studentService = new StudentService(studentRepository);
//
//        //temaRepository = new TemaRepository(TemaValidator.getInstance(), "data/TEME.txt");
//        temaRepository = new TemaDataBaseRepository(TemaValidator.getInstance());
//        temaService = new TemaService(temaRepository);
//
//        //notaRepository = new NotaRepository(NotaValidator.getInstance(), "data/NOTE.txt");
//        notaRepository = new NotaDataBaseRepository(NotaValidator.getInstance());
//        notaService = new NotaService(notaRepository);
//
//        //profesorRepository = new ProfesorRepository(ProfesorValidator.getInstance(), "data/PROFESORI.txt");
//        profesorRepository = new ProfesorDataBaseRepository(ProfesorValidator.getInstance());
//        profesorService = new ProfesorService(profesorRepository);
//
//        //motivationRepository = new MotivationRepository(MotivationValidator.getInstance(),"data/MOTIVARI.txt");
//        motivationRepository = new MotivationDataBaseRepository(MotivationValidator.getInstance());
//        motivationService = new MotivationService(motivationRepository);

//        masterService = new MasterService(profesorService, studentService, temaService, notaService, motivationService);
        Carte carte = new Carte("ION", "isbn:1234RO","Marcel Avram", "Polina", 1969);
        ExemplarCarte exemplarCarte1 = new ExemplarCarte(1, carte);
        ExemplarCarte exemplarCarte2 = new ExemplarCarte(2, carte);
        ExemplarCarte exemplarCarte3 = new ExemplarCarte(3, carte);
        ArrayList<ExemplarCarte> exemplare = new ArrayList<ExemplarCarte>(List.of(exemplarCarte1, exemplarCarte2, exemplarCarte3));

        Abonat abonat1 = new Abonat("1990324240024", "Gheorghe Vasile", "Strada limbii", "08322323", 1, "1");
        Abonat abonat2 = new Abonat("1990324240024", "Gheorghe Vasile", "Strada limbii", "08322323", 2, "1");
        Abonat abonat3 = new Abonat("1990324240024", "Gheorghe Vasile", "Strada limbii", "08322323", 3, "1");
        ArrayList<Abonat> abonati = new ArrayList<Abonat>(List.of(abonat1, abonat2, abonat3));


        Biblioteca repoBiblioteca = new Biblioteca(abonati,exemplare,new Bibliotecar()); //TODO: create constructor !!!
        DBRepositoryAbonat repoAbonat = new DBRepositoryAbonat(); //TODO: create constructor !!!
        DBRepositoryBibliotecar repoBibliotecar = new DBRepositoryBibliotecar(); //TODO: create constructor !!!
        DBRepositoryImprumut repoImprumut = new DBRepositoryImprumut(); //TODO: create constructor !!!


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
