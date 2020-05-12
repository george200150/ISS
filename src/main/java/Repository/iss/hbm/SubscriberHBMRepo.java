package Repository.iss.hbm;

import Domain.iss.Subscriber;
import Repository.CrudRepository;

import Repository.ValidationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.util.List;


public class SubscriberHBMRepo implements CrudRepository<Integer, Subscriber> {
    static SessionFactory sessionFactory;

    public SubscriberHBMRepo() {
        initialize();
    }


    public Subscriber findByCredentials(int codUnic, String password) throws IllegalArgumentException {
        if (password == null) {
            throw new IllegalArgumentException("PAROLA NU POATE FI NULL");
        }
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Query query = session.createQuery("from Subscriber where codUnic = :cod and parola = :parola", Subscriber.class);
                query.setParameter("cod", codUnic);
                query.setParameter("parola", password);
                Subscriber subscriber = (Subscriber) query.setMaxResults(1).uniqueResult();

                System.out.println(subscriber + " subscriber found");
                tx.commit();
                return subscriber;
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                return null;
            }
        }
    }

    @Override
    public Subscriber findOne(Integer codUnic) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Query query = session.createQuery("from Subscriber where codUnic = :cod", Subscriber.class);
                query.setParameter("cod", codUnic);
                Subscriber subscriber = (Subscriber) query.setMaxResults(1).uniqueResult();

                System.out.println(subscriber + " subscriber found");
                tx.commit();
                return subscriber;
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                return null;
            }
        }
    }

    @Override
    public Iterable<Subscriber> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                List<Subscriber> subscribers = session.createQuery("from Subscriber", Subscriber.class)
                        .list();

                System.out.println(subscribers.size() + " subscriber(s) found");
                for (Subscriber b : subscribers) {
                    System.out.println(b.getNume() + " " + b.getCodUnic());
                }
                tx.commit();
                return subscribers;
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                return null;
            }
        }
    }

    @Override
    public Subscriber save(Subscriber entity) {
        // NOT NECESSARY FOR RUNNING THE APP (used only to populate the database at the beginning)
        if (entity == null) {
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");
        }

        if (this.findOne(entity.getCodUnic()) != null)
            throw new ValidationException("DUPLICAT GASIT!");

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Subscriber subscriber = new Subscriber(entity.getCNP(), entity.getNume(), entity.getAdresa(), entity.getTelefon(), entity.getCodUnic(), entity.getParola());
                session.save(subscriber);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Subscriber delete(Integer codUnic) {
        // NOT NECESSARY FOR RUNNING THE APP (used only to populate the database at the beginning)
        if (codUnic == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi NULL!");
        }
        Subscriber subscriber = findOne(codUnic);
        if (subscriber != null) { // if it makes sense looking for it
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    Query query = session.createQuery("from Subscriber where codUnic = :cod", Subscriber.class);
                    query.setParameter("cod", codUnic);
                    Subscriber crit = (Subscriber) query.setMaxResults(1).uniqueResult();

                    System.err.println("delete subscriber " + crit.getCodUnic());
                    session.delete(crit);
                    tx.commit();
                    return subscriber;
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
    public Subscriber update(Subscriber entity) {
        // NOT NECESSARY FOR RUNNING THE APP (used only to populate the database at the beginning)
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        }
        if (this.findOne(entity.getCodUnic()) != null) {
            Subscriber old = this.findOne(entity.getCodUnic());
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    Query query =  session.createQuery("update Subscriber b set b.nume = :nume, b.adresa = :adres, b.CNP = :cnp, b.parola = :parola, b.telefon = :telefon where b.codUnic = :cod");
                    query.setParameter("cod", entity.getCodUnic());
                    query.setParameter("nume", entity.getNume());
                    query.setParameter("adres", entity.getAdresa());
                    query.setParameter("cnp", entity.getCNP());
                    query.setParameter("parola", entity.getParola());
                    query.setParameter("telefon", entity.getTelefon());
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
}
