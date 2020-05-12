package Repository.postgres;


import Domain.BookCopy;
import Repository.ValidationException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExemplarDataBaseRepository implements CrudRepository<Integer, BookCopy> {
    private Connection connection;

    public ExemplarDataBaseRepository() {
        this.connection = JDBCInvariant.getConnection();
    }

    @Override
    public BookCopy findOne(Integer id) throws IllegalArgumentException {
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Exemplare\"  WHERE \"codUnic\" =" + "\'" + id + "\'");
            data.next();//TODO: sql injection prone
            //int codUnic = data.getInt(1);
            String titlu = data.getString(2);
            String ISBN = data.getString(3);
            String autor = data.getString(4);
            String editura = data.getString(5);
            int anAparitie = data.getInt(6);

            BookCopy bookCopy = new BookCopy(id, titlu, ISBN, autor, editura, anAparitie);
            return bookCopy;
        } catch (SQLException ignored) {
        }
        return null;
    }

    @Override
    public Iterable<BookCopy> findAll() {
        List<BookCopy> lst = new ArrayList<>();
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Exemplare\"");
            while (data.next()) {
                int codUnic = data.getInt(1);
                String titlu = data.getString(2);
                String ISBN = data.getString(3);
                String autor = data.getString(4);
                String editura = data.getString(5);
                int anAparitie = data.getInt(6);

                BookCopy bookCopy = new BookCopy(codUnic, titlu, ISBN, autor, editura, anAparitie);
                lst.add(bookCopy);
            }
        } catch (SQLException ignored) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return lst;
    }


    @Override
    public BookCopy save(BookCopy entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");
        }
        if (findOne(entity.getCodUnic()) != null) {
            throw new ValidationException("DUPLICAT GASIT!");
        }

        try {
            connection.createStatement().execute("INSERT INTO \"Exemplare\" VALUES (" +
                    entity.getCodUnic() + ",\'" +
                    entity.getTitlu() + "\',\'" +
                    entity.getISBN() + "\',\'" +
                    entity.getAutor() + "\',\'" +
                    entity.getEditura() + "\',\'" +
                    entity.getAnAparitie() + "\')"
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BookCopy delete(Integer id) throws IllegalArgumentException {
        BookCopy bookCopy = findOne(id);
        if (bookCopy != null) {
            try {
                connection.createStatement()
                        .execute("DELETE FROM \"Exemplare\" WHERE \"codUnic\" = " + "\'" + id + "\'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bookCopy;
    }

    @Override
    public BookCopy update(BookCopy entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        }
        if (findOne(entity.getCodUnic()) != null) {
            BookCopy old = findOne(entity.getCodUnic());
            try {
                connection.createStatement().execute("UPDATE \"Exemplare\" SET " +
                        "\"titlu\" = \'" + entity.getTitlu() + "\'" +
                        ",\"ISBN\" = \'" + entity.getISBN() + "\'" +
                        ",\"autor\" = \'" + entity.getAutor() + "\'" + // trebuie cu escape backslash unde e nevoie de case sensitivity
                        ",editura = \'" + entity.getEditura() + "\'" +
                        ",\"anAparitie\" = \'" + entity.getAnAparitie() + "\'" + "WHERE \"codUnic\" =" + "\'" + entity.getCodUnic() + "\'"
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return old;
        }
        return null;
    }
}
