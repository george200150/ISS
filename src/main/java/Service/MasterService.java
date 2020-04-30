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

// class A ; class B extends A ; B b = ..(B).. new A();  MUST HAVE CAST TO class B, BUT it can THROW ClassCastException
// ..interview question..

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

    public List<Object> findAngajatByCredentials(String username, String password) {
        if(username.length() == 0){
            // search for bibliotecar account
            Bibliotecar bibliotecar = this.findBibliotecarByCredentials(password);
            if (bibliotecar == null)
                return new ArrayList<Object>(List.of("null", "null"));
            return new ArrayList<Object>(List.of(bibliotecar, "bibliotecar"));
        }
        else{
            String codAbonatString = username;
            Abonat abonat = this.findAbonatByCredentials(codAbonatString, password);
            if (abonat == null)
                return new ArrayList<Object>(List.of("null", "null"));
            return new ArrayList<Object>(List.of(abonat, "abonat"));
        }
    }

    private Abonat findAbonatByCredentials(String codAbonatString, String password) {
        try{
            int codAbonat = Integer.parseInt(codAbonatString);
            return repoAbonat.findByCredentials(codAbonat, password);
        }
        catch (NumberFormatException ignored){
            throw new UnavailableException("Codul abonatului a fost introdus necorespunzator!");
        }
    }

    private Bibliotecar findBibliotecarByCredentials(String password) {
        return repoBibliotecar.findByCredentials(password);
    }

    public Iterable<ExemplarCarte> getAllExemplareExistente() {
        return repoBiblioteca.getAllExisting();
    }

    public void imprumuta(Abonat loggedInAbonat, ExemplarCarte exemplar, Date start, Date stop) {
        //TODO: !!! validate + implement (choose where to start the validation step)
        this.repoBiblioteca.imprumuta(exemplar); // throws if already hired in the meantime
        this.repoImprumut.imprumuta(loggedInAbonat, exemplar, start, stop); // history of hired exemplars
        notifyObservers(new ExemplarStateChangeEvent(ChangeEventType.IMPRUMUTAT, exemplar));
    }

    public Iterable<ExemplarCarte> getAllExemplareDisponibile() {
        return repoBiblioteca.getAllAvailable();
    }


    //.. not implemented yet
    public void returneaza(Bibliotecar loggedInBibliotecar, int codAbonat, int codExemplar, Date now) {
        ExemplarCarte exemplar = this.findExemplarInchiriatById(codExemplar); // TODO: THIS SHOULD BE THE POINT WHERE IT IS DECIDED IF THE EXEMPLAR HAS NOT BEEN FOUND (further validations become futile (_here_))
        //TODO: change findExemplarById to find___Available___ExemplarById !!!

        //TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!//TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!//TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!
        //TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!//TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!//TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!
        //TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!//TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!//TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!


        //TODO: !!! validate + implement (choose where to start the validation step)
        this.repoBiblioteca.returneaza(exemplar);
        Abonat loggedInAbonat = this.repoAbonat.findById(codAbonat);
        int delay = this.repoImprumut.returneaza(loggedInAbonat, exemplar, now); // history of hired exemplars.
        notifyObservers(new ExemplarStateChangeEvent(ChangeEventType.RETURNAT, exemplar));
        if (delay > 0)
            throw new OverdueError("penalties: " + delay + " week overdue!");
    }

    private ExemplarCarte findExemplarById(int codExemplar) {
        //return repoBiblioteca.findExemplarById(codExemplar);
        return null;
    }

    private ExemplarCarte findExemplarInchiriatById(int codExemplar) {
        return repoBiblioteca.findExemplarInchiriatById(codExemplar);
    }



    public boolean esteExemplarInchiriat(ExemplarCarte exem) {
        return this.repoBiblioteca.esteExemplarInchiriat(exem);
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