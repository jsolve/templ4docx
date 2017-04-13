package pl.jsolve.sweetener.exception;

public class InvalidArgumentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidArgumentException(String message) {
        super(message);
    }

}
