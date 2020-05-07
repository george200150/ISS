package Domain;

public class ExemplarCarte {
    private int codUnic;
    private Carte refer;

    public ExemplarCarte(int codUnic, Carte refer) {
        this.codUnic = codUnic;
        this.refer = refer;
    }

    public ExemplarCarte(int codUnic, String titlu, String ISBN, String autor, String editura, int anAparitie) {
        this.codUnic = codUnic;
        this.refer = new Carte(titlu, ISBN, autor, editura, anAparitie);
    }

    public int getCodUnic() {
        return codUnic;
    }

    public void setCodUnic(int codUnic) {
        this.codUnic = codUnic;
    }

    public Carte getRefer() {
        return refer;
    }

    public void setRefer(Carte refer) {
        this.refer = refer;
    }

    public String getISBN(){
        return refer.getISBN();
    }

    public void setISBN(String ISBN){
        this.refer.setISBN(ISBN);
    }

    public String getAutor(){
        return refer.getAutor();
    }

    public void setAutor(String autor){
        refer.setAutor(autor);
    }

    public String getEditura(){
        return refer.getEditura();
    }

    public void setEditura(String editura){
        refer.setEditura(editura);
    }

    public String getTitlu(){
        return refer.getTitlu();
    }

    public void setTitlu(String titlu){
        refer.setTitlu(titlu);
    }

    public int getAnAparitie(){
        return refer.getAnAparitie();
    }

    public void setAnAparitie(int anAparitie){
        refer.setAnAparitie(anAparitie);
    }

    @Override
    public String toString() {
        return refer.toString();
    }
}
