package Repository.postgres;

import Domain.Librarian;
import Repository.ValidationException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class LibrarianDataBaseRepository implements CrudRepository<Integer, Librarian> {
    private Connection connection;

    public LibrarianDataBaseRepository() {
        this.connection = JDBCInvariant.getConnection();
    }

    public Librarian findByCredentials(int codUnic, String password) throws IllegalArgumentException {
        if (password == null) {
            throw new IllegalArgumentException("PAROLA NU POATE FI NULL");
        }
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Bibliotecari\"  WHERE \"codUnic\" =" + "\'" + codUnic + "\' AND parola =" + "\'" + password + "\'");
            boolean existent = data.next();
            if (existent) {
                //int codUnic = data.getInt(1);
                String parola = data.getString(2);

                Librarian librarian = new Librarian(codUnic, parola);
                return librarian;
            } else
                return null;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    @Override
    public Librarian findOne(Integer id) throws IllegalArgumentException {
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Bibliotecari\"  WHERE \"codUnic\" =" + "\'" + id + "\'");
            data.next();//TODO: sql injection prone
            int codUnic = data.getInt(1);
            String parola = data.getString(2);

            Librarian librarian = new Librarian(codUnic, parola);
            return librarian;
        } catch (SQLException ignored) {
        }
        return null;
    }

    @Override
    public Iterable<Librarian> findAll() {
        List<Librarian> lst = new ArrayList<>();
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Bibliotecari\"");
            while (data.next()) {
                int codUnic = data.getInt(1);
                String parola = data.getString(2);

                Librarian librarian = new Librarian(codUnic, parola);
                lst.add(librarian);
            }
        } catch (SQLException ignored) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return lst;
    }


    @Override
    public Librarian save(Librarian entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");
        }
        if (findOne(entity.getCodUnic()) != null) {
            throw new ValidationException("DUPLICAT GASIT!");
        }

        try {
            connection.createStatement().execute("INSERT INTO \"Bibliotecari\" VALUES (" +
                    entity.getCodUnic() + ",\'" +
                    entity.getParola() + "\')"
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Librarian delete(Integer id) throws IllegalArgumentException {
        Librarian librarian = findOne(id);
        if (librarian != null) {
            try {
                connection.createStatement()
                        .execute("DELETE FROM \"Bibliotecari\" WHERE \"codUnic\" = " + "\'" + id + "\'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return librarian;
    }

    @Override
    public Librarian update(Librarian entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        }
        if (findOne(entity.getCodUnic()) != null) {
            Librarian old = findOne(entity.getCodUnic());
            try {
                connection.createStatement().execute("UPDATE \"Abonati\" SET " +
                        "\"parola\" = \'" + entity.getParola() + "\'" + "WHERE \"codUnic\" =" + "\'" + entity.getCodUnic() + "\'"
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return old;
        }
        return null;
    }
}
