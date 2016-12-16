# templ4docx - Utility library for filling templates in docx files. 
The heart of this library is `apache-poi`

Thanks to `templ4docx` you can read the entire content (as simple String) of docx file, you can find all variables, register variables pattern and you can fill docx template file by your own map of variables.

The recommended way to get started using templ4docx in your project is a dependency management system – the snippet below can be copied and pasted into your build.
```
<dependency>
	<groupId>pl.jsolve</groupId>
	<artifactId>templ4docx</artifactId>
	<version>2.0.2</version>
</dependency>
```

[Direct link to jar file ](https://oss.sonatype.org/content/groups/public/pl/jsolve/templ4docx/2.0.0/templ4docx-2.0.0.jar)


## Example usage

```
Docx docx = new Docx("template.docx");
docx.setVariablePattern(new VariablePattern("#{", "}"));
    
// preparing variables
Variables variables = new Variables();
variables.addTextVariable(new TextVariable("#{firstname}", "Lukasz"));
variables.addTextVariable(new TextVariable("#{lastname}", "Stypka"));
        
// fill template
docx.fillTemplate(variables);
        
// save filled .docx file
docx.save("lstypka.docx");
```

More details:

* [Text Variables](http://jsolve.github.io/java/templ4docx-2-0-0-text-variables/) <br />
* [Image Variables](http://jsolve.github.io/java/templ4docx-2-0-0-text-variables/) <br />
* [Bullet List Variables](http://jsolve.github.io/java/templ4docx-2-0-0-text-variables/) <br />
* [Table Variables](http://jsolve.github.io/java/templ4docx-2-0-0-text-variables/) <br />

