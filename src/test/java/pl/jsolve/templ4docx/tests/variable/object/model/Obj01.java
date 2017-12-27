package pl.jsolve.templ4docx.tests.variable.object.model;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class Obj01 {

    NestedObj01 field1 = new NestedObj01("field1Value");

    public NestedObj01 getField1() {
        return field1;
    }

    @Override
    public String toString() {
        return "value01";
    }

}
