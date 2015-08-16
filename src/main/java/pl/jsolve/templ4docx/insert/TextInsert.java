package pl.jsolve.templ4docx.insert;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import pl.jsolve.templ4docx.util.Key;

/**
 * Insert for text variable.
 * @author Lukasz Stypka
 */
public class TextInsert extends Insert {

    /**
     * Paragraph which contains text variable
     */
    private XWPFParagraph paragraph;

    public TextInsert(Key key, XWPFParagraph paragraph) {
        super(key);
        this.paragraph = paragraph;
    }

    public XWPFParagraph getParagraph() {
        return paragraph;
    }

}