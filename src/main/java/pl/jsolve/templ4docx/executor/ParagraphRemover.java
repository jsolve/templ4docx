package pl.jsolve.templ4docx.executor;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import pl.jsolve.sweetener.text.Strings;
import pl.jsolve.templ4docx.insert.ConditionInsert;

public class ParagraphRemover {

    public void removeConditionTagsFromParagraph(ConditionInsert conditionInsert, XWPFDocument document,
            XWPFParagraph paragraph, boolean conditionFulfilled) {

        String condition = conditionInsert.getCondition();
        String paragraphText = paragraph.getText();

        int startTagIndex = paragraphText.indexOf(condition);
        int endTagIndex = startTagIndex + condition.length();

        if (!conditionFulfilled) {
            List<Integer> indexesOf = Strings.indexesOf(paragraphText, "</docx:if>");
            for (Integer index : indexesOf) {
                if (index > endTagIndex) {
                    endTagIndex = index + "</docx:if>".length();
                    break;
                }
            }
        }
        
        removeFromParagraph(paragraph, startTagIndex, endTagIndex);

        if(conditionFulfilled) {
            // remove end tag ("</docx:if>")
            paragraphText = paragraph.getText();
            List<Integer> indexesOf = Strings.indexesOf(paragraphText, "</docx:if>");
            for (Integer index : indexesOf) {
                if (index > endTagIndex) {
                    startTagIndex= index;
                    endTagIndex = index + "</docx:if>".length();
                    removeFromParagraph(paragraph, startTagIndex, endTagIndex);
                    break;
                }
            }
        }
}

    private void removeFromParagraph( XWPFParagraph paragraph, int from, int to) {
        int totalLength = 0;
        for(int i = 0; i < paragraph.getRuns().size(); i++) {
            XWPFRun run = paragraph.getRuns().get(i);
            String runText = run.getText(0);
            int runTextLength = runText.length();
            if (from > totalLength + runTextLength || to < totalLength) {
                totalLength += runTextLength;
                continue;
            } else {
                int fromIndex = 0;
                int toIndex = runTextLength;

                if (from > totalLength) {
                    fromIndex = from - totalLength;
                }

                if (to < totalLength + runTextLength) {
                    toIndex = to - totalLength;
                    if (toIndex > runTextLength) {
                        toIndex = runTextLength;
                    }
                }

                String substring = runText.substring(0, fromIndex) + runText.substring(toIndex);
                run.setText(substring, 0);
                totalLength += runTextLength;
            }
        }
    }
}
