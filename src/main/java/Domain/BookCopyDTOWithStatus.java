package Domain;

public class BookCopyDTOWithStatus {
    private int codUnic;
    private Book refer;
    private BookCopy bookCopy;

    private String titlu;
    private String ISBN;
    private String autor;
    private String editura;
    private int anAparitie;

    private String status;

    public BookCopyDTOWithStatus(int codUnic, Book refer, boolean esteInchiriata) {
        this.codUnic = codUnic;
        this.refer = refer;
        this.bookCopy = new BookCopy(codUnic, refer);
        if(esteInchiriata) {this.status = "inchiriat";} else {this.status = "disponibil";}
        this.titlu = refer.getTitlu();
        this.ISBN = refer.getISBN();
        this.autor = refer.getAutor();
        this.editura = refer.getEditura();
        this.anAparitie = refer.getAnAparitie();
    }
    public BookCopy getBookCopy() { return bookCopy; }
    public void setBookCopy(BookCopy bookCopy) { this.bookCopy = bookCopy; }
    public int getCodUnic() { return codUnic; }
    public void setCodUnic(int codUnic) { this.codUnic = codUnic; }
    public Book getRefer() { return refer; }
    public void setRefer(Book refer) { this.refer = refer; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    @Override
    public String toString() { return "codUnic=" + codUnic + ", titlu='" + titlu + ", ISBN='" + ISBN + ", autor='" + autor + ", editura='" + editura + ", anAparitie=" + anAparitie; }
}
