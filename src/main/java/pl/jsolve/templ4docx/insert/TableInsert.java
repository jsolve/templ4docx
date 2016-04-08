package pl.jsolve.templ4docx.insert;

import org.apache.poi.xwpf.usermodel.XWPFTable;
import pl.jsolve.sweetener.collection.Collections;
import pl.jsolve.templ4docx.util.Key;
import pl.jsolve.templ4docx.util.VariableType;

import java.util.List;

/**
 * Insert for table variable. This insert contains list of table row inserts
 * @author serv
 */
public class TableInsert extends Insert {

    private List<TableRowInsert> rowInserts;
    /**
     * XWPFTable which contains found row variables
     */
    private XWPFTable table;

    public TableInsert() {
        super(new Key("", VariableType.TABLE));
        this.rowInserts = Collections.newArrayList();
    }

    public void add(TableRowInsert rowInsert) {
        this.rowInserts.add(rowInsert);
        this.key.addSubKey(rowInsert.getKey());
        if (table == null) {
            table = rowInsert.getRow().getTable();
        }
    }

    public List<TableRowInsert> getRowInserts() {
        return rowInserts;
    }

    public XWPFTable getTable() {
        return table;
    }

    @Override
    public String toString() {
        return "TableInsert [rowInserts=" + rowInserts + ", table=" + table + "]";
    }

}