package pl.jsolve.templ4docx.strategy;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import pl.jsolve.templ4docx.insert.Insert;
import pl.jsolve.templ4docx.insert.TextInsert;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variable;

public class TextInsertStrategy implements InsertStrategy {

    @Override
    public void insert(Insert insert, Variable variable) {
        if (!(insert instanceof TextInsert)) {
            return;
        }
        if (!(variable instanceof TextVariable)) {
            return;
        }

        TextInsert textInsert = (TextInsert) insert;
        TextVariable textVariable = (TextVariable) variable;
        for (XWPFRun run : textInsert.getParagraph().getRuns()) {
            String text = run.getText(0);
            if (StringUtils.contains(text, textInsert.getKey().getKey())) {
                text = StringUtils.replace(text, textVariable.getKey(), textVariable.getValue());
                setText(run, text);
            }
        }
    }

    private static final void setText(final XWPFRun run, final String text) {
        if (text.contains("\n")) {
            String[] lines = text.split("\n");
            run.setText(lines[0], 0);
            for(int i=1;i<lines.length;i++){
                run.addBreak();
                run.setText(lines[i]);
            }
        } else {
            run.setText(text, 0);
        }
    }
}
