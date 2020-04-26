package Utils;


import Domain.ExemplarCarte;

public class ExemplarStateChangeEvent implements Event {
    private ChangeEventType type;
    private ExemplarCarte data, oldData;

    public ExemplarStateChangeEvent(ChangeEventType type, ExemplarCarte data){
        this.type = type;
        this.data = data;
    }

    public ExemplarStateChangeEvent(ChangeEventType type, ExemplarCarte data, ExemplarCarte oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() { return type; }

    public ExemplarCarte getData() { return data; }

    public ExemplarCarte getOldData() { return oldData; }
}
