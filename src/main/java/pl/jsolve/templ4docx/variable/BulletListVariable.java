package pl.jsolve.templ4docx.variable;

import java.util.List;

public class BulletListVariable implements Variable {

    private String key;
    private List<? extends Variable> variables;

    public BulletListVariable(String key, List<? extends Variable> variables) {
        this.key = key;
        this.variables = variables;
    }

    public String getKey() {
        return key;
    }

    public List<? extends Variable> getVariables() {
        return variables;
    }

}
