# templ4docx - Utility library for filling templates in docx files. 
The heart of this library is `apache-poi`

Thanks to `templ4docx` you can read the entire content (as simple String) of docx file, you can find all variables, register variables pattern and you can fill docx template file by your own map of variables.

The recommended way to get started using templ4docx in your project is a dependency management system – the snippet below can be copied and pasted into your build.
```
<dependency>
	<groupId>pl.jsolve</groupId>
	<artifactId>templ4docx</artifactId>
	<version>1.0.0</version>
</dependency>
```

[Direct link to jar file ](https://oss.sonatype.org/content/groups/public/pl/jsolve/templ4docx/1.0.0/templ4docx-1.0.0.jar)


## Example usage

```
// create new instance of docx template
DocxTemplate template = new DocxTemplate(); 

// set the variable pattern. In this example the pattern is as follows: #{variableName}
template.setVariablePattern(new VariablePattern("#{", "}"));  

// path to docx template
String path = "C://document.docx"; 

// open docx file
Docx openedTemplate = template.openTemplate(path); 

// read docx content as simple text
String content = template.readTextContent(openedTemplate); 

// and display it
System.out.println(content); 

// find all variables satisfying the pattern #{...}
List<String> findVariables = template.findVariables(openedTemplate); 

// and display it
for (String var : findVariables) {
		System.out.println("VARIABLE => " + var);
}

// prepare map of variables for template
Map<String, String> variables = new HashMap<String, String>();
variables.put("#{firstName}", "John");
variables.put("#{lastName}", "Sky");

// fill template by given map of variables
Docx filledTemplate = template.fillTemplate(path, variables); 

// save filled document
template.save(filledTemplate, "C://filledDocument.docx");

```
