package Utils;


import Domain.BookCopy;

public class BookCopyStateChangeEvent implements Event {
    private ChangeEventType type;
    private BookCopy data, oldData;

    public BookCopyStateChangeEvent(ChangeEventType type, BookCopy data){
        this.type = type;
        this.data = data;
    }

    public BookCopyStateChangeEvent(ChangeEventType type, BookCopy data, BookCopy oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEventType getType() { return type; }

    public BookCopy getData() { return data; }

    public BookCopy getOldData() { return oldData; }
}
