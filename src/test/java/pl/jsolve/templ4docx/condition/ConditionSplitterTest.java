package pl.jsolve.templ4docx.condition;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import pl.jsolve.templ4docx.util.Condition;
import pl.jsolve.templ4docx.util.OperationType;

@RunWith(value = Parameterized.class)
public class ConditionSplitterTest {

    private ConditionSplitter conditionSplitter;
    private String text;
    private String expectedVariable;
    private OperationType expectedOperationType;
    private String expectedValue;

    public ConditionSplitterTest(String text, String expectedVariable, OperationType expectedOperationType,
            String expectedValue) {
        this.text = text;
        this.expectedVariable = expectedVariable;
        this.expectedOperationType = expectedOperationType;
        this.expectedValue = expectedValue;
        conditionSplitter = new ConditionSplitter();
    }

    @Parameters(name = "{index}: given {0} (variable {1}, Operation type {2}, value {3})")
    public static Iterable<Object[]> data1() {
        return Arrays.asList(new Object[][] {
                {"var1 eq “test”", "var1", OperationType.EQ, "test"},
                {"${firstname} eq “Lukasz”", "${firstname}", OperationType.EQ, "Lukasz"},
                {"${firstname} ne “Lukasz“", "${firstname}", OperationType.NE, "Lukasz"},
                {"${firstname} ne 'Lukasz'", "${firstname}", OperationType.NE, "Lukasz"},
                {"${firstname} ne Lukasz", "${firstname}", OperationType.NE, "Lukasz"},
                {"${firstname} ne A very long condition ... ", "${firstname}", OperationType.NE,
                        "A very long condition ..."},
                {"${firstname} ne \"A very long condition ... \"", "${firstname}", OperationType.NE,
                        "A very long condition ... "},
                {"${firstname} is null", "${firstname}", OperationType.NULL, null},
                {"${firstname} IS NULL", "${firstname}", OperationType.NULL, null},
                {"${firstname} IS NOT NULL", "${firstname}", OperationType.NOT_NULL, null},
                {"${firstname} IS not null", "${firstname}", OperationType.NOT_NULL, null},
                {"${age} lt 5", "${age}", OperationType.LT, "5"}, {"${age} lt 5.5", "${age}", OperationType.LT, "5.5"},
                {"${age} gt 5", "${age}", OperationType.GT, "5"}, {"${age} gt 5.5", "${age}", OperationType.GT, "5.5"},
                {"${age} le 5", "${age}", OperationType.LE, "5"}, {"${age} le 5.5", "${age}", OperationType.LE, "5.5"},
                {"${age} ge 5", "${age}", OperationType.GE, "5"}, {"${age} ge 5.5", "${age}", OperationType.GE, "5.5"},

        });
    }

    @Test
    public void shouldSplitCondition() throws Exception {
        // given

        // when
        Condition condition = conditionSplitter.splitCondition(text);

        // then
        assertThat(condition.getVariable()).isEqualTo(expectedVariable);
        assertThat(condition.getOperation()).isEqualTo(expectedOperationType);
        assertThat(condition.getValue()).isEqualTo(expectedValue);

    }

}
