package pl.jsolve.templ4docx.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import pl.jsolve.templ4docx.util.Key;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class KeysHolder {

    List<Key> keys = new ArrayList<Key>();
    Map<String, Key> keysByName = new HashMap<String, Key>();
    Map<String, Key> keysByMD5Name = new HashMap<String, Key>();
    Map<String, String> keysNamesByMD5Name = new HashMap<String, String>();

    public KeysHolder(List<Key> keys) {
        this.keys = Collections.unmodifiableList(keys);
        for (Key key : keys) {
            String name = key.getKey();
            String md5Name = DigestUtils.md5Hex(name).toUpperCase();

            this.keysByName.put(name, key);
            this.keysByMD5Name.put(md5Name, key);
            this.keysNamesByMD5Name.put(md5Name, name);
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

    public boolean containsKeyByMD5Name(String md5Name) {
        return keysByMD5Name.containsKey(md5Name);
    }

    public Key getKeyByMD5Name(String md5Name) {
        return keysByMD5Name.get(md5Name);
    }

    public boolean containsKeyNameByMD5Name(String md5Name) {
        return keysNamesByMD5Name.containsKey(md5Name);
    }

    public String getKeyNameByMD5Name(String md5Name) {
        return keysNamesByMD5Name.get(md5Name);
    }

}
