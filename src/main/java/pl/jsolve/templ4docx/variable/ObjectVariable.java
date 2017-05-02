package pl.jsolve.templ4docx.variable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.utils.ReflectionHelper;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class ObjectVariable implements Variable {

    private final ObjectVariable parentObjectVariable;
    private final VariablePattern variablePattern;
    private final int maxNestedLevel;
    private final String key;
    private final Object value;

    private ObjectVariable(ObjectVariable parentObjectVariable, String key, Object value,
            VariablePattern variablePattern, int maxNestedLevel) {
        this.parentObjectVariable = parentObjectVariable;
        this.key = key;
        this.value = value;
        this.variablePattern = variablePattern;
        this.maxNestedLevel = maxNestedLevel;
    }

    public ObjectVariable(String key, Object value, VariablePattern variablePattern) {
        this(null, key, value, variablePattern, 0);
    }

    public ObjectVariable(String key, Object value, VariablePattern variablePattern, int maxNestedLevel) {
        this(null, key, value, variablePattern, maxNestedLevel);
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public String getStringValue() {
        if (value != null)
            return value.toString();
        return null;
    }

    public List<ObjectVariable> getFieldVariables() {
        List<ObjectVariable> fieldVariables = new ArrayList<ObjectVariable>();

        if (value == null)
            return fieldVariables;

        if (maxNestedLevel > 0) {
            int level = getLevel();
            if (level >= maxNestedLevel)
                return fieldVariables;
        }

        Class<? extends Object> clazz = value.getClass();
        if (clazz.isArray())
            return fieldVariables;
        if (clazz.isPrimitive())
            return fieldVariables;
        if (clazz.isSynthetic())
            return fieldVariables;
        if (clazz.isAnnotation())
            return fieldVariables;
        if (clazz.isAnonymousClass())
            return fieldVariables;
        if (ClassUtils.isPrimitiveOrWrapper(clazz))
            return fieldVariables;
        if (clazz.equals(String.class))
            return fieldVariables;

        Collection<Field> fields = ReflectionHelper.getFields(value);
        for (Field field : fields) {
            String fieldKey = calculateTreeKey(field);
            Object fieldValue = ReflectionHelper.getFieldValue(value, field);
            ObjectVariable fieldVariable = new ObjectVariable(this, fieldKey, fieldValue, variablePattern,
                    maxNestedLevel);
            fieldVariables.add(fieldVariable);
        }

        return fieldVariables;
    }

    public List<ObjectVariable> getFieldVariablesTree() {
        List<ObjectVariable> tree = new ArrayList<ObjectVariable>();

        List<ObjectVariable> fieldVariables = getFieldVariables();
        for (ObjectVariable var : fieldVariables) {
            tree.add(var);
            tree.addAll(var.getFieldVariablesTree());
        }

        return tree;
    }

    /**
     * @return List of path from root to current object
     */
    protected List<ObjectVariable> getPath() {
        List<ObjectVariable> path = new ArrayList<ObjectVariable>();
        path.add(this);

        ObjectVariable parent = this.parentObjectVariable;
        while (parent != null) {
            path.add(parent);
            parent = parent.parentObjectVariable;
        }

        Collections.reverse(path);
        return path;
    }

    protected int getLevel() {
        int level = 0;
        ObjectVariable parent = this.parentObjectVariable;
        while (parent != null) {
            level++;
            parent = parent.parentObjectVariable;
        }
        return level;
    }

    protected String calculateTreeKey(Field field) {
        StringBuilder sb = new StringBuilder();
        sb.append(variablePattern.getOriginalPrefix());
        sb.append(getKeyNameWithoutPattern());
        sb.append('.');
        sb.append(field.getName());
        sb.append(variablePattern.getOriginalSuffix());
        return sb.toString();
    }

    protected String getKeyNameWithoutPattern() {
        String keyName = key;
        String prefix = variablePattern.getOriginalPrefix();
        String suffix = variablePattern.getOriginalSuffix();
        if (key.startsWith(prefix) && key.endsWith(suffix)) {
            keyName = key.substring(prefix.length(), key.length() - suffix.length());
        }
        return keyName;
    }

    @Override
    public String toString() {
        return key + "=" + getStringValue();
    }

}
