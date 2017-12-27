package pl.jsolve.templ4docx.tests.variable.object.model;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class StringContainer {

    String value;

    public StringContainer() {

    }

    public StringContainer(String value) {
        super();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
