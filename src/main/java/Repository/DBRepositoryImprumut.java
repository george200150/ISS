package Repository;

import Domain.Abonat;
import Domain.ExemplarCarte;

import java.time.LocalDate;

public class DBRepositoryImprumut {
    public void imprumuta(Abonat loggedInAbonat, ExemplarCarte exemplar, LocalDate start, LocalDate stop) {
        //TODO: validate interval
        //TODO: check existence of exemplar ???
        //TODO: should have already checked existence of abonat and exemplar, I think..
    }

    public void returneaza(Abonat loggedInAbonat, ExemplarCarte exemplar, LocalDate now){
        //TODO: check if deadline is not overdue
        //TODO: find logged hired exemplar entry in the DB
        // update the status of the hiring log for the exemplar (set aFostReturnat to true)
    }
}
