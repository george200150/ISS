package Domain.iss;

public class Subscriber extends Employee {
    private String CNP;
    private String nume;
    private String adresa;
    private String telefon;

    private String parola;

    public Subscriber(String CNP, String nume, String adresa, String telefon, int codUnic, String parola) {
        super(codUnic);
        this.CNP = CNP;
        this.nume = nume;
        this.adresa = adresa;
        this.telefon = telefon;
        this.parola = parola;
    }

    public Subscriber() {

    }

    public String getCNP() {
        return CNP;
    }

    public void setCNP(String CNP) {
        this.CNP = CNP;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
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

    @Override
    public String toString() {
        return "Abonat " + nume;
    }
}
