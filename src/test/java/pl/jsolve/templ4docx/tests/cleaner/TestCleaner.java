package pl.jsolve.templ4docx.tests.cleaner;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestCleaner extends AbstractCleanerTest {

    @Test
    public void test() throws IOException {
        String templateFileName = "cleaner/cleaner-template";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);
        is.close();
        docx.setVariablePattern(new VariablePattern("${", "}"));

        Variables var = new Variables();
        var.addTextVariable(new TextVariable("${var01}", "value01"));
        var.addTextVariable(new TextVariable("${var02}", "value02"));
        var.addTextVariable(new TextVariable("${var03}", "value03"));
        var.addTextVariable(new TextVariable("${var01.field1}", "field1Value"));
        var.addTextVariable(new TextVariable("${var02.field2}", "field2Value"));
        var.addTextVariable(new TextVariable("${var03.field3}", "field3Value"));
        var.addTextVariable(new TextVariable("${var01.field1.field11}", "field11Value"));

        docx.fillTemplate(var);

        String tmpPath = System.getProperty("java.io.tmpdir");
        String processedPath = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed" + ".docx");

        docx.save(processedPath);

        String text = docx.readTextContent();
        assertEquals(
                "This is test simple template with three variables: value01, value02, value03. This is nested values of variables: field1Value, field2Value, field3Value. And more: field11Value.",
                text.trim());
    }

}
