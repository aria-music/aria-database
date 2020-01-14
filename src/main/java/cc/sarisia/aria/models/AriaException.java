package cc.sarisia.aria.models;

public class AriaException extends Exception {
    private String message;

    public AriaException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
