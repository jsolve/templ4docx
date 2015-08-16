package pl.jsolve.templ4docx.insert;

import org.apache.poi.xwpf.usermodel.XWPFTableCell;

import pl.jsolve.templ4docx.util.Key;

/**
 * Insert for table cell variable.
 * @author Lukasz Stypka
 */
public class TableCellInsert extends Insert {

    /**
     * Table cell which contains found variable
     */
    private XWPFTableCell cell;

    public TableCellInsert(Key key, XWPFTableCell cell) {
        super(key);
        this.cell = cell;
    }

    public void setCell(XWPFTableCell cell) {
        this.cell = cell;
    }

    public XWPFTableCell getCell() {
        return cell;
    }

}