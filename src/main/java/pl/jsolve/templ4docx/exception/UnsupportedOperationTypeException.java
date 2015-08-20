package pl.jsolve.templ4docx.exception;

/**
 * Thrown when operation type in unsupported in condition string
 *  
 * @author Lukasz Stypka
 */
public class UnsupportedOperationTypeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnsupportedOperationTypeException(String message) {
        super(message);
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
