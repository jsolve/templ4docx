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
public class TestNonWordsAfterLibreOfficeEdit extends AbstractMetaTest {

    @Test
    public void test() throws IOException {
        String templateFileName = "meta/non-words-processed-edited-in-libre-office";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);

        String text = docx.readTextContent();
        assertEquals(
                "This is test simple edited template with three variables with «non-words» symbols in name: value01, value02, value03. File edited in Libre Office.",
                text.trim());

        docx.setProcessMetaInformation(true);
        docx.setVariablePattern(new VariablePattern("P{", "}"));

        Variables var = new Variables();
        var.addTextVariable(new TextVariable("P{UNDESCORE_AND.DOT01}", "valueAfterEditInLibreOffice01"));
        var.addTextVariable(new TextVariable("P{UNDESCORE_AND.DOT02}", "valueAfterEditInLibreOffice02"));
        var.addTextVariable(new TextVariable("P{UNDESCORE_AND.DOT03}", "valueAfterEditInLibreOffice03"));

        docx.fillTemplate(var);

        String tmpPath = System.getProperty("java.io.tmpdir");
        String processedPath = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed" + ".docx");

        docx.save(processedPath);

        text = docx.readTextContent();
        assertEquals(
                "This is test simple edited template with three variables with «non-words» symbols in name: valueAfterEditInLibreOffice01, valueAfterEditInLibreOffice02, valueAfterEditInLibreOffice03. File edited in Libre Office.",
                text.trim());

        is.close();

    }

}
