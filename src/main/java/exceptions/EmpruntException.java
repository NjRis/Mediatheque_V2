package exceptions;

public class EmpruntException extends Exception {
    public EmpruntException(String message) {
        super("[EMPRUNT] " + message);
    }
}
