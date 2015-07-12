package pl.jsolve.templ4docx.core;

import pl.jsolve.sweetener.text.Escapes;

/**
 * 
 * Class represents pattern of variable in .docx document. Default variable
 * pattern is: prefix = '${' and suffix = '}' which is equivalent to
 * ${variableName}
 * 
 * @param String
 *            prefix - default value is '${'
 * @param String
 *            suffix - default value is '}'
 * @author £ukasz Stypka
 * 
 */
public class VariablePattern {

	private final String prefix;
	private final String suffix;

	public VariablePattern(String prefix, String suffix) {
		this.prefix = Escapes.escapeRegexp(prefix);
		this.suffix = Escapes.escapeRegexp(suffix);
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

}
