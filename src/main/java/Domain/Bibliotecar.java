package Domain;

public class Bibliotecar {
    private int codUnic;
    private String parola;

    public Bibliotecar(int codUnic, String parola) {
        this.codUnic = codUnic;
        this.parola = parola;
    }

    public int getCodUnic() {
        return codUnic;
    }

    public void setCodUnic(int codUnic) {
        this.codUnic = codUnic;
    }

    public String getParola() {
        return parola;
    }

    public void setParola(String parola) {
        this.parola = parola;
    }
}
