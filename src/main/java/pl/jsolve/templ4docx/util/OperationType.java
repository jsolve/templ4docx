package pl.jsolve.templ4docx.util;

public enum OperationType {

    EQ("eq"), NE("ne"), NULL("is null"), NOT_NULL("is not null"), LT("lt"), GT("gt"), LE("le"), GE("ge");

    private String text;

    OperationType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
