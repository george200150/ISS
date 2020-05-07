package Repository;

import Domain.Abonat;
import Domain.ExemplarCarte;
import Domain.Imprumut;
import Repository.postgres.ImprumutDataBaseRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DBRepositoryImprumut {

    ImprumutDataBaseRepository repo;

    public ImprumutDataBaseRepository getRepo(){
        return repo;
    }


    public DBRepositoryImprumut(ImprumutDataBaseRepository repo) {
        this.repo = repo;
        Optional<Integer> optionalMax = StreamSupport.stream(repo.findAll().spliterator(), false).map(Imprumut::getCodUnicImprumut).max(Comparator.comparingInt(x -> x));
        counter = optionalMax.map(integer -> integer + 1).orElse(0); // initialize id for further hiring of books
    }

    private static int counter = 0;
    public void imprumuta(Abonat loggedInAbonat, ExemplarCarte exemplar, Date start, Date stop) {
        //Validator.validate(start, stop);
        //TODO: validate interval
        //TODO: reverse calls in super caller stack in order to check here if exemplar is still hired at the moment
        if (!this.checkIfExemplarIsDisponibil(exemplar))
            throw new UnavailableException("Exemplarul de imprumutat nu mai este disponibil!");

        Imprumut imprumut = new Imprumut(counter, start, stop,false, loggedInAbonat.getCodUnic(), exemplar.getCodUnic());

        repo.save(imprumut);
        counter++;
    }

    private static int getWeeksBetween (Date a, Date b) {

        if (b.before(a)) {
            return -getWeeksBetween(b, a);
        }
        a = resetTime(a);
        b = resetTime(b);

        Calendar cal = new GregorianCalendar();
        cal.setTime(a);
        int weeks = 0;
        while (cal.getTime().before(b)) {
            // add another week
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            weeks++;
        }
        return weeks;
    }

    private static Date resetTime (Date d) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public int returneaza(Abonat loggedInAbonat, ExemplarCarte exemplar, Date now){
        // find logged hired exemplar entry in the DB
        Imprumut imprumut = this.findImprumutByAbonatAndExemplar(loggedInAbonat, exemplar);
        if (imprumut == null)
            throw new UnavailableException("Exemplarul de returnat nu a putut fi gasit!");

        Date deadline = imprumut.getDataRestituire();
        // compute delay, if any
        int delay = getWeeksBetween(deadline, now);

        imprumut.setaFostReturnat(true); // update status of the exemplar and update the database entry
        imprumut.setDataRestituire(now);
        repo.update(imprumut);
        return delay;
    }

    private boolean checkIfExemplarIsDisponibil(ExemplarCarte exemplar){
        List<Imprumut> fromDBresult = StreamSupport
                .stream(repo.findAll().spliterator(), false)
                .filter(x -> x.getExemplar() == exemplar.getCodUnic())
                .filter(x -> !x.isaFostReturnat())
                .collect(Collectors.toList());
        return fromDBresult.size() <= 0;
    }

    private Imprumut findImprumutByAbonatAndExemplar(Abonat loggedInAbonat, ExemplarCarte exemplar) {
        // nu are sens sa cautam deja imprumuturi efectuate
        List<Imprumut> fromDBresult = StreamSupport.stream(repo.findAll().spliterator(), false).filter(x -> !x.isaFostReturnat()).collect(Collectors.toList());
        for (Imprumut imprumut: fromDBresult) {
            if (imprumut.getCreator() == loggedInAbonat.getCodUnic() && imprumut.getExemplar() == exemplar.getCodUnic())
                return imprumut;
        }
        return null;
    }
}
