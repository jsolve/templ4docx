package pl.jsolve.templ4doc.core;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import pl.jsolve.sweetener.collection.Maps;
import pl.jsolve.sweetener.io.Resources;
import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.DocxTemplate;
import pl.jsolve.templ4docx.core.VariablePattern;

public class TemplateTest {

	private final static String PREFIX = "#{";
	private final static String SUFFIX = "}";

	@Test
	public void shouldReadGivenTemplate() throws Exception {
		// given
		DocxTemplate template = new DocxTemplate();
		Docx openedTemplate = template.openTemplate(Resources.findFilePaths("src/test/resources", "doc1.docx").get(0).toString());

		// when
		String textContent = template.readTextContent(openedTemplate);

		// then
		String originalTemplateContent = Resources.asString(Resources.findFilePaths("src/test/resources", "doc1template.txt").get(0));
		assertThat(textContent).isEqualTo(originalTemplateContent);
	}

	@Test
	public void shouldFillGivenTemplate() throws Exception {
		// given
		DocxTemplate template = new DocxTemplate();
		VariablePattern variablePattern = new VariablePattern(PREFIX, SUFFIX);
		template.setVariablePattern(variablePattern);
		Docx openedTemplate = template.openTemplate(Resources.findFilePaths("src/test/resources", "doc1.docx").get(0).toString());

		// when
		template.fillTemplate(openedTemplate, variables(variablePattern));

		// then
		String filledTemplate = template.readTextContent(openedTemplate);
		String originalFilledTemplateContent = Resources.asString(Resources.findFilePaths("src/test/resources", "doc1filledtemplate.txt").get(0));
		assertThat(filledTemplate).isEqualTo(originalFilledTemplateContent);

	}

	private static Map<String, String> variables(VariablePattern variablePattern) {
		Map<String, String> variables = Maps.newHashMap();
		variables.put("imie", "Adam");
		variables.put("nazwisko", "Nowak");
		variables.put("dochodzl", "25356");
		variables.put("dochodgr", "32");
		variables.put("podatekzl", "3124");
		variables.put("podatekgr", "75");
		variables.put("skladkizl", "742");
		variables.put("skladkigr", "51");
		variables.put("miejscowosc", "Bytom");
		variables.put("data", "11-07-2015");

		Map<String, String> poiVariables = Maps.newHashMap();

		for (Entry<String, String> entry : variables.entrySet()) {
			poiVariables.put(PREFIX + entry.getKey().trim() + SUFFIX, entry.getValue());
		}

		return poiVariables;
	}
}
