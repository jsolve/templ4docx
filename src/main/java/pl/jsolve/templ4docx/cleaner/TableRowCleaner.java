package pl.jsolve.templ4docx.cleaner;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import pl.jsolve.sweetener.collection.Collections;

/**
 * Container class which contains information about rows intended to remove.
 * @author Lukasz Stypka
 */
public class TableRowCleaner {

    private List<XWPFTableRow> rows;

    public TableRowCleaner() {
        this.rows = Collections.newArrayList();
    }

    /**
     * Add table row which should be removed from the table
     * @param row XWPFTableRow
     */
    public void add(XWPFTableRow row) {
        this.rows.add(row);
    }

    /**
     * Return list of rows which should be removed
     * @return list of rows
     */
    public List<XWPFTableRow> getRows() {
        return rows;
    }

}
