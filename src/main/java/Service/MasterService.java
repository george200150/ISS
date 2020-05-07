package Service;

import Domain.Abonat;
import Domain.Biblioteca;
import Domain.Bibliotecar;
import Domain.ExemplarCarte;
import Repository.*;
import Utils.ChangeEventType;
import Utils.ExemplarStateChangeEvent;
import Utils.Observable;
import Utils.Observer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class MasterService implements Observable<ExemplarStateChangeEvent> {
    private List<Observer> observers = new ArrayList<>();
    private Biblioteca repoBiblioteca;
    private DBRepositoryAbonat repoAbonat;
    private DBRepositoryBibliotecar repoBibliotecar;
    private DBRepositoryImprumut repoImprumut;

    public MasterService(Biblioteca repoBiblioteca, DBRepositoryAbonat repoAbonat, DBRepositoryBibliotecar repoBibliotecar, DBRepositoryImprumut repoImprumut) {
        this.repoAbonat = repoAbonat;
        this.repoBiblioteca = repoBiblioteca;
        this.repoBibliotecar = repoBibliotecar;
        this.repoImprumut = repoImprumut;
    }


    public List<Object> findAngajatByCredentials(int codAngajat, String password) {
        // search for librarian account
        Bibliotecar bibliotecar = this.findBibliotecarByCredentials(codAngajat, password);
        if (bibliotecar != null){
            return new ArrayList<Object>(List.of(bibliotecar, "bibliotecar"));
        }
        else{
            // search for subscriber account
            Abonat abonat = this.findAbonatByCredentials(codAngajat, password);
            if(abonat != null){
                return new ArrayList<Object>(List.of(abonat, "abonat"));
            }
            else{
                // then, the input is totally wrong
                return new ArrayList<Object>(List.of("null", "null"));
            }
        }
    }

    private Abonat findAbonatByCredentials(int codAbonat, String password) {
        try{
            return repoAbonat.findByCredentials(codAbonat, password);
        }
        catch (NumberFormatException ignored){
            throw new UnavailableException("Codul abonatului a fost introdus necorespunzator!");
        }
    }

    private Bibliotecar findBibliotecarByCredentials(int cod, String password) {
        return repoBibliotecar.findByCredentials(cod, password);
    }

    public Iterable<ExemplarCarte> getAllExemplareExistente() {
        Iterable<ExemplarCarte> exemplare = repoBiblioteca.findAllExemplare();
        return StreamSupport.stream(exemplare.spliterator(), false)
                .collect(Collectors.toList());
    }

    public void imprumuta(Abonat loggedInAbonat, ExemplarCarte exemplar, Date start, Date stop) {
        ExemplarCarte gasit = this.findExemplarDisponibilById(exemplar.getCodUnic());
        //voi considera o alta abordare a validarii disponibilitatii:
        // in loc sa caut in biblioteca, voi cauta in repo-ul de imprumuturi, verificand ca niciun imprumut in care a fost implicat exemplarul sa nu fie inca activ. (aFostReturnat = true)

        // acum stim statusul de disponibilitate al exemplarului
        if (gasit == null){
            throw new UnavailableException("cartea nu mai este disponibila!!! !!!");
            // TODO: aici pare sa mearga, sa il gaseasca unde trebuie, dar validarea din functiile "imprumuta" are alta parere... must fix here
        }
        //TODO: !!! validate + implement (choose where to start the validation step)
        this.repoImprumut.imprumuta(loggedInAbonat, exemplar, start, stop); // history of hired exemplars
        notifyObservers(new ExemplarStateChangeEvent(ChangeEventType.IMPRUMUTAT, exemplar));
    }

    public Iterable<ExemplarCarte> getAllExemplareDisponibile() {
        Iterable<ExemplarCarte> exemplare = repoBiblioteca.findAllExemplare();
        return StreamSupport.stream(exemplare.spliterator(), false)
                .filter(this::esteExemplarDisponibil)
                .collect(Collectors.toList());
    }



    public void returneaza(Bibliotecar loggedInBibliotecar, int codAbonat, int codExemplar, Date now) {
        ExemplarCarte exemplar = this.findExemplarInchiriatById(codExemplar); // format exceptions were handled in the controller -> only NPE can result from this call's result
        // now we know whether the exemplar exists or not.
        Abonat loggedInAbonat = this.repoAbonat.findById(codAbonat);
        // now we know whether the subscriber exists or not.
        // This validation - checking if the entities exist in the database - MUST take place NOW. Else, the DB will be in an inconsistent state.
        if (exemplar != null) {
            if (loggedInAbonat != null) {
                int delay = this.repoImprumut.returneaza(loggedInAbonat, exemplar, now); // persist the change in the history of hired exemplars.
                notifyObservers(new ExemplarStateChangeEvent(ChangeEventType.RETURNAT, exemplar));
                if (delay > 0)
                    throw new OverdueError("penalties: " + delay + " week overdue!");
            } else {
                throw new UnavailableException("Nu s-a putut gasi abonatul care a imprumutat exemplarul!");
            }
        } else {
            throw new UnavailableException("Nu s-a putut gasi exemplarul introdus!");
        }
    }


    private ExemplarCarte findExemplarInchiriatById(int codExemplar) {
        ExemplarCarte ex = repoBiblioteca.findOneExemplar(codExemplar);
        if (!repoImprumut.checkIfExemplarIsDisponibil(ex))
            return ex;
        return null;
    }

    private ExemplarCarte findExemplarDisponibilById(int codExemplar) {
        ExemplarCarte toBeImprumutat = this.repoBiblioteca.findOneExemplar(codExemplar);
        if (repoImprumut.checkIfExemplarIsDisponibil(toBeImprumutat))
            return toBeImprumutat;
        return null;
    }



    public boolean esteExemplarInchiriat(ExemplarCarte exem) {
        return !this.repoImprumut.checkIfExemplarIsDisponibil(exem);
    }

    public boolean esteExemplarDisponibil(ExemplarCarte exem) {
        return this.repoImprumut.checkIfExemplarIsDisponibil(exem);
    }



    @Override
    public void addObserver(Observer<ExemplarStateChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<ExemplarStateChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(ExemplarStateChangeEvent t) {
        observers.forEach(x -> x.update(t));
    }


    public void opereaza(ExemplarCarte exemplar, String tipOperatie) {
        if( tipOperatie.equals("INSERT")){
            this.repoBiblioteca.saveExemplar(exemplar);
        }
        else if( tipOperatie.equals("SELECT")){
            // would better implement search based on typing listener in BibliotecarController.
        }
        else if( tipOperatie.equals("UPDATE")){
            this.repoBiblioteca.updateExemplar(exemplar);
        }
        else if( tipOperatie.equals("DELETE")){
            this.repoBiblioteca.removeExemplar(exemplar.getCodUnic());
        }
        // else throw not operation type recognised exception
    }
}