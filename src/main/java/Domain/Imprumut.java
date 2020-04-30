package Domain;


import java.util.Date;

public class Imprumut {
    private int codUnicImprumut;
    private Date dataEfectuare;
    private Date dataRestituire;
    private boolean aFostReturnat;
    //private Abonat creator;
    //private ExemplarCarte exemplar;
    private int creator;
    private int exemplar;

    //public Imprumut(int codUnicImprumut, Date dataEfectuare, Date dataRestituire, boolean aFostReturnat, Abonat creator, ExemplarCarte exemplar) {
    public Imprumut(int codUnicImprumut, Date dataEfectuare, Date dataRestituire, boolean aFostReturnat, int creator, int exemplar) {
        this.codUnicImprumut = codUnicImprumut;
        this.dataEfectuare = dataEfectuare;
        this.dataRestituire = dataRestituire;
        this.aFostReturnat = aFostReturnat;
        this.creator = creator;
        this.exemplar = exemplar;
    }

    public int getCodUnicImprumut() {
        return codUnicImprumut;
    }

    public void setCodUnicImprumut(int codUnicImprumut) {
        this.codUnicImprumut = codUnicImprumut;
    }

    public Date getDataEfectuare() {
        return dataEfectuare;
    }

    public void setDataEfectuare(Date dataEfectuare) {
        this.dataEfectuare = dataEfectuare;
    }

    public Date getDataRestituire() {
        return dataRestituire;
    }

    public void setDataRestituire(Date dataRestituire) {
        this.dataRestituire = dataRestituire;
    }

    public boolean isaFostReturnat() {
        return aFostReturnat;
    }

    public void setaFostReturnat(boolean aFostReturnat) {
        this.aFostReturnat = aFostReturnat;
    }

//    public Abonat getCreator() {
//        return creator;
//    }
//
//    public void setCreator(Abonat creator) {
//        this.creator = creator;
//    }
//
//    public ExemplarCarte getExemplar() {
//        return exemplar;
//    }
//
//    public void setExemplar(ExemplarCarte exemplar) {
//        this.exemplar = exemplar;
//    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public int getExemplar() {
        return exemplar;
    }

    public void setExemplar(int exemplar) {
        this.exemplar = exemplar;
    }
}
