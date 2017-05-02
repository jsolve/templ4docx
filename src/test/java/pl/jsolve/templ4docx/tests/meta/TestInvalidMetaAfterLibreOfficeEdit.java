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
public class TestInvalidMetaAfterLibreOfficeEdit extends AbstractMetaTest {

    @Test
    public void test() throws IOException {
        String templateFileName = "meta/invalid-meta-edited-in-libre-office";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);

        String text = docx.readTextContent();
        assertEquals(
                "This is test simple template with three variables: valu invalid_data is here e01, valu ${var02} e02, valu ${var01} e03. File edited in Libre Office.",
                text.trim());

        docx.setProcessMetaInformation(true);
        docx.setVariablePattern(new VariablePattern("${", "}"));

        Variables var = new Variables();
        var.addTextVariable(new TextVariable("${var01}", "valueAfterEditInLibreOffice01"));
        var.addTextVariable(new TextVariable("${var02}", "valueAfterEditInLibreOffice02"));
        var.addTextVariable(new TextVariable("${var03}", "valueAfterEditInLibreOffice03"));

        docx.fillTemplate(var);

        String tmpPath = System.getProperty("java.io.tmpdir");
        String processedPath = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed" + ".docx");

        docx.save(processedPath);

        text = docx.readTextContent();
        assertEquals(
                "This is test simple template with three variables: valueAfterEditInLibreOffice01, valueAfterEditInLibreOffice02, valueAfterEditInLibreOffice03. File edited in Libre Office.",
                text.trim());

        is.close();

    }

}
