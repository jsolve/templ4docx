package pl.jsolve.sweetener.text;

import java.util.HashMap;
import java.util.Map;

import pl.jsolve.sweetener.exception.InvalidArgumentException;

public class Escapes {

    private static final Map<Character, String> regexpSpecials = new HashMap<Character, String>();
    private static final Map<Character, String> htmlSpecials = new HashMap<Character, String>();
    private static final Map<Character, String> urlSpecials = new HashMap<Character, String>();
    private static final Map<Character, String> xmlSpecials = new HashMap<Character, String>();
    private static final Map<Character, String> jsonSpecials = new HashMap<Character, String>();

    static {
        regexpSpecial();
        htmlSpecial();
        urlSpecial();
        xmlSpecial();
        jsonSpecial();
    }

    private static void regexpSpecial() {
        regexpSpecials.put('.', "\\.");
        regexpSpecials.put('\\', "\\\\");
        regexpSpecials.put('?', "\\?");
        regexpSpecials.put('*', "\\*");
        regexpSpecials.put('+', "\\+");
        regexpSpecials.put('&', "\\&");
        regexpSpecials.put(':', "\\:");
        regexpSpecials.put('{', "\\{");
        regexpSpecials.put('}', "\\}");
        regexpSpecials.put('[', "\\[");
        regexpSpecials.put(']', "\\]");
        regexpSpecials.put('(', "\\(");
        regexpSpecials.put(')', "\\)");
        regexpSpecials.put('^', "\\^");
        regexpSpecials.put('$', "\\$");
    }

    private static void htmlSpecial() {
        htmlSpecials.put('<', "&lt;");
        htmlSpecials.put('>', "&gt;");
        htmlSpecials.put('&', "&amp;");
        htmlSpecials.put('\"', "&quot;");
        htmlSpecials.put('\t', "&#009;");
        htmlSpecials.put('!', "&#033;");
        htmlSpecials.put('#', "&#035;");
        htmlSpecials.put('$', "&#036;");
        htmlSpecials.put('%', "&#037;");
        htmlSpecials.put('\'', "&#039;");
        htmlSpecials.put('(', "&#040;");
        htmlSpecials.put(')', "&#041;");
        htmlSpecials.put('*', "&#042;");
        htmlSpecials.put('+', "&#043;");
        htmlSpecials.put(',', "&#044;");
        htmlSpecials.put('-', "&#045;");
        htmlSpecials.put('.', "&#046;");
        htmlSpecials.put('/', "&#047;");
        htmlSpecials.put(':', "&#058;");
        htmlSpecials.put(';', "&#059;");
        htmlSpecials.put('=', "&#061;");
        htmlSpecials.put('?', "&#063;");
        htmlSpecials.put('@', "&#064;");
        htmlSpecials.put('[', "&#091;");
        htmlSpecials.put('\\', "&#092;");
        htmlSpecials.put(']', "&#093;");
        htmlSpecials.put('^', "&#094;");
        htmlSpecials.put('_', "&#095;");
        htmlSpecials.put('`', "&#096;");
        htmlSpecials.put('{', "&#123;");
        htmlSpecials.put('|', "&#124;");
        htmlSpecials.put('}', "&#125;");
        htmlSpecials.put('~', "&#126;");
    }

    private static void urlSpecial() {
        urlSpecials.put(' ', "%20");
        urlSpecials.put('"', "%22");
        urlSpecials.put('<', "%3c");
        urlSpecials.put('>', "%3e");
        urlSpecials.put('#', "%23");
        urlSpecials.put('%', "%25");
        urlSpecials.put('{', "%7b");
        urlSpecials.put('}', "%7d");
        urlSpecials.put('|', "%7c");
        urlSpecials.put('\\', "%5c");
        urlSpecials.put('^', "%5e");
        urlSpecials.put('~', "%7e");
        urlSpecials.put('[', "%5b");
        urlSpecials.put(']', "%5d");
        urlSpecials.put('`', "%60");
    }

    private static void xmlSpecial() {
        xmlSpecials.put('<', "&lt;");
        xmlSpecials.put('>', "&gt;");
        xmlSpecials.put('\"', "&quot;");
        xmlSpecials.put('\'', "&#039;");
        xmlSpecials.put('&', "&amp;");
    }

    private static void jsonSpecial() {
        jsonSpecials.put('\"', "\\\"");
        jsonSpecials.put('\\', "\\\\");
        jsonSpecials.put('/', "\\/");
        jsonSpecials.put('\b', "\\b");
        jsonSpecials.put('\f', "\\f");
        jsonSpecials.put('\n', "\\n");
        jsonSpecials.put('\r', "\\r");
        jsonSpecials.put('\t', "\\t");
    }

    public static String escapeRegexp(String value) {
        return escape(value, regexpSpecials);
    }

    public static String escapeHtml(String value) {
        return escape(value, htmlSpecials);
    }

    public static String escapeUrl(String value) {
        return escape(value, urlSpecials);
    }

    public static String escapeXml(String value) {
        return escape(value, xmlSpecials);
    }

    public static String escapeJson(String value) {
        return escape(value, jsonSpecials);
    }

    public static String escape(String value, Map<Character, String> specials) {
        if (value == null) {
            throw new InvalidArgumentException("String cannot be null");
        }
        if (specials == null) {
            throw new InvalidArgumentException("Map cannot be null");
        }
        StringBuffer sb = new StringBuffer(value);
        int countOfReplacements = 0;
        for (int i = 0; i < value.length(); i++) {
            if (specials.containsKey(value.charAt(i))) {
                sb.deleteCharAt(i + countOfReplacements).insert(i + countOfReplacements, specials.get(value.charAt(i)));
                countOfReplacements += specials.get(value.charAt(i)).length() - 1;
            }
        }
        return sb.toString();
    }

    public static Map<Character, String> getRegexpspecials() {
        return new HashMap<Character, String>(regexpSpecials);
    }

    public static Map<Character, String> getHtmlspecials() {
        return new HashMap<Character, String>(htmlSpecials);
    }

    public static Map<Character, String> getUrlspecials() {
        return new HashMap<Character, String>(urlSpecials);
    }

    public static Map<Character, String> getXmlspecials() {
        return new HashMap<Character, String>(xmlSpecials);
    }

    public static Map<Character, String> getJsonspecials() {
        return new HashMap<Character, String>(jsonSpecials);
    }

}