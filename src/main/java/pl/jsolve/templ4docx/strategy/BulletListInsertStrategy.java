package pl.jsolve.templ4docx.strategy;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

import pl.jsolve.templ4docx.cleaner.ParagraphCleaner;
import pl.jsolve.templ4docx.insert.BulletListInsert;
import pl.jsolve.templ4docx.insert.ImageInsert;
import pl.jsolve.templ4docx.insert.Insert;
import pl.jsolve.templ4docx.insert.ParagraphInsert;
import pl.jsolve.templ4docx.insert.TextInsert;
import pl.jsolve.templ4docx.util.Key;
import pl.jsolve.templ4docx.util.VariableType;
import pl.jsolve.templ4docx.variable.BulletListVariable;
import pl.jsolve.templ4docx.variable.ImageVariable;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variable;

public class BulletListInsertStrategy implements InsertStrategy {

    private InsertStrategyChooser insertStrategyChooser;
    private ParagraphCleaner paragraphCleaner;

    public BulletListInsertStrategy(InsertStrategyChooser insertStrategyChooser, ParagraphCleaner paragraphCleaner) {
        this.insertStrategyChooser = insertStrategyChooser;
        this.paragraphCleaner = paragraphCleaner;
    }

    @Override
    public void insert(Insert insert, Variable variable) {
        if (!(insert instanceof BulletListInsert)) {
            return;
        }
        if (!(variable instanceof BulletListVariable)) {
            return;
        }

        BulletListInsert bulletListInsert = (BulletListInsert) insert;
        BulletListVariable bulletListVariable = (BulletListVariable) variable;
        XWPFParagraph templateParagraph = bulletListInsert.getParagraph();

        XWPFParagraph copiedParagraph = null;
        for (int i = 0; i < bulletListVariable.getVariables().size(); i++) {
            XmlCursor templateCursor = templateParagraph.getCTP().newCursor();
            copiedParagraph = templateParagraph.getBody().insertNewParagraph(templateCursor);
            cloneParagraph(copiedParagraph, templateParagraph);

            Variable variableToInsert = bulletListVariable.getVariables().get(i);
            Insert copiedInsert = prepareInsert(bulletListInsert, copiedParagraph, bulletListInsert.getKey(),
                    variableToInsert);
            insertStrategyChooser.replace(copiedInsert, variableToInsert);
        }

        bulletListInsert.deleteMe();
    }

    private void cloneParagraph(XWPFParagraph clone, XWPFParagraph source) {
        CTPPr pPr = clone.getCTP().addNewPPr();
        pPr.set(source.getCTP().getPPr());
        for (XWPFRun r : source.getRuns()) {
            XWPFRun newRun = clone.createRun();
            cloneRun(newRun, r);
        }
    }

    private void cloneRun(XWPFRun clone, XWPFRun source) {
        CTRPr rPr = clone.getCTR().addNewRPr();
        rPr.set(source.getCTR().getRPr());
        clone.setText(source.getText(0));
    }

    private Insert prepareInsert(BulletListInsert originalInsert, XWPFParagraph paragraph, Key key,
            Variable variableToInsert) {
        if (variableToInsert instanceof TextVariable) {
            return new TextInsert(new Key(key.getKey(), VariableType.TEXT), paragraph);
        }
        if (variableToInsert instanceof ImageVariable) {
            return new ImageInsert(new Key(key.getKey(), VariableType.IMAGE), paragraph);
        }
        if (variableToInsert instanceof BulletListVariable) {
            return new BulletListInsert(new Key(key.getKey(), VariableType.BULLET_LIST), paragraph,
                    originalInsert.getCellParent(), originalInsert.getDocumentParent());
        }

        return null;
    }

    public void cleanParagraphs() {
        for (ParagraphInsert paragraph : paragraphCleaner.getParagraphs()) {
            try {
                paragraph.deleteMe();
            } catch (Exception ex) {
                // do nothing, row doesn't exist
            }
        }
    }

}
