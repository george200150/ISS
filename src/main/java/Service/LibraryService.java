package Service;

import Domain.BookCopy;
import Domain.Subscriber;
import Domain.Librarian;
import Repository.*;
import Repository.postgres.HiringDataBaseRepository;
import Repository.postgres.SubscriberDataBaseRepository;
import Repository.postgres.LibrarianDataBaseRepository;
import Repository.postgres.ExemplarDataBaseRepository;
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
    private SubscriberDataBaseRepository repoSubscribers;
    private LibrarianDataBaseRepository repoLibrarians;
    private HiringDataBaseRepository repoHirings;
    private ExemplarDataBaseRepository repoCopies;

    public LibraryService(ExemplarDataBaseRepository repoCopies, SubscriberDataBaseRepository repoSubscribers, LibrarianDataBaseRepository repoLibrarians, HiringDataBaseRepository repoHirings) {
        this.repoCopies = repoCopies;
        this.repoSubscribers = repoSubscribers;
        this.repoLibrarians = repoLibrarians;
        this.repoHirings = repoHirings;
    }

    public List<Object> findEmployeeByCredentials(int codAngajat, String password) {
        // search for librarian account
        Librarian librarian = this.findLibrarianByCredentials(codAngajat, password);
        if (librarian != null) {
            return new ArrayList<Object>(List.of(librarian, "librarian"));
        } else { // search for subscriber account
            Subscriber subscriber = this.findSubscriberByCredentials(codAngajat, password);
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

    public void hireCopy(Subscriber loggedInSubscriber, BookCopy exemplar, Date start, Date stop) {
        BookCopy gasit = this.findAvailableCopyById(exemplar.getCodUnic());
        // acum stim statusul de disponibilitate al exemplarului
        if (gasit == null) {
            throw new UnavailableException("cartea nu mai este disponibila!!! !!!");
        }
        this.repoHirings.imprumuta(loggedInSubscriber, exemplar, start, stop); // history of hired exemplars
        notifyObservers(new BookCopyStateChangeEvent(ChangeEventType.IMPRUMUTAT, exemplar));
    }

    public Iterable<BookCopy> getAllAvailableCopies() {
        Iterable<BookCopy> exemplare = this.repoCopies.findAll();
        return StreamSupport.stream(exemplare.spliterator(), false)
                .filter(this::isAvailableCopy)
                .collect(Collectors.toList());
    }

    public void returnCopy(Librarian loggedInLibrarian, int codAbonat, int codExemplar, Date now) {
        Librarian answer = this.repoLibrarians.findByCredentials(loggedInLibrarian.getCodUnic(), loggedInLibrarian.getParola());
        if (answer != null) { // doublc check credentials for librarian
            BookCopy exemplar = this.findHiredCopyById(codExemplar); // format exceptions were handled in the controller -> only NPE can result from this call's result
            // now we know whether the exemplar exists or not.
            Subscriber loggedInSubscriber = this.repoSubscribers.findOne(codAbonat);
            // now we know whether the subscriber exists or not.
            // This validation - checking if the entities exist in the database - MUST take place NOW. Else, the DB will be in an inconsistent state.
            if (exemplar != null) {
                if (loggedInSubscriber != null) {
                    int delay = this.repoHirings.returneaza(loggedInSubscriber, exemplar, now); // persist the change in the history of hired exemplars.
                    notifyObservers(new BookCopyStateChangeEvent(ChangeEventType.RETURNAT, exemplar));
                    if (delay > 0)
                        throw new OverdueError("penalitati: " + delay + " saptamani intarziate!");
                } else {
                    throw new UnavailableException("Nu s-a putut gasi abonatul care a imprumutat exemplarul!");
                }
            } else {
                if (findAvailableCopyById(codExemplar) != null) {
                    throw new UnavailableException("Exemplarul introdus a fost deja returnat!");
                } else {
                    throw new UnavailableException("Nu s-a putut gasi exemplarul introdus!");
                }
            }
        } else {
            throw new UnavailableException("Nu aveti drepturi de a face aceasta actiune! Incercati sa va logati din nou.");
        }

    }

    private BookCopy findHiredCopyById(int codExemplar) {
        BookCopy ex = this.repoCopies.findOne(codExemplar);
        if (ex != null && !repoHirings.checkIfExemplarIsDisponibil(ex))
            return ex;
        return null;
    }

    private BookCopy findAvailableCopyById(int codExemplar) {
        BookCopy toBeImprumutat = this.repoCopies.findOne(codExemplar);
        if (toBeImprumutat != null && repoHirings.checkIfExemplarIsDisponibil(toBeImprumutat))
            return toBeImprumutat;
        return null;
    }

    public boolean isHiredCopy(BookCopy exem) {
        return !this.repoHirings.checkIfExemplarIsDisponibil(exem);
    }

    public boolean isAvailableCopy(BookCopy exem) {
        return this.repoHirings.checkIfExemplarIsDisponibil(exem);
    }

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

    public void operate(BookCopy exemplar, String tipOperatie) {
        if (this.isAvailableCopy(exemplar) || tipOperatie.equals("INSERT")) {
            BookCopy result;
            if (tipOperatie.equals("INSERT")) {
                result = this.repoCopies.save(exemplar);
                if (result != null) {
                    throw new ValidationException("Exemplar duplicat gasit la adaugare!");
                }
            } else if (tipOperatie.equals("SELECT")) {
                // would better implement search based on typing listener in LibrarianController.
            } else if (tipOperatie.equals("UPDATE")) {
                result = this.repoCopies.update(exemplar);
                if (result == null) {
                    throw new ValidationException("Exemplarul nu a putut fi gasit si modificat!");
                }
            } else if (tipOperatie.equals("DELETE")) {
                result = this.repoCopies.delete(exemplar.getCodUnic());
                if (result != null) {
                    throw new ValidationException("Exemplarul nu a putut fi gasit si sters!");
                }
            } // else throw not operation type recognised exception
            this.notifyObservers(new BookCopyStateChangeEvent(ChangeEventType.OPERATIE, exemplar));
        } else {
            throw new UnavailableException("Nu se pot efectua modificari asupra exemplarelor inchiriate momentan!");
        }
    }
}