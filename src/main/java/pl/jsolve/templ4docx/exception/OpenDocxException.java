package pl.jsolve.templ4docx.exception;

/**
 * Thrown when .docx file doesn't exist or when another exception is thrown while template processing.
 * @author Lukasz Stypka
 */
public class OpenDocxException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OpenDocxException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
