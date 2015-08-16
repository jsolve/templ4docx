package pl.jsolve.templ4docx.strategy;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

import pl.jsolve.templ4docx.cleaner.TableRowCleaner;
import pl.jsolve.templ4docx.insert.BulletListInsert;
import pl.jsolve.templ4docx.insert.ImageInsert;
import pl.jsolve.templ4docx.insert.Insert;
import pl.jsolve.templ4docx.insert.TableCellInsert;
import pl.jsolve.templ4docx.insert.TableRowInsert;
import pl.jsolve.templ4docx.insert.TextInsert;
import pl.jsolve.templ4docx.util.Key;
import pl.jsolve.templ4docx.variable.TableVariable;
import pl.jsolve.templ4docx.variable.Variable;
import pl.jsolve.templ4docx.variable.Variables;

public class TableInsertStrategy implements InsertStrategy {

    private Variables variables;
    private InsertStrategyChooser insertStrategyChooser;
    private TableRowCleaner tableRowCleaner;

    public TableInsertStrategy(Variables variables, InsertStrategyChooser insertStrategyChooser,
            TableRowCleaner tableRowCleaner) {
        this.variables = variables;
        this.insertStrategyChooser = insertStrategyChooser;
        this.tableRowCleaner = tableRowCleaner;
    }

    @Override
    public void insert(Insert insert, Variable variable) {
        if (!(insert instanceof TableRowInsert)) {
            return;
        }
        if (!(variable instanceof TableVariable)) {
            return;
        }

        TableRowInsert tableRowInsert = (TableRowInsert) insert;
        TableVariable tableVariable = (TableVariable) variable;

        int numberOfRows = tableVariable.getNumberOfRows();

        XWPFTable table = tableRowInsert.getRow().getTable();
        int templateRowPosition = findRowPosition(table.getRows(), tableRowInsert.getRow());
        List<TableCellInsert> templateCellInserts = tableRowInsert.getCellInserts();
        for (int i = numberOfRows-1; i >= 0 ; i--) {
            XWPFTableRow clonedRow = cloneRow(tableRowInsert.getRow());
            for (TableCellInsert cellInsert : templateCellInserts) {

                Insert clonedCellInsert = cloneCellToCopiedRow(tableRowInsert.getRow(), clonedRow, cellInsert);
                Variable subVariable = tableVariable.getVariable(clonedCellInsert.getKey(), i);
                if (subVariable != null) {
                    insertStrategyChooser.replace(clonedCellInsert, subVariable);
                }
            }
            table.addRow(clonedRow, templateRowPosition + 1);
        }
        tableRowCleaner.add(tableRowInsert.getRow());
    }

    private XWPFTableRow cloneRow(XWPFTableRow templateRow) {
        CTRow ctRow = CTRow.Factory.newInstance();
        ctRow.set(templateRow.getCtRow());
        return new XWPFTableRow(ctRow, templateRow.getTable());
    }

    public void cleanRows() {
        for (XWPFTableRow row : tableRowCleaner.getRows()) {
            try {
                XWPFTable table = row.getTable();
                int rowPosition = findRowPosition(table.getRows(), row);
                table.removeRow(rowPosition);
            } catch (Exception ex) {
                // do nothing, row doesn't exist
            }
        }
    }

    private int findRowPosition(List<XWPFTableRow> rowsOfTable, XWPFTableRow row) {
        for (int i = 0; i < rowsOfTable.size(); i++) {
            if (rowsOfTable.get(i) == row) {
                return i;
            }
        }
        return -1;
    }

    private Insert cloneCellToCopiedRow(XWPFTableRow originalRow, XWPFTableRow copiedRow,
            TableCellInsert tableCellInsert) {
        List<XWPFTableCell> originalCells = originalRow.getTableCells();
        for (int i = 0; i < originalCells.size(); i++) {
            if (originalCells.get(i) == tableCellInsert.getCell()) {
                if (tableCellInsert.getKey().containsSubKey()) {
                    return prepareInsert(copiedRow.getCell(i), tableCellInsert.getKey().getFirstSubKey());
                }
                return prepareInsert(copiedRow.getCell(i), tableCellInsert.getKey());
            }
        }
        return null;
    }

    private Insert prepareInsert(XWPFTableCell cell, Key key) {
        switch (key.getVariableType()) {
        case TEXT:
            return new TextInsert(key, findParagraph(cell, key.getKey()));
        case IMAGE:
            return new ImageInsert(key, findParagraph(cell, key.getKey()));
        case BULLET_LIST:
            return new BulletListInsert(key, findParagraph(cell, key.getKey()), cell, null);
        }
        return null;
    }

    private XWPFParagraph findParagraph(XWPFTableCell cell, String key) {
        for (XWPFParagraph paragraph : cell.getParagraphs()) {
            if (StringUtils.contains(paragraph.getText(), key)) {
                return paragraph;
            }
        }
        return null;
    }

}
