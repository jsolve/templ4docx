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

import pl.jsolve.sweetener.text.Strings;

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

    public void replaceVariables(Docx docx, Map<String, String> tags, VariablePattern variablePattern) {
        String[] keyArray = tags.keySet().toArray(new String[0]);
        String[] valueArray = tags.values().toArray(new String[0]);

        for (XWPFParagraph p : docx.getDocument().getParagraphs()) {
            execute(p.getRuns(), keyArray, valueArray, variablePattern);
        }

        for (XWPFTable tbl : docx.getDocument().getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        execute(p.getRuns(), keyArray, valueArray, variablePattern);
                    }
                }
            }
        }
    }

    private void execute(List<XWPFRun> runs, String[] keyArray, String[] valueArray, VariablePattern variablePattern) {
        if (runs == null || runs.isEmpty()) {
            return;
        } else if (runs.size() == 1) {
            XWPFRun xwpfRun = runs.get(0);
            String text = xwpfRun.getText(0);
            if (text != null) {
                text = StringUtils.replaceEach(text, keyArray, valueArray);
                xwpfRun.setText(text, 0);
            }
        } else {
            for (XWPFRun xwpfRun : runs) {
                String text = xwpfRun.getText(0);
                if (text != null) {
                    text = StringUtils.replaceEach(text, keyArray, valueArray);
                    xwpfRun.setText(text, 0);
                }
            }

            // validate whether xwpfRun contains any variable pattern which are not recognized
            String notRecognizedVariable = "";
            String notRecognizedVariablePrefix = "";
            int notRecognizedVariableStartIndex = -1;
            for (int i = 0; i < runs.size(); i++) {
                String text = runs.get(i).getText(0);
                if (text != null) {
                    text = text.trim();
                    // check whether variable is started but not ended

                    if (notRecognizedVariableStartIndex != -1) {
                        if (!Strings.indexesOf(text, variablePattern.getSuffix()).isEmpty()) {
                            notRecognizedVariable += text.substring(0, Strings.indexesOf(text, variablePattern.getSuffix()).get(0) + 1);
                            notRecognizedVariable = StringUtils
                                    .replaceEach(notRecognizedVariable, keyArray, valueArray);
                            XWPFRun startRun = runs.get(notRecognizedVariableStartIndex);
                            String textFromStartRun = startRun.getText(0);
                            textFromStartRun = textFromStartRun.replace(notRecognizedVariablePrefix,
                                    notRecognizedVariable);
                            startRun.setText(textFromStartRun, 0);

                            // clean runs between start and end variable pattern
                            for (int j = notRecognizedVariableStartIndex + 1; j < i; j++) {
                                runs.get(j).setText("", 0);
                            }
                            text = runs.get(i).getText(0);
                            Integer suffixIndex = Strings.indexesOf(text, variablePattern.getSuffix()).get(0);
                            runs.get(i).setText(text.substring(suffixIndex + 1), 0);
                            i = notRecognizedVariableStartIndex;

                            notRecognizedVariableStartIndex = -1;
                            notRecognizedVariable = "";
                            notRecognizedVariablePrefix = "";
                            continue;
                        }
                        notRecognizedVariable += text;
                    }

                    if (!Strings.indexesOf(text, variablePattern.getPrefix()).isEmpty()
                            && Strings.indexesOf(text, variablePattern.getSuffix()).isEmpty()) {
                        notRecognizedVariableStartIndex = i;
                        notRecognizedVariable = text;
                        Integer prefixIndex = Strings.indexesOf(text, variablePattern.getPrefix()).get(0);
                        notRecognizedVariablePrefix = text.substring(prefixIndex);
                    }
                }
            }
        }
    }

}
