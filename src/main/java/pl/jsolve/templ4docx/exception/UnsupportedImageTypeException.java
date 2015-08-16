package pl.jsolve.templ4docx.exception;

/**
 * Thrown when image type is not supported. The supported image types are: .emf, .wmf, .pict, .jpeg, .jpg, .png, .dib,
 * .gif, .tiff, .eps, .bmp, .wpg
 * @author Lukasz Stypka
 */
public class UnsupportedImageTypeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnsupportedImageTypeException(String message) {
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
