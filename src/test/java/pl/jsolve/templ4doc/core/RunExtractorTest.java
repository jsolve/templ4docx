package pl.jsolve.templ4doc.core;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.Before;
import org.junit.Test;

import pl.jsolve.sweetener.collection.Collections;
import pl.jsolve.templ4docx.core.RunExtractor;
import pl.jsolve.templ4docx.core.VariablePattern;

public class RunExtractorTest {

    private RunExtractor extractor;
    private XWPFDocument document;
    private XWPFParagraph paragraph;

    @Before
    public void setUp() {
        document = new XWPFDocument();
        paragraph = document.createParagraph();
        extractor = new RunExtractor();
    }

    @Test
    public void shouldReplaceVariable1() throws Exception {
        // given
        List<XWPFRun> runs = prepareRuns("${name}");
        String[] keysArray = {"${age}", "${name}"};
        String[] valuesArray = {"15", "Lukasz"};
        VariablePattern variablePattern = new VariablePattern("${", "}");

        // when
        extractor.execute(runs, keysArray, valuesArray, variablePattern);

        // then
        assertThat(runs).hasSize(1);
        assertThat(runs.get(0).getText(0)).isEqualTo("Lukasz");
    }

    @Test
    public void shouldReplaceVariable2() throws Exception {
        // given
        List<XWPFRun> runs = prepareRuns("${", "name}");
        String[] keysArray = {"${age}", "${name}"};
        String[] valuesArray = {"15", "Lukasz"};
        VariablePattern variablePattern = new VariablePattern("${", "}");

        // when
        extractor.execute(runs, keysArray, valuesArray, variablePattern);

        // then
        assertThat(runs).hasSize(2);
        assertThat(runs.get(0).getText(0)).isEqualTo("Lukasz");
        assertThat(runs.get(1).getText(0)).isEqualTo("");
    }

    @Test
    public void shouldReplaceVariable3() throws Exception {
        // given
        List<XWPFRun> runs = prepareRuns("${", "name", "}");
        String[] keysArray = {"${age}", "${name}"};
        String[] valuesArray = {"15", "Lukasz"};
        VariablePattern variablePattern = new VariablePattern("${", "}");

        // when
        extractor.execute(runs, keysArray, valuesArray, variablePattern);

        // then
        assertThat(runs).hasSize(3);
        assertThat(runs.get(0).getText(0)).isEqualTo("Lukasz");
        assertThat(runs.get(1).getText(0)).isEqualTo("");
        assertThat(runs.get(2).getText(0)).isEqualTo("");
    }

    @Test
    public void shouldReplaceVariable4() throws Exception {
        // given
        List<XWPFRun> runs = prepareRuns("${", "name", "}${", "age}");
        String[] keysArray = {"${age}", "${name}"};
        String[] valuesArray = {"15", "Lukasz"};
        VariablePattern variablePattern = new VariablePattern("${", "}");

        // when
        extractor.execute(runs, keysArray, valuesArray, variablePattern);

        // then
        assertThat(runs).hasSize(4);
        assertThat(runs.get(0).getText(0)).isEqualTo("Lukasz");
        assertThat(runs.get(1).getText(0)).isEqualTo("");
        assertThat(runs.get(2).getText(0)).isEqualTo("15");
        assertThat(runs.get(3).getText(0)).isEqualTo("");
    }

    @Test
    public void shouldReplaceVariable5() throws Exception {
        // given
        List<XWPFRun> runs = prepareRuns("${", "name", "}${", "age", "}");
        String[] keysArray = {"${age}", "${name}"};
        String[] valuesArray = {"15", "Lukasz"};
        VariablePattern variablePattern = new VariablePattern("${", "}");

        // when
        extractor.execute(runs, keysArray, valuesArray, variablePattern);

        // then
        assertThat(runs).hasSize(5);
        assertThat(runs.get(0).getText(0)).isEqualTo("Lukasz");
        assertThat(runs.get(1).getText(0)).isEqualTo("");
        assertThat(runs.get(2).getText(0)).isEqualTo("15");
        assertThat(runs.get(3).getText(0)).isEqualTo("");
        assertThat(runs.get(4).getText(0)).isEqualTo("");
    }

    @Test
    public void shouldReplaceVariable6() throws Exception {
        // given
        List<XWPFRun> runs = prepareRuns("$", "{", "name", "}${", "age", "}");
        String[] keysArray = {"${age}", "${name}"};
        String[] valuesArray = {"15", "Lukasz"};
        VariablePattern variablePattern = new VariablePattern("${", "}");

        // when
        extractor.execute(runs, keysArray, valuesArray, variablePattern);

        // then
        assertThat(runs).hasSize(6);
        assertThat(runs.get(0).getText(0)).isEqualTo("Lukasz");
        assertThat(runs.get(1).getText(0)).isEqualTo("");
        assertThat(runs.get(2).getText(0)).isEqualTo("");
        assertThat(runs.get(3).getText(0)).isEqualTo("15");
        assertThat(runs.get(4).getText(0)).isEqualTo("");
        assertThat(runs.get(5).getText(0)).isEqualTo("");
    }
    
    @Test
    public void shouldReplaceVariable7() throws Exception {
        // given
        List<XWPFRun> runs = prepareRuns("$", "{", "name", "}$${", "age", "}");
        String[] keysArray = {"${age}", "${name}"};
        String[] valuesArray = {"15", "Lukasz"};
        VariablePattern variablePattern = new VariablePattern("${", "}");

        // when
        extractor.execute(runs, keysArray, valuesArray, variablePattern);

        // then
        assertThat(runs).hasSize(6);
        assertThat(runs.get(0).getText(0)).isEqualTo("Lukasz");
        assertThat(runs.get(1).getText(0)).isEqualTo("");
        assertThat(runs.get(2).getText(0)).isEqualTo("");
        assertThat(runs.get(3).getText(0)).isEqualTo("$15");
        assertThat(runs.get(4).getText(0)).isEqualTo("");
        assertThat(runs.get(5).getText(0)).isEqualTo("");
    }
    
    @Test
    public void shouldReplaceVariable8() throws Exception {
        // given
        List<XWPFRun> runs = prepareRuns("$", "{", "name", "}${${", "age", "}");
        String[] keysArray = {"${age}", "${name}"};
        String[] valuesArray = {"15", "Lukasz"};
        VariablePattern variablePattern = new VariablePattern("${", "}");

        // when
        extractor.execute(runs, keysArray, valuesArray, variablePattern);

        // then
        assertThat(runs).hasSize(6);
        assertThat(runs.get(0).getText(0)).isEqualTo("Lukasz");
        assertThat(runs.get(1).getText(0)).isEqualTo("");
        assertThat(runs.get(2).getText(0)).isEqualTo("");
        assertThat(runs.get(3).getText(0)).isEqualTo("${15");
        assertThat(runs.get(4).getText(0)).isEqualTo("");
        assertThat(runs.get(5).getText(0)).isEqualTo("");
    }

    private List<XWPFRun> prepareRuns(String... values) {
        List<XWPFRun> runs = Collections.newArrayList();
        for (String value : values) {
            XWPFRun run = paragraph.createRun();
            run.setText(value);
            runs.add(run);
        }
        return runs;
    }
}
