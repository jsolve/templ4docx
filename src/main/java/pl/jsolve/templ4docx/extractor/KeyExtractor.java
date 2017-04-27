package pl.jsolve.templ4docx.extractor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import pl.jsolve.templ4docx.util.Key;
import pl.jsolve.templ4docx.util.VariableType;
import pl.jsolve.templ4docx.variable.BulletListVariable;
import pl.jsolve.templ4docx.variable.ImageVariable;
import pl.jsolve.templ4docx.variable.ObjectVariable;
import pl.jsolve.templ4docx.variable.TableVariable;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * Utility class which returns list of keys for all variables added to Variables object
 * @author Lukasz Stypka
 */
public class KeyExtractor {

    /**
     * Utility method which returns list of keys for all variables added to Variables object
     * @param variables Variables - container class which contains all variables uses in docx template
     * @return list of keys for variables added to Variable object
     */
    public List<Key> extractKeys(Variables variables) {
        List<Key> keys = new ArrayList<Key>();
        for (Entry<String, TextVariable> entry : variables.getTextVariables().entrySet()) {
            keys.add(new Key(entry.getKey(), VariableType.TEXT));
        }

        for (Entry<String, ImageVariable> entry : variables.getImageVariables().entrySet()) {
            keys.add(new Key(entry.getKey(), VariableType.IMAGE));
        }

        for (TableVariable entry : variables.getTableVariables()) {
            for (Key key : entry.getKeys()) {
                Key tableKey = new Key(key.getKey(), VariableType.TABLE);
                tableKey.addSubKey(key);
                keys.add(tableKey);
            }
        }

        for (Entry<String, BulletListVariable> entry : variables.getBulletListVariables().entrySet()) {
            keys.add(new Key(entry.getKey(), VariableType.BULLET_LIST));
        }

        for (Entry<String, ObjectVariable> entry : variables.getObjectVariables().entrySet()) {
            keys.add(new Key(entry.getKey(), VariableType.OBJECT));
        }

        return keys;
    }

}
