package pl.jsolve.templ4docx.condition;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import pl.jsolve.templ4docx.executor.ConditionComparator;
import pl.jsolve.templ4docx.util.Condition;
import pl.jsolve.templ4docx.util.OperationType;
import pl.jsolve.templ4docx.variable.ConditionVariable;
import pl.jsolve.templ4docx.variable.Variables;

@RunWith(value = Parameterized.class)
public class ConditionComparatorTest {

    private ConditionComparator conditionComparator;
    private String key;
    private Object variableValue;
    private Object conditionValue;
    private OperationType operationType;
    private Boolean expectedResult;

    public ConditionComparatorTest(String key, Object variableValue, Object conditionValue,
            OperationType operationType, Boolean expectedResult) {
        this.key = key;
        this.variableValue = variableValue;
        this.conditionValue = conditionValue;
        this.operationType = operationType;
        this.expectedResult = expectedResult;
        conditionComparator = new ConditionComparator();
    }

    @Test
    public void paramTest() throws Exception {
        // given
        Variables variables = new Variables();
        if (key != null) {
            variables.addConditionVariable(new ConditionVariable(key, variableValue));
        }
        Condition condition = new Condition(key, operationType, conditionValue);

        // when
        boolean fulfilledCondition = conditionComparator.compare(condition, variables);

        // then
        assertThat(fulfilledCondition).isEqualTo(expectedResult);
    }

    @Parameters(name = "{index}: given {0} (variable {1}, Operation type {2}, value {3})")
    public static Iterable<Object[]> data1() {
        return Arrays.asList(new Object[][] { 
                
                {"${age}", "27", 27, OperationType.EQ, false},
                {null, "27", 27, OperationType.EQ, false}, {"${age}", 27, 27, OperationType.EQ, true},
                {"${age}", 28, 27, OperationType.EQ, false}, {"${name}", "Lukasz", "Lukasz", OperationType.EQ, true},
                {"${name}", "Lukasz", "Tomek", OperationType.EQ, false}, 
                
                {"${age}", 28, 27, OperationType.NE, true},
                {"${age}", 27, 27, OperationType.NE, false}, {"${name}", "Lukasz", "Tomek", OperationType.NE, true},
                {"${name}", "Lukasz", "Lukasz", OperationType.NE, false},
                
                {"${name}", null, null, OperationType.NULL, true},
                {"${name}", "Lukasz", null, OperationType.NULL, false},
             
                {"${name}", null, "Lukasz", OperationType.NOT_NULL, false},
                
                {"${age}", 35, "18", OperationType.GE, true}, {"${age}", 18, "18", OperationType.GE, true},
                {"${age}", 18.1234567, "18.1234567", OperationType.GE, true},
                {"${age}", 18.123456789, 18.123456789, OperationType.GE, true},
                {"${age}", 17.9999999999, "18.00000000001", OperationType.GE, false},
                {"${age}", 35, "18", OperationType.GT, true}, {"${age}", 18, "18", OperationType.GT, false},
                {"${age}", 18.1234567, "18.1234567", OperationType.GT, false},
                {"${age}", 18.123456789, "18.123456789", OperationType.GT, false},
                {"${age}", 17, "18", OperationType.GT, false},
                {"${age}", 17.9999999999, "18.000000001", OperationType.GT, false},

                {"${age}", 35, "18", OperationType.LE, false}, {"${age}", 18, "18", OperationType.LE, true},
                {"${age}", 18.1234567, "18.1234567", OperationType.LE, true},
                {"${age}", 18.123456789, "18.123456789", OperationType.LE, true},
                {"${age}", 17, "18", OperationType.LE, true},
                {"${age}", 17.9999999999, "18.000000001", OperationType.LE, true},
                {"${age}", "18.000000001", 17.9999999999, OperationType.LE, false},

                {"${age}", 35, "18", OperationType.LT, false}, {"${age}", 18, "18", OperationType.LT, false},
                {"${age}", 18.1234567, "18.1234567", OperationType.LT, false},
                {"${age}", 18.123456789, "18.123456789", OperationType.LT, false},
                {"${age}", 17, "18", OperationType.LT, true},
                {"${age}", 17.9999999999, "18.000000001", OperationType.LT, true},
                {"${age}", "18.000000001", 17.9999999999, OperationType.LT, false},

        });
    };
}
