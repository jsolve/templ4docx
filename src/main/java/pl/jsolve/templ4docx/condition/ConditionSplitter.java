package pl.jsolve.templ4docx.condition;

import pl.jsolve.templ4docx.exception.UnsupportedOperationTypeException;
import pl.jsolve.templ4docx.util.Condition;
import pl.jsolve.templ4docx.util.OperationType;

/**
 * Class responsible for parse condition string to Condition object
 * @author Lukasz Stypka
 */
public class ConditionSplitter {

    public Condition splitCondition(String text) {
        Condition condition = new Condition();
        text = text.trim();

        // get variable
        int spaceAfterVariable = text.indexOf(' ');
        String variable = text.substring(0, spaceAfterVariable);
        text = text.substring(spaceAfterVariable);
        condition.setVariable(variable);
        text = text.trim();

        // get operation
        OperationType operationType = getOperationType(text);
        condition.setOperation(operationType);

        if (operationType == OperationType.NULL || operationType == OperationType.NOT_NULL) {
            return condition;
        }
        text = text.substring(operationType.getText().length()).trim();

        // get value
        Object value = getValue(text, operationType);
        condition.setValue(value);

        return condition;
    }

    /**
     * Find operation type in condition String
     * @param text
     * @return OperationType
     */
    private OperationType getOperationType(String text) {
        text = text.trim();
        if (text.toLowerCase().startsWith("is null")) {
            return OperationType.NULL;
        }
        if (text.toLowerCase().startsWith("is not null")) {
            return OperationType.NOT_NULL;
        }
        if (text.toLowerCase().startsWith("eq")) {
            return OperationType.EQ;
        }
        if (text.toLowerCase().startsWith("ne")) {
            return OperationType.NE;
        }
        if (text.toLowerCase().startsWith("lt")) {
            return OperationType.LT;
        }
        if (text.toLowerCase().startsWith("gt")) {
            return OperationType.GT;
        }
        if (text.toLowerCase().startsWith("le")) {
            return OperationType.LE;
        }
        if (text.toLowerCase().startsWith("ge")) {
            return OperationType.GE;
        }

        throw new UnsupportedOperationTypeException("Unrecognized operation : " + text);
    }

    /**
     * Parse string to appropriate value object. lt, gt, le, ge require number value
     * @param text
     * @param operationType
     * @return Object parsed value object
     */
    private Object getValue(String text, OperationType operationType) {
        text = text.trim();

        // remove quote mark
        if (text.startsWith("\"")) {
            text = text.substring(1);
        } else if (text.startsWith("'")) {
            text = text.substring(1);
        } else if (text.startsWith("“")) {
            text = text.substring(1);
        } else if (text.startsWith("”")) {
            text = text.substring(1);
        }

        if (text.endsWith("\"")) {
            text = text.substring(0, text.length() - 1);
        } else if (text.endsWith("'")) {
            text = text.substring(0, text.length() - 1);
        } else if (text.endsWith("“")) {
            text = text.substring(0, text.length() - 1);
        } else if (text.endsWith("”")) {
            text = text.substring(0, text.length() - 1);
        }

        if (operationType == OperationType.GE || operationType == OperationType.GT || operationType == OperationType.LE
                || operationType == OperationType.LT) {
            if (!isNumeric(text)) {
                throw new UnsupportedOperationTypeException(text + " is not a number");
            }
            if (isInteger(text)) {
                return Integer.valueOf(text);
            }
            return Double.valueOf(text);
        }

        return text;
    }

    /**
     * Check if string is integer
     * @param str
     * @return
     */
    private boolean isInteger(String str) {
        return str.matches("-?\\d+?");
    }

    /**
     * Check if variable is a number (Integer or Double)
     * @param str
     * @return
     */
    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
