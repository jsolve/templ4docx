package pl.jsolve.templ4docx.condition;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.Test;

import pl.jsolve.templ4docx.executor.ParagraphRemover;
import pl.jsolve.templ4docx.insert.ConditionInsert;

public class ParagraphRemoverTest {

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
        remover.removeConditionTagsFromParagraph(conditionInsert, document, createdParagraph, true);

        // then
        String paragraphText = createdParagraph.getText();
        assertThat(paragraphText).isEqualTo(
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
        remover.removeConditionTagsFromParagraph(conditionInsert, document, createdParagraph, false);

        // then
        String paragraphText = createdParagraph.getText();
        assertThat(paragraphText).isEqualTo(
                "Text at the start < of paragraph Text at  the end of paragraph");
    }

    private void createdRuns(XWPFParagraph paragraph, String... runs) {
        for (String run : runs) {
            XWPFRun createdRun = paragraph.createRun();
            createdRun.setText(run);
        }
    }

}
