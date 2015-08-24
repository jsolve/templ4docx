package pl.jsolve.templ4docx;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.variable.ConditionVariable;
import pl.jsolve.templ4docx.variable.Variables;

public class Main {

    public static void main(String[] args) {
        Docx docx = new Docx("E:\\tmp\\template.docx");
        Variables var = new Variables();
        var.addConditionVariable(new ConditionVariable("age", 15));
        var.addConditionVariable(new ConditionVariable("name", "Łukasz"));
        
        var.addConditionVariable(new ConditionVariable("var", "cos"));
        
        docx.fillTemplate(var);
        docx.save("E:\\tmp\\tmp.docx");
    }
}
