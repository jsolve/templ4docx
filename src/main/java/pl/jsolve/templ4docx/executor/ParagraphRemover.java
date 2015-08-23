package pl.jsolve.templ4docx.executor;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import pl.jsolve.sweetener.collection.Collections;
import pl.jsolve.sweetener.text.Strings;
import pl.jsolve.templ4docx.insert.ConditionInsert;

public class ParagraphRemover {

    /**
     * Remove condition tags and content between them (if condition is not satisfied)
     * @param conditionInsert
     * @param document
     * @param conditionFulfilled
     * @param bodyElements
     */
    public void removeConditionTagsFromParagraphs(ConditionInsert conditionInsert, XWPFDocument document,
            boolean conditionFulfilled, IBodyElement... bodyElements) {

        int numberOfParagraphs = bodyElements.length;
        if (numberOfParagraphs == 1) {
            removeConditionTagsFromParagraph(conditionInsert, document, conditionFulfilled, bodyElements[0]);
            return;
        }

        if ((bodyElements[0].getElementType() != BodyElementType.PARAGRAPH)
                || (bodyElements[bodyElements.length - 1].getElementType() != BodyElementType.PARAGRAPH)) {
            return;
        }
        

        // Remove start condition tag
        String condition = conditionInsert.getCondition();
        String paragraphText = ((XWPFParagraph)bodyElements[0]).getText();

        int startTagIndex = paragraphText.indexOf(condition);
        int endTagIndex = startTagIndex + condition.length();

        if (!conditionFulfilled) {
            endTagIndex = paragraphText.length();
        }
        
        int _startTagIndex = startTagIndex;
        int _endTagIndex = endTagIndex;


        // remove end condition tag
        XWPFParagraph lastParagraph = (XWPFParagraph) bodyElements[bodyElements.length - 1];
        String lastParagraphText = lastParagraph.getText();
        int startCloseTagIndex = lastParagraphText.indexOf("</docx:if>");
        int endCloseTagIndex = startCloseTagIndex + "</docx:if>".length();
        if (!conditionFulfilled) {
            startCloseTagIndex = 0;
        }
        removeFromParagraph(lastParagraph, startCloseTagIndex, endCloseTagIndex);

        removeFromParagraph((XWPFParagraph)bodyElements[0], _startTagIndex, _endTagIndex);

        if (numberOfParagraphs > 2) {

            Integer startIndex = conditionInsert.getStartIndex();
            Integer endIndex = conditionInsert.getEndIndex();
            for (int i = endIndex - 1; i > startIndex; i--) { // don't remove first and last paragraph!
                document.removeBodyElement(i);
            }
        }
    }

    /**
     * This method should be invoked when start tag and closed tag is located in the same paragraph
     * @param conditionInsert
     * @param document
     * @param conditionFulfilled
     * @param bodyElement
     */
    private void removeConditionTagsFromParagraph(ConditionInsert conditionInsert, XWPFDocument document,
            boolean conditionFulfilled, IBodyElement bodyElement) {

        if (bodyElement.getElementType() != BodyElementType.PARAGRAPH) {
            return;
        }
        XWPFParagraph paragraph = (XWPFParagraph) bodyElement;

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

        int _startTagIndex = startTagIndex;
        int _endTagIndex = endTagIndex;

        if (conditionFulfilled) {
            // remove end tag ("</docx:if>")
            paragraphText = paragraph.getText();
            List<Integer> indexesOf = Strings.indexesOf(paragraphText, "</docx:if>");
            for (Integer index : indexesOf) {
                if (index > endTagIndex) {
                    startTagIndex = index;
                    endTagIndex = index + "</docx:if>".length();
                    removeFromParagraph(paragraph, startTagIndex, endTagIndex);
                    break;
                }
            }
        }
        
        
        removeFromParagraph(paragraph, _startTagIndex, _endTagIndex);
    }

    private void removeFromParagraph(XWPFParagraph paragraph, int from, int to) {
        int totalLength = 0;
        List<Integer> runsToRemove = Collections.newArrayList();
        for (int i = 0; i < paragraph.getRuns().size(); i++) {
            XWPFRun run = paragraph.getRuns().get(i);
            String runText = run.getText(0);
            if(runText == null) {
                runText = "";
            }
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
                if(StringUtils.isEmpty(substring)) {
                    System.out.println(run.getPictureText());
                    runsToRemove.add(i);
                }
                run.setText(substring, 0);
                totalLength += runTextLength;
            }
        }
        removeEmptyRuns(runsToRemove, paragraph);
    }
    
    private void removeEmptyRuns(List<Integer> indexes, XWPFParagraph paragraph) {
        for(int i = indexes.size()-1; i >=0; i--) {
            paragraph.removeRun(indexes.get(i));
        }
    }
}
