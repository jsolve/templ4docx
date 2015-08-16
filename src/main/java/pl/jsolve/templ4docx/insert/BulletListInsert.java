package pl.jsolve.templ4docx.insert;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import pl.jsolve.templ4docx.util.Key;

/**
 * Insert for bullet list. Numbered list is presented as BulletListInsert as well
 * @author Lukasz Stypka
 */
public class BulletListInsert extends ParagraphInsert {

    public BulletListInsert(Key key, XWPFParagraph paragraph, XWPFTableCell cellParent, XWPFDocument documentParent) {
        super(key, paragraph, cellParent, documentParent);
    }

}