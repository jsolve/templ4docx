package pl.jsolve.templ4docx.condition;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import pl.jsolve.sweetener.collection.Collections;
import pl.jsolve.templ4docx.executor.ConditionComparator;
import pl.jsolve.templ4docx.util.Condition;
import pl.jsolve.templ4docx.util.OperationType;
import pl.jsolve.templ4docx.variable.ConditionVariable;
import pl.jsolve.templ4docx.variable.Variables;

@RunWith(value = Parameterized.class)
public class ComplexObjectConditionComparatorTest {

    private ConditionComparator conditionComparator;
    private String key;
    private String conditionKey;
    private Object variableValue;
    private Object conditionValue;
    private OperationType operationType;
    private Boolean expectedResult;

    public ComplexObjectConditionComparatorTest(String key, String conditionKey, Object variableValue, Object conditionValue,
            OperationType operationType, Boolean expectedResult) {
        this.key = key;
        this.conditionKey = conditionKey;
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
        Condition condition = new Condition(conditionKey, operationType, conditionValue);

        // when
        boolean fulfilledCondition = conditionComparator.compare(condition, variables);

        // then
        assertThat(fulfilledCondition).isEqualTo(expectedResult);
    }

    @Parameters(name = "{index}: given {0} (variable {1}, Operation type {2}, value {3})")
    public static Iterable<Object[]> data1() {
        return Arrays.asList(new Object[][] { 
              
                {"${person}", "${person}.age", new Person("Lukasz", 27, null), 27, OperationType.EQ, true},
                {"${per.son}", "${per.son}.age", new Person("Lukasz", 27, null), 27, OperationType.EQ, true},
                {"${p.e.r.s.o.n}", "${p.e.r.s.o.n}.age", new Person("Lukasz", 27, null), 27, OperationType.EQ, true},
                {"${person}", "${person}.children.size", new Person("Lukasz", 27, Collections.newArrayList("a", "b", "c")), 3, OperationType.EQ, true},
                 
        });
    };
}
