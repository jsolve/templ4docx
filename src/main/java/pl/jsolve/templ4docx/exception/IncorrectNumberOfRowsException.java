package pl.jsolve.templ4docx.exception;

/**
 * Thrown when number of values for given variable is different than number of values for another variable in the same
 * row. For example: Table contains row with two cells with the following variables: ${value1} and ${value2}. The
 * exception is thrown when size of list of values for ${value1} is different than size of list of values for ${value2} 
 * 
 * @author Lukasz Stypka
 */
public class IncorrectNumberOfRowsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IncorrectNumberOfRowsException(String message) {
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
