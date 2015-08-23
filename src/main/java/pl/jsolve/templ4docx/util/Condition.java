package pl.jsolve.templ4docx.util;

public class Condition {

    private String variable;
    private OperationType operation;
    private Object value;

    public Condition() {
    }

    public Condition(String variable, OperationType operation, Object value) {
        this.variable = variable;
        this.operation = operation;
        this.value = value;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public OperationType getOperation() {
        return operation;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
