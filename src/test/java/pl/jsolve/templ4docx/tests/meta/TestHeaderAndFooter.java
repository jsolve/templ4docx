package pl.jsolve.templ4docx.tests.meta;

import org.junit.Test;
import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variables;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author Keyon (youngyou[dot]name[at]gmail[dot]com)
 *
 */
public class TestHeaderAndFooter extends AbstractMetaTest {

    @Test
    public void test() throws IOException {
        String templateFileName = "meta/header-and-footer";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);
        docx.setProcessMetaInformation(true);
        docx.setVariablePattern(new VariablePattern("${", "}"));

        Variables var = new Variables();
        var.addTextVariable(new TextVariable("${header01}", "Header"));
        var.addTextVariable(new TextVariable("${footer01}", "Footer"));
        var.addTextVariable(new TextVariable("${var01}", "Value"));

        docx.fillTemplate(var);

        String tmpPath = System.getProperty("java.io.tmpdir");
        String processedPath = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed" + ".docx");

        docx.save(processedPath);

        String text = docx.readTextContent();

        assertEquals("This is header with two variables: Header, Value.\n" +
                "This is test simple template with one variable: Value\n" +
                "This is footer with two variables: Footer, Value.", text.trim());

        is.close();

        docx = new Docx(processedPath);
        docx.setProcessMetaInformation(true);
        docx.setVariablePattern(new VariablePattern("${", "}"));

        var = new Variables();
        var.addTextVariable(new TextVariable("${header01}", "Another Header"));
        var.addTextVariable(new TextVariable("${footer01}", "Another Footer"));
        var.addTextVariable(new TextVariable("${var01}", "Another Value"));

        docx.fillTemplate(var);

        String processedPath2 = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed2" + ".docx");

        docx.save(processedPath2);

        text = docx.readTextContent();
        assertEquals("This is header with two variables: Another Header, Another Value.\n" +
                "This is test simple template with one variable: Another Value\n" +
                "This is footer with two variables: Another Footer, Another Value.", text.trim());
    }
}
