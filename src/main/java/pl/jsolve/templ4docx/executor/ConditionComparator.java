package pl.jsolve.templ4docx.executor;

import pl.jsolve.templ4docx.util.Condition;
import pl.jsolve.templ4docx.variable.ConditionVariable;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * Class returns information whether given condition is fulfilled
 * @author Lukasz Stypka
 */
public class ConditionComparator {

    /**
     * Returns information whether given condition is fulfilled
     * @param condition
     * @param variables
     * @return
     */
    public boolean compare(Condition condition, Variables variables) {
        ConditionVariable foundCondition = getVariable(condition, variables);

        if (foundCondition == null) {
            return false;
        }

        switch (condition.getOperation()) {
        case EQ:
            return foundCondition.getValue().equals(condition.getValue());
        case NE:
            return !foundCondition.getValue().equals(condition.getValue());
        case NOT_NULL:
            return foundCondition.getValue() != null;
        case NULL:
            return foundCondition.getValue() == null;
        case GE:
            return compare(foundCondition.getValue(), condition.getValue()) >= 0;
        case GT:
            return compare(foundCondition.getValue(), condition.getValue()) > 0;
        case LE:
            return compare(foundCondition.getValue(), condition.getValue()) <= 0;
        case LT:
            return compare(foundCondition.getValue(), condition.getValue()) < 0;
        }

        return false;
    }

    private ConditionVariable getVariable(Condition condition, Variables variables) {
        ConditionVariable foundCondition = variables.getCondition(condition.getVariable());
        return foundCondition;
    }

    private int compare(Object fistParam, Object secondParam) {
        Double firstParamAsDouble = Double.parseDouble(fistParam.toString());
        Double secondParamAsDouble = Double.parseDouble(secondParam.toString());
        return firstParamAsDouble.compareTo(secondParamAsDouble);
    }
}
