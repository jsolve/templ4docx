package pl.jsolve.templ4docx.strategy;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import pl.jsolve.templ4docx.insert.Insert;
import pl.jsolve.templ4docx.insert.ObjectInsert;
import pl.jsolve.templ4docx.variable.ObjectVariable;
import pl.jsolve.templ4docx.variable.Variable;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class ObjectInsertStrategy implements InsertStrategy {

    @Override
    public void insert(Insert insert, Variable variable) {
        if (!(insert instanceof ObjectInsert)) {
            return;
        }
        if (!(variable instanceof ObjectVariable)) {
            return;
        }

        ObjectInsert objectInsert = (ObjectInsert) insert;
        ObjectVariable objectVariable = (ObjectVariable) variable;
        for (XWPFRun run : objectInsert.getParagraph().getRuns()) {
            String text = run.getText(0);
            if (StringUtils.contains(text, objectInsert.getKey().getKey())) {
                text = StringUtils.replace(text, objectVariable.getKey(), objectVariable.getStringValue());
                run.setText(text, 0);
            }
        }
    }
}
