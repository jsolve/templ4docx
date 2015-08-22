package pl.jsolve.templ4docx.condition;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.Test;

import pl.jsolve.templ4docx.executor.ParagraphRemover;
import pl.jsolve.templ4docx.insert.ConditionInsert;

public class ParagraphRemoverTest {

    private final static boolean CONDITION_IS_FULFILLED = true;
    private final static boolean CONDITION_IS_NOT_FULFILLED = false;

    @Test
    public void shouldRemoveConditionTagFromOneParagraphWhenConditionIsFulfilled() throws Exception {
        // given document with one paragraph
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph createdParagraph = document.createParagraph();
        createdRuns(createdParagraph, "Text at the start < of paragraph<", "do", "cx", ":", "if", " ${var} eq ",
                "'Lukasz</doc' > This concent should be preserved </doc </d", "ocx:if> Text at ",
                " the end of paragraph");

        // when
        ParagraphRemover remover = new ParagraphRemover();
        ConditionInsert conditionInsert = new ConditionInsert();
        conditionInsert.setCondition("<docx:if ${var} eq 'Lukasz</doc' >");
        remover.removeConditionTagsFromParagraphs(conditionInsert, document, CONDITION_IS_FULFILLED, createdParagraph);

        // then
        String paragraphText = createdParagraph.getText();
        assertThat(paragraphText)
                .isEqualTo(
                        "Text at the start < of paragraph This concent should be preserved </doc  Text at  the end of paragraph");
    }

    @Test
    public void shouldRemoveConditionTagFromOneParagraphWhenConditionIsNotFulfilled() throws Exception {
        // given document with one paragraph
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph createdParagraph = document.createParagraph();
        createdRuns(createdParagraph, "Text at the start < of paragraph<", "do", "cx", ":", "if", " ${var} eq ",
                "'Lukasz</doc' > This concent should be preserved </doc </d", "ocx:if> Text at ",
                " the end of paragraph");

        // when
        ParagraphRemover remover = new ParagraphRemover();
        ConditionInsert conditionInsert = new ConditionInsert();
        conditionInsert.setCondition("<docx:if ${var} eq 'Lukasz</doc' >");
        remover.removeConditionTagsFromParagraphs(conditionInsert, document, CONDITION_IS_NOT_FULFILLED,
                createdParagraph);

        // then
        String paragraphText = createdParagraph.getText();
        assertThat(paragraphText).isEqualTo("Text at the start < of paragraph Text at  the end of paragraph");
    }

    @Test
    public void shouldRemoveConditionTagFromTwoParagraphWhenConditionIsFulfilled() throws Exception {
        // given document with one paragraph
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph firstParagraph = document.createParagraph();
        createdRuns(firstParagraph, "Text at the start < of paragraph<", "do", "cx", ":", "if", " ${var} eq ",
                "'Lukasz</doc' > This concent");

        XWPFParagraph secondParagraph = document.createParagraph();
        createdRuns(secondParagraph, "should be preserved </doc </d", "ocx:if> Text at ", " the end of paragraph");

        // when
        ParagraphRemover remover = new ParagraphRemover();
        ConditionInsert conditionInsert = new ConditionInsert();
        conditionInsert.setCondition("<docx:if ${var} eq 'Lukasz</doc' >");
        remover.removeConditionTagsFromParagraphs(conditionInsert, document, CONDITION_IS_FULFILLED, firstParagraph,
                secondParagraph);

        // then
        String firstParagraphText = firstParagraph.getText();
        assertThat(firstParagraphText).isEqualTo("Text at the start < of paragraph This concent");

        String secondParagraphText = secondParagraph.getText();
        assertThat(secondParagraphText).isEqualTo("should be preserved </doc  Text at  the end of paragraph");
    }

    @Test
    public void shouldRemoveConditionTagFromTwoParagraphWhenConditionIsNotFulfilled() throws Exception {
        // given document with one paragraph
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph firstParagraph = document.createParagraph();
        createdRuns(firstParagraph, "Text at the start < of paragraph<", "do", "cx", ":", "if", " ${var} eq ",
                "'Lukasz</doc' > This concent");

        XWPFParagraph secondParagraph = document.createParagraph();
        createdRuns(secondParagraph, "should be preserved </doc </d", "ocx:if> Text at ", " the end of paragraph");

        // when
        ParagraphRemover remover = new ParagraphRemover();
        ConditionInsert conditionInsert = new ConditionInsert();
        conditionInsert.setCondition("<docx:if ${var} eq 'Lukasz</doc' >");
        remover.removeConditionTagsFromParagraphs(conditionInsert, document, CONDITION_IS_NOT_FULFILLED,
                firstParagraph, secondParagraph);

        // then
        String firstParagraphText = firstParagraph.getText();
        assertThat(firstParagraphText).isEqualTo("Text at the start < of paragraph");

        String secondParagraphText = secondParagraph.getText();
        assertThat(secondParagraphText).isEqualTo(" Text at  the end of paragraph");
    }

    @Test
    public void shouldRemoveConditionTagFromOneRunWhenConditionIsFulfilled() throws Exception {
        // given document with one paragraph
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph firstParagraph = document.createParagraph();
        createdRuns(
                firstParagraph,
                "Text at the start < of paragraph<docx:if ${var} eq 'Lukasz</doc' > This concent should be preserved </doc </docx:if> Text at the end of paragraph");

        // when
        ParagraphRemover remover = new ParagraphRemover();
        ConditionInsert conditionInsert = new ConditionInsert();
        conditionInsert.setCondition("<docx:if ${var} eq 'Lukasz</doc' >");
        remover.removeConditionTagsFromParagraphs(conditionInsert, document, CONDITION_IS_FULFILLED, firstParagraph);

        // then
        String firstParagraphText = firstParagraph.getText();
        assertThat(firstParagraphText).isEqualTo("Text at the start < of paragraph This concent should be preserved </doc  Text at the end of paragraph");
    }
    
    @Test
    public void shouldRemoveConditionTagFromOneRunWhenConditionIsNotFulfilled() throws Exception {
        // given document with one paragraph
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph firstParagraph = document.createParagraph();
        createdRuns(
                firstParagraph,
                "Text at the start < of paragraph<docx:if ${var} eq 'Lukasz</doc' > This concent should be preserved </doc </docx:if> Text at the end of paragraph");

        // when
        ParagraphRemover remover = new ParagraphRemover();
        ConditionInsert conditionInsert = new ConditionInsert();
        conditionInsert.setCondition("<docx:if ${var} eq 'Lukasz</doc' >");
        remover.removeConditionTagsFromParagraphs(conditionInsert, document, CONDITION_IS_NOT_FULFILLED, firstParagraph);

        // then
        String firstParagraphText = firstParagraph.getText();
        assertThat(firstParagraphText).isEqualTo("Text at the start < of paragraph Text at the end of paragraph");
    }
    
    @Test
    public void shouldRemoveTwoConditionsTagFromOneRunWhenConditionIsFulfilled() throws Exception {
        // given document with one paragraph
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph firstParagraph = document.createParagraph();
        createdRuns(
                firstParagraph,
                "Text at the start < of paragraph<docx:if ${var} eq 'Lukasz</doc' > This concent should be preserved </doc </docx:if> Text at the <docx:if var le 5>end </docx:if>of paragraph");

        // when
        ParagraphRemover remover = new ParagraphRemover();
        ConditionInsert conditionInsert = new ConditionInsert();
        conditionInsert.setCondition("<docx:if ${var} eq 'Lukasz</doc' >");
        remover.removeConditionTagsFromParagraphs(conditionInsert, document, CONDITION_IS_FULFILLED, firstParagraph);
        
        ConditionInsert secondConditionInsert = new ConditionInsert();
        secondConditionInsert.setCondition("<docx:if var le 5>");
        remover.removeConditionTagsFromParagraphs(secondConditionInsert, document, CONDITION_IS_FULFILLED, firstParagraph);

        // then
        String firstParagraphText = firstParagraph.getText();
        assertThat(firstParagraphText).isEqualTo("Text at the start < of paragraph This concent should be preserved </doc  Text at the end of paragraph");
    }
    
    @Test
    public void shouldRemoveTwoConditionsTagFromOneRunWhenConditionIsNotFulfilled() throws Exception {
        // given document with one paragraph
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph firstParagraph = document.createParagraph();
        createdRuns(
                firstParagraph,
                "Text at the start < of paragraph<docx:if ${var} eq 'Lukasz</doc' > This concent should be preserved </doc </docx:if> Text at the <docx:if var le 5>end </docx:if>of paragraph");

        // when
        ParagraphRemover remover = new ParagraphRemover();
        ConditionInsert conditionInsert = new ConditionInsert();
        conditionInsert.setCondition("<docx:if ${var} eq 'Lukasz</doc' >");
        remover.removeConditionTagsFromParagraphs(conditionInsert, document, CONDITION_IS_NOT_FULFILLED, firstParagraph);
        
        ConditionInsert secondConditionInsert = new ConditionInsert();
        secondConditionInsert.setCondition("<docx:if var le 5>");
        remover.removeConditionTagsFromParagraphs(secondConditionInsert, document, CONDITION_IS_NOT_FULFILLED, firstParagraph);

        // then
        String firstParagraphText = firstParagraph.getText();
        assertThat(firstParagraphText).isEqualTo("Text at the start < of paragraph Text at the of paragraph");
    }

    private void createdRuns(XWPFParagraph paragraph, String... runs) {
        for (String run : runs) {
            XWPFRun createdRun = paragraph.createRun();
            createdRun.setText(run);
        }
    }

}
