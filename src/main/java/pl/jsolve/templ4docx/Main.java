package pl.jsolve.templ4docx;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.variable.Variables;

public class Main {

    public static void main(String[] args) {
        Docx docx = new Docx("E:\\tmp\\template.docx");
        Variables var = new Variables();
        docx.fillTemplate(var);
        docx.save("E:\\tmp\\tmp.docx");
    }
}
