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
 * MS Office do not allow empty content inside of {@code bookmarkStart} and {@code bookmarkEnd} tags.
 *
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestEmptyMetaAfterMSOfficeEdit extends AbstractMetaTest {

    @Test
    public void test() throws IOException {
        String templateFileName = "meta/empty-meta-edited-in-ms-office";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);

        String text = docx.readTextContent();
        assertEquals("This is test simple template with three variables: , value02,. File edited in MS Office.",
                text.trim());

        docx.setProcessMetaInformation(true);
        docx.setVariablePattern(new VariablePattern("${", "}"));

        Variables var = new Variables();
        var.addTextVariable(new TextVariable("${var01}", "valueAfterEditInMSOffice01"));
        var.addTextVariable(new TextVariable("${var02}", "valueAfterEditInMSOffice02"));
        var.addTextVariable(new TextVariable("${var03}", "valueAfterEditInMSOffice03"));

        docx.fillTemplate(var);

        String tmpPath = System.getProperty("java.io.tmpdir");
        String processedPath = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed" + ".docx");

        docx.save(processedPath);

        text = docx.readTextContent();
        assertEquals(
                "This is test simple template with three variables: , valueAfterEditInMSOffice02,. File edited in MS Office.",
                text.trim());

        is.close();

    }

}
