package Repository.iss.hbm;

import Domain.iss.Book;
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


public class BookHBMRepo implements CrudRepository<String, Book> {
    static SessionFactory sessionFactory;

    public BookHBMRepo() {
        initialize();
    }

    @Override
    public Book findOne(String codUnic) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Query query = session.createQuery("from Book where ISBN = :isbn", Book.class);
                query.setParameter("isbn", codUnic);
                Book book = (Book) query.setMaxResults(1).uniqueResult();

                System.out.println(book + " book found");
                tx.commit();
                return book;
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                return null;
            }
        }
    }

    @Override
    public Iterable<Book> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                List<Book> books = session.createQuery("from Book", Book.class)
                        .list();

                System.out.println(books.size() + " book(s) found");
                for (Book b : books) {
                    System.out.println(b.getTitlu() + " " + b.getISBN());
                }
                tx.commit();
                return books;
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                return null;
            }
        }
    }

    @Override
    public Book save(Book entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");
        }

        if (this.findOne(entity.getISBN()) != null)
            throw new ValidationException("DUPLICAT GASIT!");

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Book book = new Book(entity.getTitlu(), entity.getISBN(), entity.getAutor(), entity.getEditura(), entity.getAnAparitie());
                session.save(book);
                tx.commit();
            } catch (RuntimeException ex) {
                if (tx != null)
                    tx.rollback();
            }
        }
        return null;
    }

    @Override
    public Book delete(String codUnic) {
        if (codUnic == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi NULL!");
        }
        Book book = findOne(codUnic);
        if (book != null) { // if it makes sense looking for it
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    Query query = session.createQuery("from Book where ISBN = :isbn", Book.class);
                    query.setParameter("isbn", codUnic);
                    Book crit = (Book) query.setMaxResults(1).uniqueResult();

                    System.err.println("delete book " + crit.getISBN());
                    session.delete(crit);
                    tx.commit();
                    return book;
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
    public Book update(Book entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        }
        if (this.findOne(entity.getISBN()) != null) {
            Book old = this.findOne(entity.getISBN());
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    // validation is done one application layer above this one
                    Query query =  session.createQuery("update Book b set b.anAparitie = :an, b.autor = :aut, b.editura = :editu, b.titlu = :titlu where b.ISBN = :isbn");
                    query.setParameter("isbn", entity.getISBN());
                    query.setParameter("an", entity.getAnAparitie());
                    query.setParameter("aut", entity.getAutor());
                    query.setParameter("editu", entity.getEditura());
                    query.setParameter("titlu", entity.getTitlu());
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

    /**
     * This method will string match all the input fields received
     * (encapsulated in a book - id does not matter when searching)
     * and return the book that is identic to @param{book1}.
     * This validation is necessary in order to avoid creating a copy of a non existing book.
     *
     * The id of the copy is not necessary to be validated, as there is an in-place validation
     * of it in the @method{save()} from @class{CopyHBMRepo}, so we always avoid duplicated PK-s.
     */
    public Book findEquivalent(Book book1) {

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();

                Query query = session.createQuery("from Book where autor = :aut and anAparitie = :anap and editura = :editu and titlu = :titl and ISBN = :isbn", Book.class);
                query.setParameter("aut", book1.getAutor());
                query.setParameter("anap", book1.getAnAparitie());
                query.setParameter("editu", book1.getEditura());
                query.setParameter("titl", book1.getTitlu());
                query.setParameter("isbn", book1.getISBN());
                Book book = (Book) query.setMaxResults(1).uniqueResult();

                System.out.println(book + " book found");
                tx.commit();
                return book;
            } catch (RuntimeException e) {
                if (tx != null)
                    tx.rollback();
                return null;
            }
        }
    }
}
