package pl.jsolve.templ4docx.cleaner;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;

import pl.jsolve.sweetener.text.Strings;
import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.extractor.KeyExtractor;
import pl.jsolve.templ4docx.util.Key;
import pl.jsolve.templ4docx.util.ParagraphUtil;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * One variable may be shared by many XWPFRun. This class moves split variable to one XWPFRun
 * @author Lukasz Stypka
 */
public class DocumentCleaner {

    private KeyExtractor keyExtractor;

    public DocumentCleaner() {
        this.keyExtractor = new KeyExtractor();
    }

    /**
     * Main method for cleaning XWPFRun in whole document. This method moves split variable to one XWPFRun
     * @param docx Docx
     * @param variables variables
     * @param variablePattern variablePattern
     */
    public void clean(Docx docx, Variables variables, VariablePattern variablePattern) {
        List<Key> keys = keyExtractor.extractKeys(variables);
        for (XWPFParagraph paragraph : ParagraphUtil.getAllParagraphs(docx.getXWPFDocument(), true)) {
            clean(paragraph.getRuns(), keys, variablePattern);
        }
    }

    /**
     * Clean content of tables. This method is invoked recursively for each table
     * @param tables
     * @param keys
     * @param variablePattern
     */
    private void cleanTables(List<XWPFTable> tables, List<Key> keys, VariablePattern variablePattern) {
        for (XWPFTable table : tables) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        if (!cell.getTables().isEmpty()) {
                            cleanTables(cell.getTables(), keys, variablePattern);
                        }
                        clean(paragraph.getRuns(), keys, variablePattern);
                    }
                }
            }
        }
    }

    /**
     * Clean list of XWPFRun. If one variable is split between many XWPFRun, this method will move this variable to run
     * where variable begins. The text from other XWPFRuns which contain parts of found variable is cleaned.
     * @param runs
     * @param keys
     * @param variablePattern
     */
    private void clean(List<XWPFRun> runs, List<Key> keys, VariablePattern variablePattern) {
        if (runs == null || runs.isEmpty() || runs.size() == 1) {
            return;
        } else {
            // validate whether xwpfRun contains any variable pattern which are not recognized
            String notRecognizedVariable = "";
            String notRecognizedPrefix = "";
            int notRecognizedVariableStartIndex = -1;
            for (int i = 0; i < runs.size(); i++) {
                String text = runs.get(i).getText(0);
                if (text != null) {
                    // check whether variable is started but not ended

                    if (notRecognizedVariableStartIndex != -1) {
                        List<Integer> suffixIndexesOf = Strings.indexesOf(text, variablePattern.getSuffix());
                        if (!suffixIndexesOf.isEmpty()) {
                            notRecognizedVariable += text.substring(0, suffixIndexesOf.get(0) + 1);
                            XWPFRun startRun = runs.get(notRecognizedVariableStartIndex);
                            boolean executeResult = containsKey(keys, notRecognizedVariable);
                            if (executeResult) {
                                // Set found variable to start run
                                String textFromStartRun = startRun.getText(0);
                                textFromStartRun = StringUtils.replace(textFromStartRun, notRecognizedPrefix,
                                        notRecognizedVariable);
                                startRun.setText(textFromStartRun, 0);

                                // clean runs between start and end variable pattern
                                for (int j = notRecognizedVariableStartIndex + 1; j < i; j++) {
                                    runs.get(j).setText("", 0);
                                }
                                text = runs.get(i).getText(0);
                                Integer suffixIndex = suffixIndexesOf.get(0);
                                runs.get(i).setText(text.substring(suffixIndex + 1), 0);
                                i = notRecognizedVariableStartIndex;
                            }

                            notRecognizedVariableStartIndex = -1;
                            notRecognizedVariable = "";
                            continue;
                        }
                        notRecognizedVariable += text;
                    }

                    String prefix = getFirstChar(variablePattern.getPrefix());
                    List<Integer> prefixIndexesOf = Strings.indexesOf(text, prefix);
                    if (!prefixIndexesOf.isEmpty() && Strings.indexesOf(text, variablePattern.getSuffix()).isEmpty()) {
                        notRecognizedVariableStartIndex = i;
                        notRecognizedPrefix = text.substring(prefixIndexesOf.get(prefixIndexesOf.size() - 1));
                        notRecognizedVariable = notRecognizedPrefix;
                    }
                }
            }
        }
    }

    /**
     * @param keys
     * @param textContent
     * @return boolean which will indicate, if given string contains any key from list
     */
    private boolean containsKey(List<Key> keys, String textContent) {
        for (Key key : keys) {
            if (StringUtils.contains(textContent, key.getKey())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param prefix
     * @return Escaped first char. If string starts with \, the second char will be also included in returned string
     */
    private String getFirstChar(String prefix) {
        if (prefix.length() == 1) {
            return prefix;
        } else if (prefix.startsWith("\\") && prefix.length() > 1) {
            return prefix.substring(0, 2);
        }
        return prefix.substring(0, 1);
    }

}
