package Service;

import Domain.iss.Book;
import Domain.iss.BookCopy;
import Domain.iss.Subscriber;
import Domain.iss.Librarian;
import Repository.*;
import Repository.iss.hbm.*;
import Utils.ChangeEventType;
import Utils.BookCopyStateChangeEvent;
import Utils.Observable;
import Utils.Observer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class LibraryService implements Observable<BookCopyStateChangeEvent> {
    private List<Observer> observers = new ArrayList<>();
    private SubscriberHBMRepo repoSubscribers;
    private LibrarianHBMRepo repoLibrarians;
    private HiringHBMRepo repoHirings;
    private CopyHBMRepo repoCopies;
    private BookHBMRepo repoBooks; // check when inserting/updating copies if the info matches the original book's info

    public LibraryService(BookHBMRepo repoBooks, CopyHBMRepo repoCopies, SubscriberHBMRepo repoSubscribers, LibrarianHBMRepo repoLibrarians, HiringHBMRepo repoHirings) {
        this.repoBooks = repoBooks;
        this.repoCopies = repoCopies;
        this.repoSubscribers = repoSubscribers;
        this.repoLibrarians = repoLibrarians;
        this.repoHirings = repoHirings;
    }

    public List<Object> findEmployeeByCredentials(int employeeCode, String password) {
        // search for librarian account
        Librarian librarian = this.findLibrarianByCredentials(employeeCode, password);
        if (librarian != null) {
            return new ArrayList<Object>(List.of(librarian, "librarian"));
        } else { // search for subscriber account
            Subscriber subscriber = this.findSubscriberByCredentials(employeeCode, password);
            if (subscriber != null) {
                return new ArrayList<Object>(List.of(subscriber, "subscriber"));
            } else { // then, the input is totally wrong
                return new ArrayList<Object>(List.of("null", "null"));
            }
        }
    }

    private Subscriber findSubscriberByCredentials(int codAbonat, String password) {
        try {
            return repoSubscribers.findByCredentials(codAbonat, password);
        } catch (NumberFormatException ignored) {
            throw new UnavailableException("Codul abonatului a fost introdus necorespunzator!");
        }
    }

    private Librarian findLibrarianByCredentials(int cod, String password) {
        return repoLibrarians.findByCredentials(cod, password);
    }

    public Iterable<BookCopy> getAllExistingCopies() {
        Iterable<BookCopy> exemplare = this.repoCopies.findAll();
        return StreamSupport.stream(exemplare.spliterator(), false)
                .collect(Collectors.toList());
    }

    public void hireCopy(Subscriber loggedInSubscriber, BookCopy bookCopy, Date start, Date stop) {
        BookCopy found = this.findAvailableCopyById(bookCopy.getCodUnic());
        // acum stim statusul de disponibilitate al exemplarului
        if (found == null) {
            throw new UnavailableException("cartea nu mai este disponibila!!! !!!");
        }
        this.repoHirings.hireCopy(loggedInSubscriber, bookCopy, start, stop); // history of hired exemplars
        notifyObservers(new BookCopyStateChangeEvent(ChangeEventType.IMPRUMUTAT, bookCopy));
    }

    public Iterable<BookCopy> getAllAvailableCopies() {
        Iterable<BookCopy> exemplare = this.repoCopies.findAll();
        return StreamSupport.stream(exemplare.spliterator(), false)
                .filter(x -> this.isAvailableCopy(x.getCodUnic()))
                .collect(Collectors.toList());
    }

    public void returnCopy(Librarian loggedInLibrarian, int subscriberCode, int copyCode, Date now) {
        Librarian found = this.repoLibrarians.findByCredentials(loggedInLibrarian.getCodUnic(), loggedInLibrarian.getParola());
        if (found != null) { // doublc check credentials for librarian
            BookCopy foundCopy = this.findHiredCopyById(copyCode); // format exceptions were handled in the controller -> only NPE can result from this call's result
            // now we know whether the book copy exists or not.
            Subscriber loggedInSubscriber = this.repoSubscribers.findOne(subscriberCode);
            // now we know whether the subscriber exists or not.
            // This validation - checking if the entities exist in the database - MUST take place NOW. Else, the DB will be in an inconsistent state.
            if (foundCopy != null) {
                if (loggedInSubscriber != null) {
                    int delay = this.repoHirings.returnCopy(loggedInSubscriber, foundCopy, now); // persist the change in the history of hired exemplars.
                    notifyObservers(new BookCopyStateChangeEvent(ChangeEventType.RETURNAT, foundCopy));
                    if (delay > 0)
                        throw new OverdueError("penalitati: " + delay + " saptamani intarziate!");
                } else {
                    throw new UnavailableException("Nu s-a putut gasi abonatul care a imprumutat exemplarul!");
                }
            } else {
                if (findAvailableCopyById(copyCode) != null) {
                    throw new UnavailableException("Exemplarul introdus a fost deja returnat!");
                } else {
                    throw new UnavailableException("Nu s-a putut gasi exemplarul introdus!");
                }
            }
        } else {
            throw new UnavailableException("Nu aveti drepturi de a face aceasta actiune! Incercati sa va logati din nou.");
        }

    }

    private BookCopy findHiredCopyById(int copyCode) {
        BookCopy ex = this.repoCopies.findOne(copyCode);
        if (ex != null && !repoHirings.isCopyAvailable(copyCode))
            return ex;
        return null;
    }

    private BookCopy findAvailableCopyById(int copyCode) {
        BookCopy toBeHired = this.repoCopies.findOne(copyCode);
        if (toBeHired != null && repoHirings.isCopyAvailable(copyCode))
            return toBeHired;
        return null;
    }

    public boolean isHiredCopy(int copyCode) {
        return !this.repoHirings.isCopyAvailable(copyCode);
    }

    private boolean isAvailableCopy(int codUnic) { return this.repoHirings.isCopyAvailable(codUnic); }

    @Override
    public void addObserver(Observer<BookCopyStateChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<BookCopyStateChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(BookCopyStateChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }

    public void operate(int uniqueCode, Book realBook, String operationType) {
        if (this.isAvailableCopy(uniqueCode) || operationType.equals("INSERT")) {
            BookCopy result;
            if (operationType.equals("INSERT")) {
                // validate if existing book ; if values match the book's ones (find book by all fields)
                Book existing = this.repoBooks.findEquivalent(realBook);
                if (existing != null) {
                    result = this.repoCopies.save(new BookCopy(uniqueCode, realBook.getISBN()));
                    if (result != null) {
                        throw new ValidationException("Exemplar duplicat gasit la adaugare!");
                    }
                } else {
                    throw new ValidationException("Nu s-a putut gasi originalul!");
                }
            } else if (operationType.equals("SELECT")) {
                // would better implement search based on typing listener in LibrarianController.
            } else if (operationType.equals("UPDATE")) {
                // validate if existing book ; if values match the book's ones (find book by all fields)
                Book existing = this.repoBooks.findEquivalent(realBook);
                if (existing != null) {
                    result = this.repoCopies.update(new BookCopy(uniqueCode, realBook.getISBN()));
                    if (result == null) {
                        throw new ValidationException("Exemplarul nu a putut fi gasit si modificat!");
                    }
                } else {
                    throw new ValidationException("Nu s-a putut gasi originalul!");
                }
            } else if (operationType.equals("DELETE")) {
                result = this.repoCopies.delete(uniqueCode);
                if (result == null) {
                    throw new ValidationException("Exemplarul nu a putut fi gasit si sters!");
                }
            } // else throw not operation type recognised exception
            this.notifyObservers(new BookCopyStateChangeEvent(ChangeEventType.OPERATIE, new BookCopy(uniqueCode, realBook.getISBN())));
        } else {
            throw new UnavailableException("Nu se pot efectua modificari asupra exemplarelor inchiriate momentan!");
        }
    }

    public void shutdown() {
        SubscriberHBMRepo.close();
        BookHBMRepo.close();
        LibrarianHBMRepo.close();
        HiringHBMRepo.close();
        CopyHBMRepo.close();
    }

    public Book findBookById(String isbn) {
        return this.repoBooks.findOne(isbn);
    }
}