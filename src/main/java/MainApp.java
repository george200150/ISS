import Domain.*;
import MVC.LoginController;
import Repository.postgres.*;
import Service.LibraryService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Properties;

public class MainApp extends Application {
    private LibraryService libraryService;

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

        Book book = new Book("ION", "isbn:1234RO","Marcel Avram", "Polina", 1969);
        BookCopy bookCopy1 = new BookCopy(1, book);
        BookCopy bookCopy2 = new BookCopy(2, book);
        BookCopy bookCopy3 = new BookCopy(3, book);
        //ArrayList<BookCopy> exemplare = new ArrayList<BookCopy>(List.of(bookCopy1, bookCopy2, bookCopy3));

        ExemplarDataBaseRepository repoE = new ExemplarDataBaseRepository();
        /*repoE.save(bookCopy1);
        repoE.save(bookCopy2);
        repoE.save(bookCopy3);*/

        /*Subscriber abonat1 = new Subscriber("1990324240024", "Gheorghe Vasile", "Strada limbii", "08322323", 1, "1");
        Subscriber abonat2 = new Subscriber("1990324240024", "Gheorghe Vasile", "Strada limbii", "08322323", 2, "1");
        Subscriber abonat3 = new Subscriber("1990324240024", "Gheorghe Vasile", "Strada limbii", "08322323", 3, "1");
        ArrayList<Subscriber> abonati = new ArrayList<Subscriber>(List.of(abonat1, abonat2, abonat3));*/

        SubscriberDataBaseRepository repoA = new SubscriberDataBaseRepository();
        //repoA.save(abonat1);
        //repoA.save(abonat2);
        //repoA.save(abonat3);

        Librarian librarian1 = new Librarian(0,"0");
        LibrarianDataBaseRepository repoB = new LibrarianDataBaseRepository();
        //repoB.save(librarian1);


        HiringDataBaseRepository repoI = new HiringDataBaseRepository();



        libraryService = new LibraryService(repoE, repoA, repoB, repoI);

        init1(primaryStage);
        primaryStage.show();
    }

    private void init1(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/views/login.fxml"));
        AnchorPane gradeLayout = fxmlLoader.load();

        primaryStage.setScene(new Scene(gradeLayout));

        LoginController loginChoiceController = fxmlLoader.getController();
        loginChoiceController.setService(libraryService, primaryStage);
    }
}
