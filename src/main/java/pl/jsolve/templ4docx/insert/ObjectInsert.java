package pl.jsolve.templ4docx.insert;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import pl.jsolve.templ4docx.util.Key;

/**
 * Insert for object variable.
 *
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class ObjectInsert extends Insert {

    /**
     * Paragraph which contains text variable
     */
    private XWPFParagraph paragraph;

    public ObjectInsert(Key key, XWPFParagraph paragraph) {
        super(key);
        this.paragraph = paragraph;
    }

    public XWPFParagraph getParagraph() {
        return paragraph;
    }

}