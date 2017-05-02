package pl.jsolve.templ4docx.tests.meta;

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
public class TestNonWords extends AbstractMetaTest {

    @Test
    public void test() throws IOException {
        String templateFileName = "meta/non-words";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);
        docx.setProcessMetaInformation(true);
        docx.setVariablePattern(new VariablePattern("P{", "}"));

        Variables var = new Variables();
        var.addTextVariable(new TextVariable("P{UNDESCORE_AND.DOT01}", "value01"));
        var.addTextVariable(new TextVariable("P{UNDESCORE_AND.DOT02}", "value02"));
        var.addTextVariable(new TextVariable("P{UNDESCORE_AND.DOT03}", "value03"));

        docx.fillTemplate(var);

        String tmpPath = System.getProperty("java.io.tmpdir");
        String processedPath = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed" + ".docx");

        docx.save(processedPath);

        String text = docx.readTextContent();
        assertEquals(
                "This is test simple template with three variables with «non-words» symbols in name: value01, value02, value03.",
                text.trim());

        is.close();

        docx = new Docx(processedPath);
        docx.setProcessMetaInformation(true);
        docx.setVariablePattern(new VariablePattern("P{", "}"));

        var = new Variables();
        var.addTextVariable(new TextVariable("P{UNDESCORE_AND.DOT01}", "value011"));
        var.addTextVariable(new TextVariable("P{UNDESCORE_AND.DOT02}", "value022"));
        var.addTextVariable(new TextVariable("P{UNDESCORE_AND.DOT03}", "value033"));

        docx.fillTemplate(var);

        String processedPath2 = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed2" + ".docx");

        docx.save(processedPath2);

        text = docx.readTextContent();
        assertEquals(
                "This is test simple template with three variables with «non-words» symbols in name: value011, value022, value033.",
                text.trim());
    }

}
