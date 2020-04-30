package Repository.postgres;

import Domain.Abonat;
import Repository.ValidationException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AbonatDataBaseRepository implements CrudRepository<Integer, Abonat> {
    private Connection connection;
    //TODO: private Validator<Abonat> validator;

    public AbonatDataBaseRepository(/*TODO: Validator<Abonat> validator*/) {
        this.connection = JDBCInvariant.getConnection();
        //TODO: this.validator = validator;
    }

    public Abonat findClientByCredentials(int codUnic, String password) throws IllegalArgumentException {
        if (password == null) {
            throw new IllegalArgumentException("PAROLA NU POATE FI NULL");
        }
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Abonati\"  WHERE \"codUnic\" =" + "\'" + codUnic + "\' AND parola =" + "\'" + password + "\'");
            boolean existent = data.next();
            if(existent) {
                //int codUnic = data.getInt(1);
                String CNP = data.getString(2);
                String nume = data.getString(3);
                String adresa = data.getString(4);
                String telefon = data.getString(5);
                String parola = data.getString(6);

                Abonat abonat = new Abonat(CNP, nume, adresa, telefon, codUnic, parola);
                return abonat;
            }
            else
                return null;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

        @Override
    public Abonat findOne(Integer id) throws IllegalArgumentException {
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Abonati\"  WHERE \"codUnic\" =" + "\'" + id + "\'");
            data.next();//TODO: sql injection prone
            //int codUnic = data.getInt(1);
            String CNP = data.getString(2);
            String nume = data.getString(3);
            String adresa = data.getString(4);
            String telefon = data.getString(5);
            String parola = data.getString(6);

            Abonat abonat = new Abonat(CNP,nume,adresa,telefon,id,parola);
            return abonat;
        } catch (SQLException ignored) {
        }
        return null;
    }

    @Override
    public Iterable<Abonat> findAll() {
        List<Abonat> lst = new ArrayList<>();
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Abonati\"");
            while (data.next()) {
                int codUnic = data.getInt(1);
                String CNP = data.getString(2);
                String nume = data.getString(3);
                String adresa = data.getString(4);
                String telefon = data.getString(5);
                String parola = data.getString(6);

                Abonat abonat = new Abonat(CNP,nume,adresa,telefon,codUnic,parola);
                lst.add(abonat);
            }
        } catch (SQLException ignored) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return lst;
    }


    @Override
    public Abonat save(Abonat entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");
        }
        //TODO: validator.validate(entity);
        if (findOne(entity.getCodUnic()) != null) {
            throw new ValidationException("DUPLICAT GASIT!");
        }

        try {
            connection.createStatement().execute("INSERT INTO \"Abonati\" VALUES (" +
                    entity.getCodUnic() + ",\'" +
                    entity.getCNP() + "\',\'" +
                    entity.getNume() + "\',\'" +
                    entity.getAdresa() + "\',\'" +
                    entity.getTelefon() + "\',\'" +
                    entity.getParola() + "\')"
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Abonat delete(Integer id) throws IllegalArgumentException {
        Abonat abonat = findOne(id);
        if (abonat != null) {
            try {
                connection.createStatement()
                        .execute("DELETE FROM \"Abonati\" WHERE \"codUnic\" = " + "\'" +  id + "\'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return abonat;
    }

    @Override
    public Abonat update(Abonat entity) {
        if (entity == null){
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        }
        //TODO: validator.validate(entity);
        if (findOne(entity.getCodUnic()) != null) {
            Abonat old = findOne(entity.getCodUnic());
            try {
                connection.createStatement().execute("UPDATE \"Abonati\" SET " +
                        "\"CNP\" = \'" + entity.getCNP() + "\'" +
                        ",nume = \'" + entity.getNume() + "\'" +
                        ",\"adresa\" = \'" + entity.getAdresa() + "\'" + // trebuie cu escape backslash unde e nevoie de case sensitivity
                        ",telefon = \'" + entity.getTelefon() + "\'" +
                        ",\"parola\" = \'" + entity.getParola() + "\'" + "WHERE \"codUnic\" =" + "\'" + entity.getCodUnic() + "\'"
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return old;
        }
        return null;
    }
}
