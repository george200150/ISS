package Domain;

import Repository.postgres.AbonatDataBaseRepository;
import Repository.postgres.ExemplarDataBaseRepository;


public class Biblioteca {
    private AbonatDataBaseRepository abonati;
    private ExemplarDataBaseRepository exemplare;
    private Bibliotecar bibliotecar;

    public Biblioteca(AbonatDataBaseRepository abonati, ExemplarDataBaseRepository exemplare, Bibliotecar bibliotecar) {
        this.abonati = abonati;
        this.exemplare = exemplare;
        this.bibliotecar = bibliotecar;
    }

    public Iterable<ExemplarCarte> findAllExemplare(){
        return this.exemplare.findAll();
    }

    public ExemplarCarte findOneExemplar(int codUnic){
        return this.exemplare.findOne(codUnic);
    }

    public ExemplarCarte saveExemplar(ExemplarCarte exemplar) {
        return this.exemplare.save(exemplar);
    }

    public ExemplarCarte updateExemplar(ExemplarCarte exemplar) {
        return this.exemplare.update(exemplar);
    }

    public ExemplarCarte removeExemplar(int codUnic) {
        return this.exemplare.delete(codUnic);
    }


    public Bibliotecar getBibliotecar() {
        return bibliotecar;
    }

    public void setBibliotecar(Bibliotecar bibliotecar) {
        this.bibliotecar = bibliotecar;
    }
}
