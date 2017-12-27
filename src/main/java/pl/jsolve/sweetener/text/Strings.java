package pl.jsolve.sweetener.text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Strings {

    public static List<Integer> indexesOf(String sourceObject, String sequence, boolean ignoreRegexp) {
        if (sequence == null) {
            return new ArrayList<Integer>();
        }
        if (ignoreRegexp) {
            return indexesOf(sourceObject, Escapes.escapeRegexp(sequence));
        }
        return indexesOf(sourceObject, sequence);
    }

    public static List<Integer> indexesOf(String sourceObject, String sequence) {
        List<Integer> indexes = new ArrayList<Integer>();
        if (sourceObject == null || sequence == null || sequence.isEmpty()) {
            return indexes;
        }
        Pattern pattern = Pattern.compile(sequence);
        Matcher matcher = pattern.matcher(sourceObject);
        while (matcher.find()) {
            indexes.add(matcher.start());
        }
        return indexes;
    }

    public static List<Integer> indexesOf(String sourceObject, Character c, boolean ignoreRegexp) {
        if (c == null) {
            return new ArrayList<Integer>();
        }
        if (ignoreRegexp) {
            return indexesOf(sourceObject, Escapes.escapeRegexp(c.toString()));
        }
        return indexesOf(sourceObject, c.toString());
    }

    public static List<Integer> indexesOf(String sourceObject, Character c) {
        return indexesOf(sourceObject, c, false);
    }

}