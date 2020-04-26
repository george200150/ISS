package Domain;


public class ExemplarCarteDTO{
    private int codUnic;
    private Carte refer;
    private ExemplarCarte exemplarCarte;

    private String titlu;
    private String ISBN;
    private String autor;
    private String editura;
    private int anAparitie;

    public ExemplarCarteDTO(int codUnic, Carte refer) {
        this.codUnic = codUnic;
        this.refer = refer;
        this.exemplarCarte = new ExemplarCarte(codUnic, refer);

        this.titlu = refer.getTitlu();
        this.ISBN = refer.getISBN();
        this.autor = refer.getAutor();
        this.editura = refer.getEditura();
        this.anAparitie = refer.getAnAparitie();
    }
    public ExemplarCarte getExemplarCarte() { return exemplarCarte; }
    public void setExemplarCarte(ExemplarCarte exemplarCarte) { this.exemplarCarte = exemplarCarte; }
    public int getCodUnic() { return codUnic; }
    public void setCodUnic(int codUnic) { this.codUnic = codUnic; }
    public Carte getRefer() { return refer; }
    public void setRefer(Carte refer) { this.refer = refer; }
    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }
    public String getISBN() { return ISBN; }
    public void setISBN(String ISBN) { this.ISBN = ISBN; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getEditura() { return editura; }
    public void setEditura(String editura) { this.editura = editura; }
    public int getAnAparitie() { return anAparitie; }
    public void setAnAparitie(int anAparitie) { this.anAparitie = anAparitie; }
    @Override
    public String toString() { return "codUnic=" + codUnic + ", titlu='" + titlu + ", ISBN='" + ISBN + ", autor='" + autor + ", editura='" + editura + ", anAparitie=" + anAparitie; }
}