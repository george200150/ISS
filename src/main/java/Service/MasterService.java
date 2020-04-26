package Service;

import Domain.Abonat;
import Domain.Biblioteca;
import Domain.Bibliotecar;
import Domain.ExemplarCarte;
import Repository.DBRepositoryAbonat;
import Repository.DBRepositoryBibliotecar;
import Repository.DBRepositoryImprumut;
import Utils.ChangeEventType;
import Utils.ExemplarStateChangeEvent;
import Utils.Observable;
import Utils.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// class A ; class B extends A ; B b = ..(B).. new A();  MUST HAVE CAST TO class B, BUT it can THROW ClassCastException
// ..interview question..

public class MasterService implements Observable<ExemplarStateChangeEvent> {
    private List<Observer> observers = new ArrayList<>();
    private Biblioteca repoBiblioteca;
    private DBRepositoryAbonat repoAbonat;
    private DBRepositoryBibliotecar repoBibliotecar;
    private DBRepositoryImprumut repoImprumut;

    public List<Object> findAngajatByCredentials(String username, String password) {
        if(username.length() == 0){
            // search for bibliotecar account
            Bibliotecar bibliotecar = this.findBibliotecarByCredentials(password);
            if (bibliotecar == null)
                return new ArrayList<Object>(List.of("null", "null"));
            return new ArrayList<Object>(List.of(bibliotecar, "bibliotecar"));
        }
        else{
            int codAbonat = Integer.parseInt(username);
            Abonat abonat = this.findAbonatByCredentials(codAbonat, password);
            if (abonat == null)
                return new ArrayList<Object>(List.of("null", "null"));
            return new ArrayList<Object>(List.of(abonat, "abonat"));
        }
    }

    private Abonat findAbonatByCredentials(int codAbonat, String password) {
        return repoAbonat.findByCredentials(codAbonat, password);
    }

    private Bibliotecar findBibliotecarByCredentials(String password) {
        return repoBibliotecar.findByCredentials(password);
    }

    public Iterable<ExemplarCarte> getAllExemplareExistente() {
        return repoBiblioteca.getAllExisting();
    }

    public void imprumuta(Abonat loggedInAbonat, ExemplarCarte exemplar, LocalDate start, LocalDate stop) {
        //TODO: !!! validate + implement
        this.repoBiblioteca.imprumuta(exemplar); // two lists - one of available exemplars and one of hired
        this.repoImprumut.imprumuta(loggedInAbonat, exemplar, start, stop); // history of hired exemplars. these will not be deleted for statistical use... i guess ?
        notifyObservers(new ExemplarStateChangeEvent(ChangeEventType.IMPRUMUTAT, exemplar));
    }

    public Iterable<ExemplarCarte> getAllExemplareDisponibile() {
        return repoBiblioteca.getAllAvailable();
    }

    public void returneaza(Bibliotecar loggedInBibliotecar, int codAbonat, int codExemplar, LocalDate now) {
        ExemplarCarte exemplar = this.findExemplarById(codExemplar);
        //TODO: !!! validate + implement
        this.repoBiblioteca.returneaza(exemplar); // two lists - one of available exemplars and one of hired
        Abonat loggedInAbonat = this.repoAbonat.findById(codAbonat);// TODO: check if information request is not redundant
        this.repoImprumut.returneaza(loggedInAbonat, exemplar, now); // history of hired exemplars. these will not be deleted for statistical use... i guess ?
        notifyObservers(new ExemplarStateChangeEvent(ChangeEventType.RETURNAT, exemplar));
    }

    private ExemplarCarte findExemplarById(int codExemplar) {
        return repoBiblioteca.findExemplarById(codExemplar);
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
}