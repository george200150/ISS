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
        ExemplarCarte gasit = this.findExemplarDisponibilById(exemplar.getCodUnic());
        if (gasit == null){
            //TODO: aici ar trebui sa arunce exceptie
            throw new UnavailableException("cartea nu mai este disponibila!!! !!!"); // TODO: aici pare sa mearga, sa il gaseasca unde trebuie, dar validarea din functiile "imprumuta" are alta parere... must fix here
        }
        //TODO: !!! validate + implement (choose where to start the validation step)
        this.repoImprumut.imprumuta(loggedInAbonat, exemplar, start, stop); // history of hired exemplars
        this.repoBiblioteca.imprumuta(exemplar); // throws if already hired in the meantime

        repoBiblioteca.setUpExemplare(repoImprumut.getRepo());
        notifyObservers(new ExemplarStateChangeEvent(ChangeEventType.IMPRUMUTAT, exemplar));
    }

    public Iterable<ExemplarCarte> getAllExemplareDisponibile() {
        return repoBiblioteca.getAllAvailable();
    }



    public void returneaza(Bibliotecar loggedInBibliotecar, int codAbonat, int codExemplar, Date now) {
        ExemplarCarte exemplar = this.findExemplarInchiriatById(codExemplar);
        // TODO: THIS SHOULD BE THE POINT WHERE IT IS DECIDED IF THE EXEMPLAR HAS NOT BEEN FOUND (further validations become futile (_here_))
        //TODO: change findExemplarById to find___Available___ExemplarById !!!

        //TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!//TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!//TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!
        //TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!//TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!//TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!
        //TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!//TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!//TODO: ALREADY CHECKED IF EXEMPLAR EXISTS !!!


        //TODO: !!! validate + implement (choose where to start the validation step)
        this.repoBiblioteca.returneaza(exemplar);
        Abonat loggedInAbonat = this.repoAbonat.findById(codAbonat);
        int delay = this.repoImprumut.returneaza(loggedInAbonat, exemplar, now); // history of hired exemplars.
        repoBiblioteca.setUpExemplare(repoImprumut.getRepo());
        notifyObservers(new ExemplarStateChangeEvent(ChangeEventType.RETURNAT, exemplar));
        if (delay > 0)
            throw new OverdueError("penalties: " + delay + " week overdue!");
    }


    private ExemplarCarte findExemplarInchiriatById(int codExemplar) {
        return repoBiblioteca.findExemplarInchiriatById(codExemplar);
    }

    private ExemplarCarte findExemplarDisponibilById(int codExemplar) {
        return repoBiblioteca.findExemplarDisponibilById(codExemplar);
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

    public void deleteExemmplar(int codUnic) {
        //TODO: implement this + convert Biblioteca to a service that has a repo of Exemplars. (nu e ok cum am conceput design-ul aici)
    }
}