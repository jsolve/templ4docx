package pl.jsolve.templ4docx.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import pl.jsolve.sweetener.io.Resources;
import pl.jsolve.templ4docx.cleaner.DocumentCleaner;
import pl.jsolve.templ4docx.exception.OpenDocxException;
import pl.jsolve.templ4docx.executor.DocumentExecutor;
import pl.jsolve.templ4docx.extractor.VariablesExtractor;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * The main class responsible for reading docx template, finding variables, saving completed docx template
 * @author Lukasz Stypka
 */
public class Docx implements Serializable {

    private static final long serialVersionUID = 1L;

    private VariablePattern variablePattern = new VariablePattern("${", "}");

    /**
     * Path to .docx template file
     */
    private String docxPath;

    /**
     * XWPFDocument object with opened .docx template file
     */
    private XWPFDocument docx = null;

    /**
     * Document cleaner service, which is used to merge split variable
     */
    private DocumentCleaner documentCleaner;

    public Docx(String docxPath) {
        this.docxPath = docxPath;
        open();
        this.documentCleaner = new DocumentCleaner();
    }

    /**
     * Open .docx template file and create new object of XWPFDocument class
     */
    private void open() {
        try {
            FileInputStream fis = new FileInputStream(docxPath);
            docx = new XWPFDocument(fis);
        } catch (Exception ex) {
            throw new OpenDocxException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Finds list of variables in the template file which satisfy the variable pattern
     * @return list of found variables
     */
    public List<String> findVariables() {
        VariablesExtractor extractor = new VariablesExtractor();
        String content = readTextContent();
        return extractor.extract(content, variablePattern);
    }

    /**
     * Returns text content as String
     * @return String text content of template file
     */
    public String readTextContent() {
        XWPFWordExtractor extractor = null;
        try {
            extractor = new XWPFWordExtractor(docx);
            return extractor.getText();
        } finally {
            if (extractor != null) {
                Resources.closeStream(extractor);
            }
        }
    }

    /**
     * Method replaces found variable to value provided by user
     * @param variables
     */
    public void fillTemplate(Variables variables) {
        documentCleaner.clean(this, variables, variablePattern);
        DocumentExecutor documentExecutor = new DocumentExecutor(variables);
        documentExecutor.execute(this);
    }

    /**
     * Save filled template as new .docx file
     * @param outputPath
     */
    public void save(String outputPath) {
        try {
            docx.write(new FileOutputStream(outputPath));
        } catch (Exception ex) {
            throw new OpenDocxException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Save filled template as new .docx file
     * @param outputStream
     */
    public void save(OutputStream outputStream) {
        try {
            docx.write(outputStream);
        } catch (Exception ex) {
            throw new OpenDocxException(ex.getMessage(), ex.getCause());
        }
    }

    /**
     * Set variable pattern which indicates how the variable looks like
     * @param variablePattern
     */
    public void setVariablePattern(VariablePattern variablePattern) {
        this.variablePattern = variablePattern;
    }

    /**
     * Returns opened XWPFDocument
     * @return XWPFDocument
     */
    public XWPFDocument getXWPFDocument() {
        return docx;
    }

}
