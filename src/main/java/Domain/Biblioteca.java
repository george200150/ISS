package Domain;

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
        return all; // TODO: maybe create the "Imprumut" repo, in which save all the unavailable books for now...
    }

    public Iterable<ExemplarCarte> getAllAvailable() {
        return exemplareDisponibile;
    }

    public ExemplarCarte findExemplarById(int codExemplar) {
        return null;
    }

    public void imprumuta(ExemplarCarte exemplar) {

    }

    public void returneaza(ExemplarCarte exemplar) {

    }
}
