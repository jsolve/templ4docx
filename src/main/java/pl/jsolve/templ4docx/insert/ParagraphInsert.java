package pl.jsolve.templ4docx.insert;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import pl.jsolve.templ4docx.util.Key;

/**
 * Abstract paragraph insert.
 * @author Lukasz Stypka
 */
public abstract class ParagraphInsert extends Insert {

    /**
     * If variable is located in table cell
     */
    private XWPFTableCell cellParent;

    private XWPFDocument documentParent;
    /**
     * If variable is located in paragraph, outside a table
     */
    private XWPFParagraph paragraph;

    public ParagraphInsert(Key key, XWPFParagraph paragraph, XWPFTableCell cellParent, XWPFDocument documentParent) {
        super(key);
        this.paragraph = paragraph;
        this.cellParent = cellParent;
        this.documentParent = documentParent;
    }

    /**
     * After creating new paragraphs, the origin template paragraph should be removed. This method is responsible for
     * deleting template paragraph
     * @return boolean which indicates whether paragraph has been removed
     */
    public boolean deleteMe() {
        if (cellParent != null) {

            List<XWPFParagraph> p = cellParent.getParagraphs();
            for (int i = 0; i < p.size(); i++) {
                if (p.get(i).equals(paragraph)) {
                    cellParent.removeParagraph(i);
                    return true;
                }
            }
        } else if (documentParent != null) {
            int posOfParagraph = documentParent.getPosOfParagraph(paragraph);
            documentParent.removeBodyElement(posOfParagraph);
            return true;
        }
        return false;
    }

    public XWPFTableCell getCellParent() {
        return cellParent;
    }

    public XWPFDocument getDocumentParent() {
        return documentParent;
    }

    public XWPFParagraph getParagraph() {
        return paragraph;
    }

}
