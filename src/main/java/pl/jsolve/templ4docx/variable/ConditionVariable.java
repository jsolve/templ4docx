package pl.jsolve.templ4docx.variable;

public class ConditionVariable {

    private final String key;
    private final Object value;

    public ConditionVariable(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

}
