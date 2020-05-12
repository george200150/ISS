import Domain.iss.Book;
import Domain.iss.BookCopy;
import Domain.iss.Librarian;
import Domain.iss.Subscriber;
import MVC.LoginController;
import Repository.iss.hbm.*;
import Service.LibraryService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class MainApp extends Application {
    private LibraryService libraryService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        CopyHBMRepo copyHBMRepo = new CopyHBMRepo();
        HiringHBMRepo hiringHBMRepo = new HiringHBMRepo();
        LibrarianHBMRepo librarianHBMRepo = new LibrarianHBMRepo();
        SubscriberHBMRepo subscriberHBMRepo = new SubscriberHBMRepo();
        BookHBMRepo bookHBMRepo = new BookHBMRepo();



        Book book = new Book("Ion Escu", "isbn:1234RO","Marcel Avram", "RAO", 2003);
        BookCopy bookCopy1 = new BookCopy(1, book.getISBN());
        BookCopy bookCopy2 = new BookCopy(2, book.getISBN());
        BookCopy bookCopy3 = new BookCopy(3, book.getISBN());

        Subscriber abonat1 = new Subscriber("1990324240024", "Gheorghe Vasile", "Strada limbii", "08322323", 1, "1");
        Subscriber abonat2 = new Subscriber("1990324240024", "Gheorghe Vasile", "Strada limbii", "08322323", 2, "1");
        Subscriber abonat3 = new Subscriber("1990324240024", "Gheorghe Vasile", "Strada limbii", "08322323", 3, "1");

        Librarian librarian1 = new Librarian(0,"0");

        /*copyHBMRepo.save(bookCopy1);copyHBMRepo.save(bookCopy2);copyHBMRepo.save(bookCopy3);
        librarianHBMRepo.save(librarian1);
        subscriberHBMRepo.save(abonat1);subscriberHBMRepo.save(abonat2);subscriberHBMRepo.save(abonat3);
        bookHBMRepo.save(book);*/
        /*Book book2 = new Book("ALA", "BALA", "PORTOCALA", "HELLO", 2019);
        bookHBMRepo.save(book2);*/


        libraryService = new LibraryService(bookHBMRepo, copyHBMRepo, subscriberHBMRepo, librarianHBMRepo, hiringHBMRepo);

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
