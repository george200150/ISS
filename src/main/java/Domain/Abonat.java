package Domain;

public class Abonat {
    private String CNP;
    private String nume;
    private String adresa;
    private String telefon;
    private int codUnic;
    private String parola;

    public Abonat(String CNP, String nume, String adresa, String telefon, int codUnic, String parola) {
        this.CNP = CNP;
        this.nume = nume;
        this.adresa = adresa;
        this.telefon = telefon;
        this.codUnic = codUnic;
        this.parola = parola;
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
