package Repository;

public class OverdueError extends RuntimeException {
    public OverdueError() { }

    public OverdueError(String message) {
        super(message);
    }

    public OverdueError(String message, Throwable cause) {
        super(message, cause);
    }

    public OverdueError(Throwable cause) {
        super(cause);
    }

    public OverdueError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
