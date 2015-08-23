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
public class ConditionSplitterParamTest {

    private ConditionSplitter conditionSplitter;
    private String text;
    private String expectedVariable;
    private OperationType expectedOperationType;
    private Object expectedValue;
    private Class<?> expectedClass;

    public ConditionSplitterParamTest(String text, String expectedVariable, OperationType expectedOperationType,
            Object expectedValue, Class<?> expectedClass) {
        this.text = text;
        this.expectedVariable = expectedVariable;
        this.expectedOperationType = expectedOperationType;
        this.expectedValue = expectedValue;
        this.expectedClass = expectedClass;
        conditionSplitter = new ConditionSplitter();
    }

    @Parameters(name = "{index}: given {0} (variable {1}, Operation type {2}, value {3})")
    public static Iterable<Object[]> data1() {
        return Arrays.asList(new Object[][] {
                {"var1 eq “test”", "var1", OperationType.EQ, "test", String.class},
                {"${firstname} eq “Lukasz”", "${firstname}", OperationType.EQ, "Lukasz", String.class},
                {"${age} eq 5", "${age}", OperationType.EQ, 5, Integer.class},
                {"${var1} ne 5.34", "${var1}", OperationType.NE, 5.34, Double.class},
                {"${firstname} ne “Lukasz“", "${firstname}", OperationType.NE, "Lukasz", String.class},
                {"${firstname} ne 'Lukasz'", "${firstname}", OperationType.NE, "Lukasz", String.class},
                {"${firstname} ne Lukasz", "${firstname}", OperationType.NE, "Lukasz", String.class},
                {"${firstname} ne A very long condition ... ", "${firstname}", OperationType.NE,
                        "A very long condition ...", String.class},
                {"${firstname} ne \"A very long condition ... \"", "${firstname}", OperationType.NE,
                        "A very long condition ... ", String.class},
                {"${firstname} is null", "${firstname}", OperationType.NULL, null, null},
                {"${firstname} IS NULL", "${firstname}", OperationType.NULL, null, null},
                {"${firstname} IS NOT NULL", "${firstname}", OperationType.NOT_NULL, null, null},
                {"${firstname} IS not null", "${firstname}", OperationType.NOT_NULL, null, null},
                {"${age} lt 5", "${age}", OperationType.LT, 5, Integer.class},
                {"${age} lt 5.5", "${age}", OperationType.LT, 5.5, Double.class},
                {"${age} gt 5", "${age}", OperationType.GT, 5, Integer.class},
                {"${age} gt 5.5", "${age}", OperationType.GT, 5.5, Double.class},
                {"${age} le 5", "${age}", OperationType.LE, 5, Integer.class},
                {"${age} le 5.5", "${age}", OperationType.LE, 5.5, Double.class},
                {"${age} ge 5", "${age}", OperationType.GE, 5, Integer.class},
                {"${age} ge 5.5", "${age}", OperationType.GE, 5.5, Double.class},

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
        if (expectedValue != null) {
            assertThat(condition.getValue()).isInstanceOf(expectedClass);
        }

    }

}
