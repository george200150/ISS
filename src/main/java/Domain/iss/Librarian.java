package Domain.iss;

public class Librarian extends Employee {
    private String parola;

    public Librarian(int codUnic, String parola) {
        super(codUnic);
        this.parola = parola;
    }

    public Librarian() {
        super();
    }

    public int getCodUnic() {
        return super.getCodUnic();
    }

    public void setCodUnic(int codUnic) {
        super.setCodUnic(codUnic);
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }
}
