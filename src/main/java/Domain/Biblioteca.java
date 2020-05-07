package Domain;

import Repository.postgres.AbonatDataBaseRepository;
import Repository.postgres.ExemplarDataBaseRepository;

import java.util.List;


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

    public void saveExemplar(ExemplarCarte exemplar) { //TODO: should throw ??? - no return
        this.exemplare.save(exemplar);
    }

    public void updateExemplar(ExemplarCarte exemplar) { //TODO: should throw ??? - no return
        this.exemplare.update(exemplar);
    }

    public void removeExemplar(int codUnic) { //TODO: should throw ??? - no return
        this.exemplare.delete(codUnic);
    }


    public Bibliotecar getBibliotecar() {
        return bibliotecar;
    }

    public void setBibliotecar(Bibliotecar bibliotecar) {
        this.bibliotecar = bibliotecar;
    }
}
