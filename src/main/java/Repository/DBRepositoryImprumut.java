package Repository;

import Domain.Abonat;
import Domain.ExemplarCarte;
import Domain.Imprumut;

import java.time.LocalDate;

public class DBRepositoryImprumut {
    private static int counter = 0;
    public void imprumuta(Abonat loggedInAbonat, ExemplarCarte exemplar, LocalDate start, LocalDate stop) {
        //Validator.validate(start, stop);
        //TODO: check if already imprumutat
        if( findImprumutByAbonatAndExemplar(loggedInAbonat, exemplar) != null)
            throw new UnavailableException("Exemplarul pe care incercati sa il imprumutati nu mai este disponibil!"); // TODO: maybe this is overkill... this is an unnecessary doublecheck

        Imprumut imprumut = new Imprumut(counter, start, stop,false,loggedInAbonat, exemplar);
        //TODO: validate interval
        //exemplar must exist (ne need to check)
        //TODO: should have already checked existence of abonat and exemplar, I think..
    }

    public int returneaza(Abonat loggedInAbonat, ExemplarCarte exemplar, LocalDate now){
        //TODO: check if deadline is not overdue
        //TODO: find logged hired exemplar entry in the DB
        // update the status of the hiring log for the exemplar (set aFostReturnat to true)
        Imprumut imprumut = this.findImprumutByAbonatAndExemplar(loggedInAbonat, exemplar);
        if (imprumut == null)
            throw new UnavailableException("Exemplarul de returnat nu a putut fi gasit!"); // TODO: maybe this is overkill... this is an unnecessary doublecheck

        // compute delay, if any
        int delay = 0;
        imprumut.setaFostReturnat(true);
        return delay;
    }

    private Imprumut findImprumutByAbonatAndExemplar(Abonat loggedInAbonat, ExemplarCarte exemplar) {
        return null;
        // TODO: foreach imprumut in imprumuturi, where imprumut.aFostReturnat == False !!! (nu are sens sa cautam deja imprumuturi efectuate)
    }
}
