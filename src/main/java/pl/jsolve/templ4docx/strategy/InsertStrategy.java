package pl.jsolve.templ4docx.strategy;

import pl.jsolve.templ4docx.insert.Insert;
import pl.jsolve.templ4docx.variable.Variable;

public interface InsertStrategy {

    public void insert(Insert insert, Variable variable);
}
