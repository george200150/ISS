package Repository;

import Domain.Abonat;

public class DBRepositoryAbonat {
    public Abonat findByCredentials(int codAbonat, String password) {
        return new Abonat("1234","da","avem","este",1111,"1");
        // TODO: return null; implement this !!!
    }

    public Abonat findById(int codAbonat) {
        return null;
    }
}
