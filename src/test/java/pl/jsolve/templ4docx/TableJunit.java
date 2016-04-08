package pl.jsolve.templ4docx;

import org.junit.Test;
import pl.jsolve.sweetener.collection.Collections;
import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.variable.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by serv on 16/4/8.
 */
public class TableJunit {

    @Test
    public void test(){
        URL resource = TableJunit.class.getClassLoader().getResource("advance_tmp.docx");
        Docx docx = new Docx(resource.getPath());
        Variables var = new Variables();

        TableVariable tableVariable = new TableVariable();

        List<Variable> nameColumnVariables = new ArrayList<Variable>();
        List<Variable> ageColumnVariables = new ArrayList<Variable>();
        List<Variable> logoColumnVariables = new ArrayList<Variable>();
        List<Variable> languagesColumnVariables = new ArrayList<Variable>();
        List<Variable> contentColumnVariables = new ArrayList<Variable>();
        List<Variable> aColumnVariables = new ArrayList<Variable>();
        List<Variable> bColumnVariables = new ArrayList<Variable>();
        List<Variable> cColumnVariables = new ArrayList<Variable>();


        for (Student student : getStudents()) {
            nameColumnVariables.add(new TextVariable("${name}", student.getName()));
            ageColumnVariables.add(new TextVariable("${age}", student.getAge().toString()));
            logoColumnVariables.add(new ImageVariable("${logo}", student.getLogoPath(),40,40));

            List<Variable> languages = new ArrayList<Variable>();
            for (String language : student.getLanguages()) {
                languages.add(new TextVariable("${languages}", language));
            }
            languagesColumnVariables.add(new BulletListVariable("${languages}", languages));

            contentColumnVariables.add(new TextVariable("${content}",student.getContent()));
            aColumnVariables.add(new TextVariable("${a}","a"+student.getAge()));
            bColumnVariables.add(new TextVariable("${b}","b"+student.getAge()));
            cColumnVariables.add(new TextVariable("${c}","c"+student.getAge()));
        }

        tableVariable.addVariable(nameColumnVariables);
        tableVariable.addVariable(ageColumnVariables);
        tableVariable.addVariable(logoColumnVariables);
        tableVariable.addVariable(languagesColumnVariables);
        tableVariable.addVariable(contentColumnVariables);
        tableVariable.addVariable(aColumnVariables);
        tableVariable.addVariable(bColumnVariables);
        tableVariable.addVariable(cColumnVariables);

        var.addTableVariable(tableVariable);
        var.addTextVariable(new TextVariable("${title}","测试表"));
        var.addTextVariable(new TextVariable("${end}","结束"));
        docx.fillTemplate(var);
        docx.save("filledTable.docx");
    }

    private List<Student> getStudents(){

        URL smiling = TableJunit.class.getClassLoader().getResource("smiling.png");
        URL crying = TableJunit.class.getClassLoader().getResource("crying.png");


        List<Student> students = new ArrayList<Student>();
        students.add(new Student("Lukasz", 28, smiling.getPath(), Collections.newArrayList("Polish", "English"),"aaaaaaaaa","a","b","c"));
        students.add(new Student("Tomek", 24, crying.getPath(), Collections.newArrayList("Polish", "English", "French"),"bbbbbbbb","d","e","f"));
        return students;
    }
}
