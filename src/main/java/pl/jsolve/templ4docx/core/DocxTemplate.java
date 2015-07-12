package pl.jsolve.templ4docx.core;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import pl.jsolve.sweetener.io.Resources;
import pl.jsolve.templ4docx.exception.OpenDocxException;

/**
 * 
 * The main class responsible for reading docx template, finding variables,
 * saving completed docx template
 * 
 * @author £ukasz Stypka
 * 
 */
public class DocxTemplate {

	private VariablePattern variablePattern = new VariablePattern("${", "}");

	public Docx openTemplate(String path) {
		return new Docx(path);
	}

	public String readTextContent(Docx docx) {
		XWPFWordExtractor extractor = null;
		try {
			extractor = new XWPFWordExtractor(docx.getDocument());
			return extractor.getText();
		} finally {
			if (extractor != null) {
				Resources.closeStream(extractor);
			}
		}
	}

	public List<String> findVariables(Docx docx) {
		VariablesExtractor extractor = new VariablesExtractor();
		String content = readTextContent(docx);
		return extractor.extract(content, variablePattern);
	}

	public Docx fillTemplate(String path, Map<String, String> templateVariables) {
		Docx docx = openTemplate(path);
		fillTemplate(docx, templateVariables);
		return docx;
	}

	public void fillTemplate(Docx docx, Map<String, String> templateVariables) {
		try {
			VariablesExtractor extractor = new VariablesExtractor();
			extractor.replaceVariables(docx, templateVariables);
		} catch (Exception e) {
			throw new OpenDocxException(e.getMessage(), e.getCause());
		}
	}

	public void save(Docx docx, String outputPath) {
		try {
			docx.getDocument().write(new FileOutputStream(outputPath));
		} catch (Exception ex) {
			throw new OpenDocxException(ex.getMessage(), ex.getCause());
		}
	}

	public void save(Docx docx, OutputStream outputStream) {
		try {
			docx.getDocument().write(outputStream);
		} catch (Exception ex) {
			throw new OpenDocxException(ex.getMessage(), ex.getCause());
		}
	}

	public void setVariablePattern(VariablePattern variablePattern) {
		this.variablePattern = variablePattern;
	}

}
