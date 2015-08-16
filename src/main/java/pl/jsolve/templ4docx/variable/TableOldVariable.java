package pl.jsolve.templ4docx.variable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.jsolve.sweetener.collection.Collections;
import pl.jsolve.sweetener.collection.Maps;
import pl.jsolve.templ4docx.exception.IncorrectNumberOfRowsException;
import pl.jsolve.templ4docx.util.Key;
import pl.jsolve.templ4docx.util.VariableType;

public class TableOldVariable implements Variable {

    private Map<String, List<TextVariable>> textVariables;
    private Map<String, List<ImageVariable>> imageVariables;
    private Map<String, List<BulletListVariable>> bulletListVariables;

    private int numberOfRows = 0;

    public TableOldVariable() {
        textVariables = Maps.newHashMap();
        imageVariables = Maps.newHashMap();
        bulletListVariables = Maps.newHashMap();
    }

    public void addTextVariables(String key, List<String> textVariables) {
        if (numberOfRows != 0) {
            if (textVariables.size() != numberOfRows) {
                throw new IncorrectNumberOfRowsException("Incorrect number of rows. Expected " + numberOfRows
                        + " but was " + textVariables.size());
            }
        } else {
            numberOfRows = textVariables.size();
        }
        this.textVariables.put(key, convert(key, textVariables));
    }

    public void addTextVariables(String key, String... textVariables) {
        if (numberOfRows != 0) {
            if (textVariables.length != numberOfRows) {
                throw new IncorrectNumberOfRowsException("Incorrect number of rows. Expected " + numberOfRows
                        + " but was " + textVariables.length);
            }
        } else {
            numberOfRows = textVariables.length;
        }
        this.textVariables.put(key, convert(key, Arrays.asList(textVariables)));
    }

    private List<TextVariable> convert(String key, List<String> values) {
        List<TextVariable> textValues = Collections.newArrayList();
        for (String value : values) {
            textValues.add(new TextVariable(key, value));
        }
        return textValues;
    }

    public void addImageVariables(String key, List<ImageVariable> imageVariables) {
        if (numberOfRows != 0) {
            if (imageVariables.size() != numberOfRows) {
                throw new IncorrectNumberOfRowsException("Incorrect number of rows. Expected " + numberOfRows
                        + " but was " + imageVariables.size());
            }
        } else {
            numberOfRows = imageVariables.size();
        }
        this.imageVariables.put(key, imageVariables);
    }

    public void addImageVariables(String key, ImageVariable... imageVariables) {
        if (numberOfRows != 0) {
            if (imageVariables.length != numberOfRows) {
                throw new IncorrectNumberOfRowsException("Incorrect number of rows. Expected " + numberOfRows
                        + " but was " + imageVariables.length);
            }
        } else {
            numberOfRows = imageVariables.length;
        }
        this.imageVariables.put(key, Arrays.asList(imageVariables));
    }

    public void addBulletListVariables(String key, List<BulletListVariable> bulletListVariables) {
        if (numberOfRows != 0) {
            if (bulletListVariables.size() != numberOfRows) {
                throw new IncorrectNumberOfRowsException("Incorrect number of rows. Expected " + numberOfRows
                        + " but was " + bulletListVariables.size());
            }
        } else {
            numberOfRows = bulletListVariables.size();
        }
        this.bulletListVariables.put(key, bulletListVariables);
    }

    public void addBulletListVariables(String key, BulletListVariable... bulletListVariables) {
        if (numberOfRows != 0) {
            if (bulletListVariables.length != numberOfRows) {
                throw new IncorrectNumberOfRowsException("Incorrect number of rows. Expected " + numberOfRows
                        + " but was " + bulletListVariables.length);
            }
        } else {
            numberOfRows = bulletListVariables.length;
        }
        this.bulletListVariables.put(key, Arrays.asList(bulletListVariables));
    }

    public Map<String, List<TextVariable>> getTextVariables() {
        return textVariables;
    }

    public Map<String, List<ImageVariable>> getImageVariables() {
        return imageVariables;
    }

    public Map<String, List<BulletListVariable>> getBulletListVariables() {
        return bulletListVariables;
    }

    public Set<Key> getKeys() {
        Set<Key> keys = Collections.newHashSet();
        generateKeys(keys, textVariables.keySet(), VariableType.TEXT);
        generateKeys(keys, imageVariables.keySet(), VariableType.IMAGE);
        generateKeys(keys, bulletListVariables.keySet(), VariableType.BULLET_LIST);
        return keys;
    }

    private void generateKeys(Set<Key> keys, Set<String> keysValues, VariableType variableType) {
        for (String keyValue : keysValues) {
            keys.add(new Key(keyValue, variableType));
        }
    }

    public boolean containsKey(String key) {
        return textVariables.containsKey(key) || imageVariables.containsKey(key)
                || bulletListVariables.containsKey(key);
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public Variable getVariable(Key key, int index) {
        switch (key.getVariableType()) {
        case TEXT:
            return textVariables.get(key.getKey()).get(index);
        case IMAGE:
            return imageVariables.get(key.getKey()).get(index);
        case BULLET_LIST:
            return bulletListVariables.get(key.getKey()).get(index);
        }
        return null;
    }
}
