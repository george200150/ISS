package Repository;

import Domain.Abonat;
import Repository.postgres.AbonatDataBaseRepository;

public class DBRepositoryAbonat {
    AbonatDataBaseRepository repo;

    public DBRepositoryAbonat(AbonatDataBaseRepository repo) {
        this.repo = repo;
    }

    public Abonat findByCredentials(int codAbonat, String password) {
        return repo.findClientByCredentials(codAbonat,password);
    }

    public Abonat findById(int codAbonat) {
        return repo.findOne(codAbonat);
    }
}
