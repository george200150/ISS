package Domain.iss;

public class Employee {
    private int codUnic;

    public Employee(int codUnic) {
        this.codUnic = codUnic;
    }

    public Employee() {

    }

    public int getCodUnic() {
        return codUnic;
    }

    public void setCodUnic(int codUnic) {
        this.codUnic = codUnic;
    }
}
