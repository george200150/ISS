package Repository.iss.hbm;

import Domain.iss.Librarian;
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


public class LibrarianHBMRepo implements CrudRepository<Integer, Librarian> {
    static SessionFactory sessionFactory;

    public LibrarianHBMRepo() {
        initialize();
    }


    public Librarian findByCredentials(int codUnic, String password) throws IllegalArgumentException {
        if (password == null) {
            throw new IllegalArgumentException("PAROLA NU POATE FI NULL");
        }
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Query query = session.createQuery("from Librarian where codUnic = :cod and parola = :parola", Librarian.class);
                query.setParameter("cod", codUnic);
                query.setParameter("parola", password);
                Librarian librarian = (Librarian) query.setMaxResults(1).uniqueResult();

                System.out.println(librarian + " librarian found");
                tx.commit();
                return librarian;
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                return null;
            }
        }
    }

    @Override
    public Librarian findOne(Integer codUnic) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Query query = session.createQuery("from Librarian where codUnic = :cod", Librarian.class);
                query.setParameter("cod", codUnic);
                Librarian librarian = (Librarian) query.setMaxResults(1).uniqueResult();

                System.out.println(librarian + " librarian found");
                tx.commit();
                return librarian;
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                return null;
            }
        }
    }

    @Override
    public Iterable<Librarian> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                List<Librarian> bilete = session.createQuery("from Librarian", Librarian.class)
                        .list();

                System.out.println(bilete.size() + " librarian(s) found");
                for (Librarian b : bilete) {
                    System.out.println(b.getCodUnic() + " " + b.getParola());
                }
                tx.commit();
                return bilete;
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                return null;
            }
        }
    }

    @Override
    public Librarian save(Librarian entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");
        }

        if (this.findOne(entity.getCodUnic()) != null)
            throw new ValidationException("DUPLICAT GASIT!");

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Librarian librarian = new Librarian(entity.getCodUnic(), entity.getParola());
                session.save(librarian);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Librarian delete(Integer codUnic) {
        throw new NullPointerException();
    }

    @Override
    public Librarian update(Librarian entity) {
        throw new NullPointerException();
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
