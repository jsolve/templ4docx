package pl.jsolve.templ4docx.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class VariablesExtractor {

	public List<String> extract(String content, VariablePattern variablePattern) {
		final List<String> tagValues = new ArrayList<String>();
		Pattern pattern = Pattern.compile(variablePattern.getPrefix() + "(.*?)" + variablePattern.getSuffix());
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			tagValues.add(matcher.group());
		}

		return tagValues;
	}

	public void replaceVariables(Docx docx, Map<String, String> tags) {
		String[] keyArray = tags.keySet().toArray(new String[0]);
		String[] valueArray = tags.values().toArray(new String[0]);

		for (XWPFParagraph p : docx.getDocument().getParagraphs()) {
			List<XWPFRun> runs = p.getRuns();
			if (runs != null) {
				for (XWPFRun r : runs) {

					String text = r.getText(0);
					if (text != null) {
						text = StringUtils.replaceEach(text, keyArray, valueArray);
						r.setText(text, 0);
					}
				}
			}
		}

		for (XWPFTable tbl : docx.getDocument().getTables()) {
			for (XWPFTableRow row : tbl.getRows()) {
				for (XWPFTableCell cell : row.getTableCells()) {
					for (XWPFParagraph p : cell.getParagraphs()) {
						for (XWPFRun r : p.getRuns()) {
							String text = r.getText(0);
							text = StringUtils.replaceEach(text, keyArray, valueArray);
							r.setText(text, 0);
						}
					}
				}
			}
		}
	}

}
