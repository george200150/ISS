package Repository;

import Domain.Abonat;
import Domain.ExemplarCarte;
import Domain.Imprumut;
import Repository.postgres.ImprumutDataBaseRepository;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DBRepositoryImprumut {

    ImprumutDataBaseRepository repo;

    public DBRepositoryImprumut(ImprumutDataBaseRepository repo) {
        this.repo = repo;
        Optional<Integer> optionalMax = StreamSupport.stream(repo.findAll().spliterator(), false).map(Imprumut::getCodUnicImprumut).max(Comparator.comparingInt(x -> x));
        counter = optionalMax.map(integer -> integer + 1).orElse(0); // initialize id for further hiring of books
    }

    private static int counter = 0;
    public void imprumuta(Abonat loggedInAbonat, ExemplarCarte exemplar, Date start, Date stop) {
        //Validator.validate(start, stop);
        //TODO: check if already imprumutat
//        if( findImprumutByAbonatAndExemplar(loggedInAbonat, exemplar) != null)
//            throw new UnavailableException("Exemplarul pe care incercati sa il imprumutati nu mai este disponibil!"); // TODO: maybe this is overkill... this is an unnecessary doublecheck
        // bad double check...

        Imprumut imprumut = new Imprumut(counter, start, stop,false, loggedInAbonat.getCodUnic(), exemplar.getCodUnic());
        //TODO: validate interval
        //exemplar must exist (ne need to check)
        //TODO: should have already checked existence of abonat and exemplar, I think..
        repo.save(imprumut);
        counter++;
    }

    public int returneaza(Abonat loggedInAbonat, ExemplarCarte exemplar, Date now){
        //TODO: check if deadline is not overdue
        //TODO: find logged hired exemplar entry in the DB
        // update the status of the hiring log for the exemplar (set aFostReturnat to true)
        Imprumut imprumut = this.findImprumutByAbonatAndExemplar(loggedInAbonat, exemplar);
        if (imprumut == null) // maybe this is bad as well
            throw new UnavailableException("Exemplarul de returnat nu a putut fi gasit!"); // TODO: maybe this is overkill... this is an unnecessary doublecheck

        // compute delay, if any
        int delay = 0;
        imprumut.setaFostReturnat(true);
        imprumut.setDataRestituire(now);
        repo.update(imprumut);
        return delay;
    }

    private Imprumut findImprumutByAbonatAndExemplar(Abonat loggedInAbonat, ExemplarCarte exemplar) {
        List<Imprumut> fromDBresult = StreamSupport.stream(repo.findAll().spliterator(), false).filter(x -> !x.isaFostReturnat()).collect(Collectors.toList());
        for (Imprumut imprumut: fromDBresult) {
            if (imprumut.getCreator() == loggedInAbonat.getCodUnic() && imprumut.getExemplar() == exemplar.getCodUnic())
                return imprumut;
        }// foreach imprumut in imprumuturi, where imprumut.aFostReturnat == False !!! (nu are sens sa cautam deja imprumuturi efectuate)
        return null;
    }
}
