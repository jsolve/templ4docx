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
 * MS Office has limitations of bookmark names length and content ({@code ^(?!\d)\w{1,40}$}). So we need test this.
 *
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestLongNamesAfterMSOfficeEdit extends AbstractMetaTest {

    @Test
    public void test() throws IOException {
        String templateFileName = "meta/long-names-processed-edited-in-ms-office";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);

        String text = docx.readTextContent();
        assertEquals(
                "This is test simple edited template with three variables with long names: value01, value02, value03. File edited in MS Office.",
                text.trim());

        docx.setProcessMetaInformation(true);
        docx.setVariablePattern(new VariablePattern("${", "}"));

        Variables var = new Variables();
        var.addTextVariable(new TextVariable("${variableWithVeryVeryLongName01}", "valueAfterEditInMSOffice01"));
        var.addTextVariable(
                new TextVariable("${variableWithVeryVeryVeryVeryVeryVeryLongName02}", "valueAfterEditInMSOffice02"));
        var.addTextVariable(
                new TextVariable("${variableWithVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryVeryLongName03}",
                        "valueAfterEditInMSOffice03"));

        docx.fillTemplate(var);

        String tmpPath = System.getProperty("java.io.tmpdir");
        String processedPath = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed" + ".docx");

        docx.save(processedPath);

        text = docx.readTextContent();
        assertEquals(
                "This is test simple edited template with three variables with long names: valueAfterEditInMSOffice01, valueAfterEditInMSOffice02, valueAfterEditInMSOffice03. File edited in MS Office.",
                text.trim());

        is.close();

    }

}
