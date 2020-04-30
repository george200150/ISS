package Repository.postgres;


import Domain.Imprumut;
import Repository.ValidationException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ImprumutDataBaseRepository implements CrudRepository<Integer, Imprumut> {
    private Connection connection;
    //TODO: private Validator<Abonat> validator;

    public ImprumutDataBaseRepository(/*TODO: Validator<Abonat> validator*/) {
        this.connection = JDBCInvariant.getConnection();
        //TODO: this.validator = validator;
    }

    @Override
    public Imprumut findOne(Integer id) throws IllegalArgumentException {
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Imprumuturi\"  WHERE \"codUnicImprumut\" =" + "\'" + id + "\'");
            data.next();//TODO: sql injection prone
            //int codUnicImprumut = data.getInt(1);
            Date dataEfectuare = data.getDate(2);
            Date dataRestituire = data.getDate(3);
            Boolean aFostReturnat = data.getBoolean(4);
            int creator = data.getInt(5);
            int exemplar = data.getInt(6);

            Imprumut imprumut = new Imprumut(id,dataEfectuare,dataRestituire,aFostReturnat,creator,exemplar);
            return imprumut;
        } catch (SQLException ignored) {
        }
        return null;
    }

    @Override
    public Iterable<Imprumut> findAll() {
        List<Imprumut> lst = new ArrayList<>();
        try {
            ResultSet data = connection.createStatement().executeQuery("SELECT * FROM \"Imprumuturi\"");
            while (data.next()) {
                int codUnicImprumut = data.getInt(1);
                Date dataEfectuare = data.getDate(2);
                Date dataRestituire = data.getDate(3);
                Boolean aFostReturnat = data.getBoolean(4);
                int creator = data.getInt(5);
                int exemplar = data.getInt(6);

                Imprumut imprumut = new Imprumut(codUnicImprumut,dataEfectuare,dataRestituire,aFostReturnat,creator,exemplar);
                lst.add(imprumut);
            }
        } catch (SQLException ignored) {
            throw new IllegalArgumentException("Error: Could not connect to the database");
        }
        return lst;
    }


    @Override
    public Imprumut save(Imprumut entity) throws ValidationException {
        if (entity == null) {
            throw new IllegalArgumentException("ENTITATEA NU POATE FI NULL");
        }
        //TODO: validator.validate(entity);

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
    public Imprumut delete(Integer id) throws IllegalArgumentException {
        Imprumut abonat = findOne(id);
        if (abonat != null) {
            try {
                connection.createStatement()
                        .execute("DELETE FROM \"Imprumuturi\" WHERE \"codUnicImprumut\" = " + "\'" +  id + "\'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return abonat;
    }

    @Override
    public Imprumut update(Imprumut entity) {
        if (entity == null){
            throw new IllegalArgumentException("Entitatea nu poate fi NULL!");
        }
        //TODO: validator.validate(entity);
        if (findOne(entity.getCodUnicImprumut()) != null) {
            Imprumut old = findOne(entity.getCodUnicImprumut());
            try {
                connection.createStatement().execute("UPDATE \"Imprumuturi\" SET " +
                        "\"dataEfectuare\" = \'" + entity.getDataEfectuare() + "\'" +
                        ",\"dataRestituire\" = \'" + entity.getDataRestituire() + "\'" +
                        ",\"aFostReturnat\" = \'" + entity.isaFostReturnat() + "\'" + // trebuie cu escape backslash unde e nevoie de case sensitivity
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
