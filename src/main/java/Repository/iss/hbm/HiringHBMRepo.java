package Repository.iss.hbm;

import Domain.iss.BookCopy;
import Domain.iss.Hiring;
import Domain.iss.Subscriber;
import Repository.CrudRepository;

import Repository.UnavailableException;
import Repository.ValidationException;
import Repository.utils.FunctionsUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class HiringHBMRepo implements CrudRepository<Integer, Hiring> {
    private static int counter = 0; // this is for correct Id assignment when creating a new Hiring contract
    static SessionFactory sessionFactory;

    public HiringHBMRepo() {
        initialize();
        Optional<Integer> optionalMax = StreamSupport.stream(this.findAll().spliterator(), false).map(Hiring::getCodUnicImprumut).max(Comparator.comparingInt(x -> x));
        counter = optionalMax.map(integer -> integer + 1).orElse(0); // initialize id for further hiring of books
    }

    @Override
    public Hiring findOne(Integer codUnic) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Query query = session.createQuery("from Hiring where codUnicImprumut = :cod", Hiring.class);
                query.setParameter("cod", codUnic);
                Hiring hiring = (Hiring) query.setMaxResults(1).uniqueResult();

                System.out.println(hiring + " hiring found");
                tx.commit();
                return hiring;
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                return null;
            }
        }
    }

    @Override
    public Iterable<Hiring> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                List<Hiring> hirings = session.createQuery("from Hiring", Hiring.class)
                        .list();

                System.out.println(hirings.size() + " hiring(s) found");
                for (Hiring b : hirings) {
                    System.out.println(b.getCreator() + " " + b.getExemplar());
                }
                tx.commit();
                return hirings;
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                return null;
            }
        }
    }

    @Override
    public Hiring save(Hiring entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");
        }

        if (this.findOne(entity.getCodUnicImprumut()) != null)
            throw new ValidationException("DUPLICAT GASIT!");

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Hiring hiring = new Hiring(entity.getCodUnicImprumut(), entity.getDataEfectuare(), entity.getDataRestituire(), entity.isaFostReturnat(), entity.getCreator(), entity.getExemplar());
                session.save(hiring);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Hiring delete(Integer codUnic) {
        // NOT NECESSARY FOR RUNNING THE APP (used only to populate the database at the beginning)
        if (codUnic == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi NULL!");
        }
        Hiring bookCopy = findOne(codUnic);
        if (bookCopy != null) { // if it makes sense looking for it
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    Query query = session.createQuery("from Hiring where codUnicImprumut = :cod", Hiring.class);
                    query.setParameter("cod", codUnic);
                    Hiring crit = (Hiring) query.setMaxResults(1).uniqueResult();

                    System.err.println("delete hiring " + crit.getCodUnicImprumut());
                    session.delete(crit);
                    tx.commit();
                    return bookCopy;
                } catch (RuntimeException e) {
                    if (tx != null)
                        tx.rollback();
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Hiring update(Hiring entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        }
        if (this.findOne(entity.getCodUnicImprumut()) != null) {
            Hiring old = this.findOne(entity.getCodUnicImprumut());
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    // validation is done one application layer above this one
                    Query query =  session.createQuery("update Hiring b set b.creator = :creator, b.dataEfectuare = :datastart, b.dataRestituire = :dataend, b.exemplar = :exemp, b.aFostReturnat = :isret where b.codUnicImprumut = :cod");
                    query.setParameter("cod", entity.getCodUnicImprumut());
                    query.setParameter("creator", entity.getCreator());
                    query.setParameter("datastart", entity.getDataEfectuare());
                    query.setParameter("dataend", entity.getDataRestituire()); // updating this (in case of delay)
                    query.setParameter("exemp", entity.getExemplar());
                    query.setParameter("isret", entity.isaFostReturnat()); // updating this (current status)
                    query.executeUpdate();

                    tx.commit();
                    return old;
                } catch (RuntimeException e) {
                    if (tx != null)
                        tx.rollback();
                    return null;
                }
            }
        }
        return null;
    }

    public static void initialize() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            System.out.println("Exceptie " + e);
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    public int returneaza(Subscriber loggedInSubscriber, BookCopy exemplar, Date now) {
        // find logged hired copy entry in the DB
        Hiring hiring = this.findImprumutByAbonatAndExemplar(loggedInSubscriber, exemplar);
        if (hiring == null)
            throw new UnavailableException("Exemplarul de returnat nu a putut fi gasit!");

        Date deadline = hiring.getDataRestituire();
        // compute delay, if any
        int delay = FunctionsUtils.getWeeksBetween(deadline, now);

        hiring.setaFostReturnat(true); // update status of the copy and update the database entry
        hiring.setDataRestituire(now);
        this.update(hiring);
        return delay;
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

    public void imprumuta(Subscriber loggedInSubscriber, BookCopy exemplar, Date start, Date stop) {
        if (FunctionsUtils.getWeeksBetween(start, stop) <= 0) {
            throw new ValidationException("Nu puteti imprumuta exemplarul in acest interval!");
        } else {
            if (!this.checkIfExemplarIsDisponibil(exemplar.getCodUnic()))
                throw new UnavailableException("Exemplarul de imprumutat nu mai este disponibil!");

            Hiring hiring = new Hiring(counter, start, stop, false, loggedInSubscriber.getCodUnic(), exemplar.getCodUnic());

            this.save(hiring);
            counter++;
        }
    }

    public boolean checkIfExemplarIsDisponibil(int codUnic) {
        List<Hiring> fromDBresult = StreamSupport
                .stream(this.findAll().spliterator(), false)
                .filter(x -> x.getExemplar() == codUnic)
                .filter(x -> !x.isaFostReturnat())
                .collect(Collectors.toList());
        return fromDBresult.size() <= 0;
    }
}
