package Domain.iss;

public class BookCopy {
    private int codUnic;
    private String refer;

    public BookCopy(int codUnic, String refer) {
        this.codUnic = codUnic;
        this.refer = refer;
    }

    public BookCopy() {
    }

    public int getCodUnic() {
        return codUnic;
    }

    public void setCodUnic(int codUnic) {
        this.codUnic = codUnic;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    @Override
    public String toString() {
        return this.codUnic + " " + this.refer;
    }
}
