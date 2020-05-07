package Repository;

import Domain.Bibliotecar;
import Repository.postgres.BibliotecarDataBaseRepository;

public class DBRepositoryBibliotecar {

    BibliotecarDataBaseRepository repo;

    public DBRepositoryBibliotecar(BibliotecarDataBaseRepository repo) {
        this.repo = repo;
    }

    public Bibliotecar findByCredentials(int cod, String password) {
        return repo.findClientByCredentials(cod, password);
    }
}
