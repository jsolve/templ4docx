package pl.jsolve.templ4docx.core;

import pl.jsolve.sweetener.text.Escapes;

/**
 * Class represents pattern of variable in .docx document. Default variable pattern is: prefix = '${' and suffix = '}'
 * which is equivalent to ${variableName}
 * @author Łukasz Stypka
 */
public class VariablePattern {

    private final String prefix;
    private final String suffix;

    private final String originalPrefix;
    private final String originalSuffix;

    public VariablePattern(String prefix, String suffix) {
        this.originalPrefix = prefix;
        this.originalSuffix = suffix;
        this.prefix = Escapes.escapeRegexp(prefix);
        this.suffix = Escapes.escapeRegexp(suffix);
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getOriginalPrefix() {
        return originalPrefix;
    }

    public String getOriginalSuffix() {
        return originalSuffix;
    }

}
