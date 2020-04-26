package Domain;

import Repository.UnavailableException;

import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private List<Abonat> abonati;
    private List<ExemplarCarte> exemplareDisponibile;
    private List<ExemplarCarte> exemplareInchiriate;
    private Bibliotecar bibliotecar;

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

    public ExemplarCarte findExemplarById(int codExemplar) {
        // ... maybe split? TODO: vague function naming... available or unavailable ??? - refactor ASAP !!!
        return null; // TODO: implement this
        //TODO: decide if this should be refactored...
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


    public void imprumuta(ExemplarCarte exemplar) {
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
            if(ex.getCodUnic() == exemplar.getCodUnic())
                this.exemplareDisponibile.remove(ex);
                break;
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
        for (ExemplarCarte ex : this.exemplareInchiriate) {
            if(ex.getCodUnic() == exemplar.getCodUnic())
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
