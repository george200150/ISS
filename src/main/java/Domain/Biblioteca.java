package Domain;

import Repository.UnavailableException;
import Repository.postgres.ImprumutDataBaseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Biblioteca {
    private List<Abonat> abonati;
    private List<ExemplarCarte> exemplareDisponibile; // aici pun restul
    private List<ExemplarCarte> exemplareInchiriate; // trebuie ca la startup sa iau din logul de imprumuturi toate cartile care nu sunt returnate si sa le pun aici
    private Bibliotecar bibliotecar;

    public void setUpExemplare(ImprumutDataBaseRepository repoI){//TODO: exemplarele nu se gasesc cum trebuie. raman inchiriate, cand nu sunt, de fapt.
        List<Imprumut> allData = StreamSupport.stream(repoI.findAll().spliterator(), false).collect(Collectors.toList());
        List<Integer> disponibile = allData.stream().filter(Imprumut::isaFostReturnat).map(Imprumut::getExemplar).collect(Collectors.toList());
        List<ExemplarCarte> toBeRemoved = new ArrayList<>();
        for (ExemplarCarte exemplar: exemplareDisponibile) {//TODO: poate aici e problema... (e ca si cum nu ar conta ce e in baza de date, in imprumuturi)
            boolean found = false;
            for(int cod : disponibile){
                if(exemplar.getCodUnic() == cod){
                    found = true;
                    break;
                }
            }
            if(!found){
                toBeRemoved.add(exemplar); // => invalidated iterator
                exemplareInchiriate.add(exemplar);
            }
        }
        for (ExemplarCarte exemplar: toBeRemoved) {
            this.exemplareDisponibile.remove(exemplar);
        }
    }

    public Biblioteca(List<Abonat> abonati, List<ExemplarCarte> exemplare, Bibliotecar bibliotecar) {
        this.abonati = abonati;
        this.exemplareDisponibile = exemplare;
        this.exemplareInchiriate = new ArrayList<>();
        this.bibliotecar = bibliotecar;
    }

    public List<Abonat> getAbonati() {
        return abonati;
    }

    public void setAbonati(List<Abonat> abonati) {
        this.abonati = abonati;
    }

    public Bibliotecar getBibliotecar() {
        return bibliotecar;
    }

    public void setBibliotecar(Bibliotecar bibliotecar) {
        this.bibliotecar = bibliotecar;
    }

    public Iterable<ExemplarCarte> getAllExisting() {
        List<ExemplarCarte> all = new ArrayList<>();
        all.addAll(exemplareDisponibile);
        all.addAll(exemplareInchiriate);
        return all;
    }

    public Iterable<ExemplarCarte> getAllAvailable() {
        return exemplareDisponibile;
    }


    public ExemplarCarte findExemplarDisponibilById(int codExemplar) {
        for (ExemplarCarte ex : this.exemplareDisponibile) {
            if(ex.getCodUnic() == codExemplar)
                return ex;
        }
        return null;
    }
    public ExemplarCarte findExemplarInchiriatById(int codExemplar) {
        for (ExemplarCarte ex : this.exemplareInchiriate) {
            if(ex.getCodUnic() == codExemplar)
                return ex;
        }
        return null;
    }


    public void imprumuta(ExemplarCarte exemplar) { // TODO: buguri aici... nu se imprumuta bine
        if( this.esteExemplarInchiriat(exemplar)){
            throw new UnavailableException("Cartea a fost deja imprumutata intre timp!");
        }
        this.deleteExemplarDisponibil(exemplar);
        this.addExemplarInchiriat(exemplar);
    }

    private void addExemplarInchiriat(ExemplarCarte exemplar) {
        this.exemplareInchiriate.add(exemplar);
    }

    private void deleteExemplarDisponibil(ExemplarCarte exemplar) {
        for (ExemplarCarte ex : this.exemplareDisponibile) {
            if (ex.getCodUnic() == exemplar.getCodUnic()) {
                this.exemplareDisponibile.remove(ex);
                break;
            }
        }
    }

    public void returneaza(ExemplarCarte exemplar) {
        if( ! this.esteExemplarInchiriat(exemplar)){
            throw new UnavailableException("Cartea a fost deja returnata intre timp?");
        }
        this.deleteExemplarInchiriat(exemplar);
        this.addExemplarDisponibil(exemplar);
    }

    private void addExemplarDisponibil(ExemplarCarte exemplar) {
        this.exemplareDisponibile.add(exemplar);
    }

    private void deleteExemplarInchiriat(ExemplarCarte exemplar) {
        for (ExemplarCarte ex : this.exemplareInchiriate)
            if (ex.getCodUnic() == exemplar.getCodUnic()) {
                this.exemplareInchiriate.remove(ex);
                break;
            }
    }

    public boolean esteExemplarInchiriat(ExemplarCarte exem) {
        for (ExemplarCarte ex : this.exemplareInchiriate) {
            if(ex.getCodUnic() == exem.getCodUnic())
                return true;
        }
        return false;
    }
}
