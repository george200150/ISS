package Domain;

import java.time.LocalDate;

public class Imprumut {
    private int codUnicImprumut;
    private LocalDate dataEfectuare;
    private LocalDate dataRestituire;
    private boolean aFostReturnat;
    private Abonat creator;
    private ExemplarCarte exemplar;

    public Imprumut(int codUnicImprumut, LocalDate dataEfectuare, LocalDate dataRestituire, boolean aFostReturnat, Abonat creator, ExemplarCarte exemplar) {
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

    public LocalDate getDataEfectuare() {
        return dataEfectuare;
    }

    public void setDataEfectuare(LocalDate dataEfectuare) {
        this.dataEfectuare = dataEfectuare;
    }

    public LocalDate getDataRestituire() {
        return dataRestituire;
    }

    public void setDataRestituire(LocalDate dataRestituire) {
        this.dataRestituire = dataRestituire;
    }

    public boolean isaFostReturnat() {
        return aFostReturnat;
    }

    public void setaFostReturnat(boolean aFostReturnat) {
        this.aFostReturnat = aFostReturnat;
    }

    public Abonat getCreator() {
        return creator;
    }

    public void setCreator(Abonat creator) {
        this.creator = creator;
    }

    public ExemplarCarte getExemplar() {
        return exemplar;
    }

    public void setExemplar(ExemplarCarte exemplar) {
        this.exemplar = exemplar;
    }
}
