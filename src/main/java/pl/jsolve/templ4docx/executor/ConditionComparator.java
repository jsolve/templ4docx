package pl.jsolve.templ4docx.executor;

import pl.jsolve.sweetener.core.Reflections;
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
        try {
            ConditionVariable foundCondition = variables.getCondition(condition.getVariable());

            if (foundCondition != null) {
                return foundCondition;
            }

            String variable = condition.getVariable();
            if (variable.contains(".")) {
                String[] splitValues = variable.split("\\.");
                String concatenatedValue = "";
                for (String splitValue : splitValues) {
                    concatenatedValue += splitValue;
                    foundCondition = variables.getCondition(concatenatedValue);

                    if (foundCondition != null) {
                        Object foundObject = foundCondition.getValue();
                        String key = variable.substring(concatenatedValue.length());
                        if (key.startsWith(".")) {
                            key = key.substring(1);
                        }
                        Object fieldValue = Reflections.getFieldValue(foundObject, key);
                        return new ConditionVariable(variable, fieldValue);
                    }

                    concatenatedValue += ".";
                }
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    private int compare(Object fistParam, Object secondParam) {
        Double firstParamAsDouble = Double.parseDouble(fistParam.toString());
        Double secondParamAsDouble = Double.parseDouble(secondParam.toString());
        return firstParamAsDouble.compareTo(secondParamAsDouble);
    }
}
