package Repository.postgres;

import Domain.Bibliotecar;
import Repository.ValidationException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class BibliotecarDataBaseRepository implements CrudRepository<Integer, Bibliotecar> {
    private Connection connection;
    //TODO: private Validator<Bibliotecari> validator;

    public BibliotecarDataBaseRepository(/*TODO: Validator<Bibliotecari> validator*/) {
        this.connection = JDBCInvariant.getConnection();
        //TODO: this.validator = validator;
    }

    public Bibliotecar findClientByCredentials(String password) throws IllegalArgumentException {
        if (password == null) {
            throw new IllegalArgumentException("PAROLA NU POATE FI NULL");
        }
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Bibliotecari\"  WHERE \"parola\" =" + "\'" + password + "\'");
            boolean existent = data.next();
            if(existent){
                int codUnic = data.getInt(1);
                String parola = data.getString(2);

                Bibliotecar bibliotecar = new Bibliotecar(codUnic, parola);
                return bibliotecar;
            }
            else
                return null;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    @Override
    public Bibliotecar findOne(Integer id) throws IllegalArgumentException {
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Bibliotecari\"  WHERE \"codUnic\" =" + "\'" + id + "\'");
            data.next();//TODO: sql injection prone
            int codUnic = data.getInt(1);
            String parola = data.getString(2);

            Bibliotecar bibliotecar = new Bibliotecar(codUnic,parola);
            return bibliotecar;
        } catch (SQLException ignored) {
        }
        return null;
    }

    @Override
    public Iterable<Bibliotecar> findAll() {
        List<Bibliotecar> lst = new ArrayList<>();
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Bibliotecari\"");
            while (data.next()) {
                int codUnic = data.getInt(1);
                String parola = data.getString(2);

                Bibliotecar bibliotecar = new Bibliotecar(codUnic,parola);
                lst.add(bibliotecar);
            }
        } catch (SQLException ignored) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return lst;
    }


    @Override
    public Bibliotecar save(Bibliotecar entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");
        }
        //TODO: validator.validate(entity);
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
    public Bibliotecar delete(Integer id) throws IllegalArgumentException {
        Bibliotecar bibliotecar = findOne(id);
        if (bibliotecar != null) {
            try {
                connection.createStatement()
                        .execute("DELETE FROM \"Bibliotecari\" WHERE \"codUnic\" = " + "\'" +  id + "\'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bibliotecar;
    }

    @Override
    public Bibliotecar update(Bibliotecar entity) {
        if (entity == null){
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        }
        //TODO: validator.validate(entity);
        if (findOne(entity.getCodUnic()) != null) {
            Bibliotecar old = findOne(entity.getCodUnic());
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

