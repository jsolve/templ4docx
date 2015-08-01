package pl.jsolve.templ4docx;

import java.util.HashMap;
import java.util.Map;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.DocxTemplate;

public class Main {

    public static void main(String[] args) {

        DocxTemplate template = new DocxTemplate();

        String path = "E://template2.docx";

        Docx openedTemplate = template.openTemplate(path);

        Map<String, String> variables = new HashMap<String, String>();
        variables.put("${first}", "John");
        variables.put("${last}", "Sky");
        variables.put("${telefon}", "3435");
        
        // fill template by given map of variables
        Docx filledTemplate = template.fillTemplate(path, variables);

        // save filled document
        String content2 = template.readTextContent(filledTemplate);
        System.out.println(content2);
        template.save(filledTemplate, "E://filledDocument.docx");

    }

}
