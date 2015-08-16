package pl.jsolve.templ4docx.util;

import pl.jsolve.templ4docx.variable.TableVariable;

public class RowInsert {

    private int rowIndex;
    private TableVariable tableVariable;

    public RowInsert(int rowIndex, TableVariable tableVariable) {
        this.rowIndex = rowIndex;
        this.tableVariable = tableVariable;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public TableVariable getTableVariable() {
        return tableVariable;
    }

}
