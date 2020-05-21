package Repository.iss.hbm;

import Domain.iss.BookCopy;
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


public class CopyHBMRepo implements CrudRepository<Integer, BookCopy> {
    private static SessionFactory sessionFactory;

    public CopyHBMRepo() {
        initialize();
    }

    @Override
    public BookCopy findOne(Integer codUnic) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Query query = session.createQuery("from BookCopy where codUnic = :cod", BookCopy.class);
                query.setParameter("cod", codUnic);
                BookCopy bookCopy = (BookCopy) query.setMaxResults(1).uniqueResult();

                System.out.println(bookCopy + " bookCopy found");
                tx.commit();
                return bookCopy;
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                return null;
            }
        }
    }

    @Override
    public Iterable<BookCopy> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                List<BookCopy> bookCopies = session.createQuery("from BookCopy", BookCopy.class)
                        .list();

                System.out.println(bookCopies.size() + " book copy(ies) found");
                for (BookCopy b : bookCopies) {
                    System.out.println(b.getRefer() + " " + b.getCodUnic());
                }
                tx.commit();
                return bookCopies;
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                return null;
            }
        }
    }

    @Override
    public BookCopy save(BookCopy entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");
        }

        if (this.findOne(entity.getCodUnic()) != null)
            throw new ValidationException("DUPLICAT GASIT!");

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                BookCopy bookCopy = new BookCopy(entity.getCodUnic(), entity.getRefer());
                session.save(bookCopy);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public BookCopy delete(Integer codUnic) {
        if (codUnic == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi NULL!");
        }
        BookCopy bookCopy = findOne(codUnic);
        if (bookCopy != null) { // if it makes sense looking for it
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    Query query = session.createQuery("from BookCopy where codUnic = :cod", BookCopy.class);
                    query.setParameter("cod", codUnic);
                    BookCopy crit = (BookCopy) query.setMaxResults(1).uniqueResult();

                    System.err.println("delete book copy " + crit.getCodUnic());
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
    public BookCopy update(BookCopy entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        }
        if (this.findOne(entity.getCodUnic()) != null) {
            BookCopy old = this.findOne(entity.getCodUnic());
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    // validation is done one application layer above this one
                    Query query =  session.createQuery("update BookCopy b set b.refer = :refe where b.codUnic = :cod");
                    query.setParameter("cod", entity.getCodUnic());
                    query.setParameter("refe", entity.getRefer());
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
