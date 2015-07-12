package pl.jsolve.templ4docx.exception;

public class OpenDocxException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OpenDocxException(String message, Throwable cause) {
		super(message, cause);
	}
}
