package Repository.postgres;

import Domain.BookCopy;
import Domain.Hiring;
import Domain.Subscriber;
import Repository.UnavailableException;
import Repository.ValidationException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class HiringDataBaseRepository implements CrudRepository<Integer, Hiring> {
    private Connection connection;
    private static int counter = 0; // this is for correct Id assignment when creating a new Hiring contract

    public HiringDataBaseRepository() {
        this.connection = JDBCInvariant.getConnection();
        Optional<Integer> optionalMax = StreamSupport.stream(this.findAll().spliterator(), false).map(Hiring::getCodUnicImprumut).max(Comparator.comparingInt(x -> x));
        counter = optionalMax.map(integer -> integer + 1).orElse(0); // initialize id for further hiring of books
    }

    public void imprumuta(Subscriber loggedInSubscriber, BookCopy exemplar, Date start, Date stop) {
        if (getWeeksBetween(start, stop) <= 0) {
            throw new ValidationException("Nu puteti imprumuta exemplarul in acest interval!");
        } else {
            if (!this.checkIfExemplarIsDisponibil(exemplar))
                throw new UnavailableException("Exemplarul de imprumutat nu mai este disponibil!");

            Hiring hiring = new Hiring(counter, start, stop, false, loggedInSubscriber.getCodUnic(), exemplar.getCodUnic());

            this.save(hiring);
            counter++;
        }
    }

    private static int getWeeksBetween(Date a, Date b) {
        if (b.before(a)) {
            return -getWeeksBetween(b, a);
        }
        a = resetTime(a);
        b = resetTime(b);

        Calendar cal = new GregorianCalendar();
        cal.setTime(a);
        int weeks = 0;
        while (cal.getTime().before(b)) { // add another week
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            weeks++;
        }
        return weeks;
    }

    private static Date resetTime(Date d) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public int returneaza(Subscriber loggedInSubscriber, BookCopy exemplar, Date now) {
        // find logged hired copy entry in the DB
        Hiring hiring = this.findImprumutByAbonatAndExemplar(loggedInSubscriber, exemplar);
        if (hiring == null)
            throw new UnavailableException("Exemplarul de returnat nu a putut fi gasit!");

        Date deadline = hiring.getDataRestituire();
        // compute delay, if any
        int delay = getWeeksBetween(deadline, now);

        hiring.setaFostReturnat(true); // update status of the copy and update the database entry
        hiring.setDataRestituire(now);
        this.update(hiring);
        return delay;
    }

    public boolean checkIfExemplarIsDisponibil(BookCopy exemplar) {
        List<Hiring> fromDBresult = StreamSupport
                .stream(this.findAll().spliterator(), false)
                .filter(x -> x.getExemplar() == exemplar.getCodUnic())
                .filter(x -> !x.isaFostReturnat())
                .collect(Collectors.toList());
        return fromDBresult.size() <= 0;
    }

    private Hiring findImprumutByAbonatAndExemplar(Subscriber loggedInSubscriber, BookCopy exemplar) {
        // no need to search for already finished lendings
        List<Hiring> fromDBresult = StreamSupport.stream(this.findAll().spliterator(), false).filter(x -> !x.isaFostReturnat()).collect(Collectors.toList());
        for (Hiring hiring : fromDBresult) {
            if (hiring.getCreator() == loggedInSubscriber.getCodUnic() && hiring.getExemplar() == exemplar.getCodUnic())
                return hiring;
            else if (hiring.getCreator() != loggedInSubscriber.getCodUnic() && hiring.getExemplar() == exemplar.getCodUnic())
                throw new UnavailableException("Exemplarul este imprumutat de un alt abonat momentan!");
        }
        return null;
    }

    @Override
    public Hiring findOne(Integer id) throws IllegalArgumentException {
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Imprumuturi\"  WHERE \"codUnicImprumut\" =" + "\'" + id + "\'");
            data.next();//TODO: sql injection prone
            //int codUnicImprumut = data.getInt(1);
            Date dataEfectuare = data.getDate(2);
            Date dataRestituire = data.getDate(3);
            Boolean aFostReturnat = data.getBoolean(4);
            int creator = data.getInt(5);
            int exemplar = data.getInt(6);

            Hiring hiring = new Hiring(id, dataEfectuare, dataRestituire, aFostReturnat, creator, exemplar);
            return hiring;
        } catch (SQLException ignored) {
        }
        return null;
    }

    @Override
    public Iterable<Hiring> findAll() {
        List<Hiring> lst = new ArrayList<>();
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Imprumuturi\"");
            while (data.next()) {
                int codUnicImprumut = data.getInt(1);
                Date dataEfectuare = data.getDate(2);
                Date dataRestituire = data.getDate(3);
                Boolean aFostReturnat = data.getBoolean(4);
                int creator = data.getInt(5);
                int exemplar = data.getInt(6);

                Hiring hiring = new Hiring(codUnicImprumut, dataEfectuare, dataRestituire, aFostReturnat, creator, exemplar);
                lst.add(hiring);
            }
        } catch (SQLException ignored) {
            throw new IllegalArgumentException("Eroare: nu s-a putut realiza conexiunea la baza de date");
        }
        return lst;
    }

    @Override
    public Hiring save(Hiring entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");
        }
        try {
            connection.createStatement().execute("INSERT INTO \"Imprumuturi\" VALUES (" +
                    entity.getCodUnicImprumut() + ",\'" +
                    entity.getDataEfectuare() + "\',\'" +
                    entity.getDataRestituire() + "\',\'" +
                    entity.isaFostReturnat() + "\',\'" +
                    entity.getCreator() + "\',\'" +
                    entity.getExemplar() + "\')"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Hiring delete(Integer id) throws IllegalArgumentException {
        Hiring abonat = findOne(id);
        if (abonat != null) {
            try {
                connection.createStatement()
                        .execute("DELETE FROM \"Imprumuturi\" WHERE \"codUnicImprumut\" = " + "\'" + id + "\'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return abonat;
    }

    @Override
    public Hiring update(Hiring entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        }
        if (findOne(entity.getCodUnicImprumut()) != null) {
            Hiring old = findOne(entity.getCodUnicImprumut());
            try {
                connection.createStatement().execute("UPDATE \"Imprumuturi\" SET " +
                        "\"dataEfectuare\" = \'" + entity.getDataEfectuare() + "\'" +
                        ",\"dataRestituire\" = \'" + entity.getDataRestituire() + "\'" +
                        ",\"aFostReturnat\" = \'" + entity.isaFostReturnat() + "\'" + // backslash in order to make it case sensitive
                        ",creator = \'" + entity.getCreator() + "\'" +
                        ",\"exemplar\" = \'" + entity.getExemplar() + "\'" + "WHERE \"codUnicImprumut\" =" + "\'" + entity.getCodUnicImprumut() + "\'"
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return old;
        }
        return null;
    }
}
