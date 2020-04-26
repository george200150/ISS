package Domain;

public class ExemplarCarte {
    private int codUnic;
    private Carte refer;

    public ExemplarCarte(int codUnic, Carte refer) {
        this.codUnic = codUnic;
        this.refer = refer;
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
}
