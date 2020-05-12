package Domain;

public class BookCopy {
    private int codUnic;
    private Book refer;

    public BookCopy(int codUnic, Book refer) {
        this.codUnic = codUnic;
        this.refer = refer;
    }

    public BookCopy(int codUnic, String titlu, String ISBN, String autor, String editura, int anAparitie) {
        this.codUnic = codUnic;
        this.refer = new Book(titlu, ISBN, autor, editura, anAparitie);
    }

    public int getCodUnic() {
        return codUnic;
    }

    public void setCodUnic(int codUnic) {
        this.codUnic = codUnic;
    }

    public Book getRefer() {
        return refer;
    }

    public void setRefer(Book refer) {
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
