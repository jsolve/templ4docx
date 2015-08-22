package pl.jsolve.templ4docx.executor;

import java.util.List;

import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import pl.jsolve.sweetener.text.Strings;
import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.insert.ConditionInsert;
import pl.jsolve.templ4docx.variable.Variables;

public class ConditionExecutor {

    private final static String CONDITION_PREFIX = "<docx:if";
    private final static String CONDITION_SUFFIX = "</docx:if>";

    private Variables variables;

    public ConditionExecutor(Variables variables) {
        this.variables = variables;
    }

    public void execute(Docx docx) {
        XWPFDocument xwpfDocument = docx.getXWPFDocument();

        while (true) {
            ConditionInsert conditionInsert = findConditionInsert(xwpfDocument.getBodyElements());
            if (!conditionInsert.isFound()) {
                break;
            }
            removeParagraphs(xwpfDocument, conditionInsert);
        }
    }

    private ConditionInsert findConditionInsert(List<IBodyElement> bodyElements) {

        ConditionInsert conditionInsert = new ConditionInsert();

        int deepIndex = 0;
        boolean found = false;
        for (int i = 0; i < bodyElements.size(); i++) {

            if (bodyElements.get(i).getElementType() == BodyElementType.PARAGRAPH) {
                XWPFParagraph paragraph = (XWPFParagraph) bodyElements.get(i);
                String paragraphText = paragraph.getText();
                if (paragraphText.contains(CONDITION_PREFIX)) {
                    if (!found) {
                        found = true;
                        conditionInsert.setStartIndex(i);
                        conditionInsert.setStartParagraph(paragraph);
                        int startIndex = paragraphText.indexOf(CONDITION_PREFIX);
                        List<Integer> indexesOf = Strings.indexesOf(paragraphText, ">");
                        int closeTagIndex = -1;
                        for (Integer index : indexesOf) {
                            if(index < startIndex) {
                                continue;
                            }
                            if(index > 0 && paragraphText.charAt(index - 1) != '/') {
                                closeTagIndex = index;
                            }
                        }
                        conditionInsert.setCondition(paragraphText.substring(startIndex, closeTagIndex + 1));
                    } else {
                        deepIndex++;
                    }
                    if (paragraphText.contains(CONDITION_SUFFIX)) {
                        deepIndex--;
                        conditionInsert.setEndIndex(i);
                        conditionInsert.setEndParagraph(paragraph);
                    }
                }
                if (paragraphText.contains(CONDITION_SUFFIX)) {
                    if (deepIndex == 0) {
                        conditionInsert.setEndIndex(i);
                        conditionInsert.setEndParagraph(paragraph);
                        conditionInsert.setFound(true);
                    } else {
                        deepIndex--;
                    }
                }
            }
        }
        return conditionInsert;
    }

    private void removeParagraphs(XWPFDocument xwpfDocument, ConditionInsert conditionInsert) {
        Integer startIndex = conditionInsert.getStartIndex();
        Integer endIndex = conditionInsert.getEndIndex();
        for (int i = endIndex; i >= startIndex; i--) {
            xwpfDocument.removeBodyElement(i);
        }

    }

    private void removeConditionTagsFromParagraph(XWPFParagraph paragraph) {

    }

}
