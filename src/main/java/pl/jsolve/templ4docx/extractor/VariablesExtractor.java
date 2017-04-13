package pl.jsolve.templ4docx.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import pl.jsolve.templ4docx.core.VariablePattern;

/**
 * Utility class which returns list of variables found in text content of template .docx file
 * @author Lukasz Stypka
 */
public class VariablesExtractor {

    /**
     * Main method responsible for extract all variables which satisfy given variable pattern.
     * @param content
     * @param variablePattern
     * @return List of found variables
     */
    public List<String> extract(String content, VariablePattern variablePattern) {
        final List<String> tagValues = new ArrayList<String>();
        Pattern pattern = Pattern.compile(getFirstChar(variablePattern.getPrefix()) + "(.*?)"
                + getFirstChar(variablePattern.getSuffix()));
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            tagValues.add(matcher.group());
        }

        List<String> filteredValues = new ArrayList<String>();
        for (String value : tagValues) {
            if (StringUtils.isNoneBlank(value) && StringUtils.startsWith(value, variablePattern.getOriginalPrefix())
                    && StringUtils.endsWith(value, variablePattern.getOriginalSuffix())) {
                filteredValues.add(value);
            }
        }

        return filteredValues;
    }

    /**
     * Return first char. If the first char is \ it will mean that char is escaped, so the second char is added to
     * result
     * @param prefix
     * @return First escaped char
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
