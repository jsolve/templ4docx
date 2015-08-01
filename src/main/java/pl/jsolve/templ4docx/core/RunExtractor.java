package pl.jsolve.templ4docx.core;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import pl.jsolve.sweetener.text.Strings;

public class RunExtractor {

    public void execute(List<XWPFRun> runs, String[] keyArray, String[] valueArray, VariablePattern variablePattern) {
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
                            notRecognizedVariable += text.substring(0,
                                    Strings.indexesOf(text, variablePattern.getSuffix()).get(0) + 1);
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

                    String prefix = getFirstChar(variablePattern.getPrefix());
                    if (!Strings.indexesOf(text, prefix).isEmpty()
                            && Strings.indexesOf(text, variablePattern.getSuffix()).isEmpty()) {
                        notRecognizedVariableStartIndex = i;
                        notRecognizedVariable = text;
                        List<Integer> indexesOf = Strings.indexesOf(text, prefix);
                        Integer prefixIndex = indexesOf.get(0);
                        notRecognizedVariablePrefix = text.substring(prefixIndex);
                    }
                }
            }
        }
    }

    private String getFirstChar(String prefix) {
        if (prefix.length() == 1) {
            return prefix;
        } else if (prefix.startsWith("\\") && prefix.length() > 1) {
            return prefix.substring(0, 2);
        }
        return prefix.substring(0, 1);
    }

}
