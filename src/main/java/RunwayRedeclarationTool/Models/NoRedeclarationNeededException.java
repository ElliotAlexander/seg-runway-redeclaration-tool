package RunwayRedeclarationTool.Models;

public class NoRedeclarationNeededException extends Exception {
    public NoRedeclarationNeededException() {
        super();
    }
    public NoRedeclarationNeededException(String message) {
        super(message);
    }
}
