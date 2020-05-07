package Domain;

public class Carte {
    private String titlu;
    private String ISBN;
    private String autor;
    private String editura;
    private int anAparitie;

    public Carte(String titlu, String ISBN, String autor, String editura, int anAparitie) {
        this.titlu = titlu;
        this.ISBN = ISBN;
        this.autor = autor;
        this.editura = editura;
        this.anAparitie = anAparitie;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditura() {
        return editura;
    }

    public void setEditura(String editura) {
        this.editura = editura;
    }

    public int getAnAparitie() {
        return anAparitie;
    }

    public void setAnAparitie(int anAparitie) {
        this.anAparitie = anAparitie;
    }

    @Override
    public String toString() {
        return '"'+titlu + '"' + " de " + autor + ", publicata de " + editura;
    }
}
