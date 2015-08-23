package pl.jsolve.templ4docx.util;

public class StringUtil {

    /**
     * Check if string is integer
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        return str.matches("-?\\d+?");
    }

    /**
     * Check if variable is a number (Integer or Double)
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
