package pl.jsolve.templ4docx.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.jsolve.templ4docx.util.Key;

/**
 * @author indvd00m
 *
 */
public class KeysHolder {

    List<Key> keys = new ArrayList<Key>();
    Map<String, Key> keysByName = new HashMap<String, Key>();

    public KeysHolder(List<Key> keys) {
        this.keys = Collections.unmodifiableList(keys);
        for (Key key : keys) {
            String name = key.getKey();
            this.keysByName.put(name, key);
        }
    }

    public List<Key> getKeys() {
        return keys;
    }

    public boolean containsKeyByName(String name) {
        return keysByName.containsKey(name);
    }

    public Key getKeyByName(String name) {
        return keysByName.get(name);
    }

}
