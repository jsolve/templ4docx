package pl.jsolve.templ4docx.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class ReflectionHelper {

    /**
     * Set a {@code field} to a {@code value} of an entity {@code object}
     *
     * @param object
     * @param field
     * @param value
     */
    public static void setFieldValue(Object object, Field field, Object value) {
        try {
            boolean isAccessible = field.isAccessible();
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(isAccessible);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Trying set a {@code field} to a {@code value} of an entity {@code object} by using a setter method. If setter not
     * found, field will be set directly throw {@code setFieldValue} method.
     *
     * @param object
     * @param field
     * @param value
     */
    public static void setFieldValueBySetter(Object object, Field field, Object value) {
        try {
            boolean isSetted = false;
            Collection<Method> methods = getMethods(object);
            Iterator<Method> it = methods.iterator();
            String fieldName = field.getName();
            String methodName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            while (it.hasNext()) {
                Method m = it.next();
                if (!Modifier.isPublic(m.getModifiers()) || m.getParameterTypes().length != 1
                        || (value != null && !m.getParameterTypes()[0].isAssignableFrom(value.getClass()))
                        || !m.getName().equals(methodName)) {
                    it.remove();
                }
            }
            for (Method m : methods) {
                m.invoke(object, value);
                isSetted = true;
            }
            if (isSetted)
                return;
            setFieldValue(object, field, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param object
     * @param field
     * @return the field value of an object
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Object getFieldValue(Object object, Field field) {
        try {
            boolean isAccessible = field.isAccessible();
            field.setAccessible(true);
            Object value = field.get(object);
            field.setAccessible(isAccessible);
            return value;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param object
     * @param field
     * @return the field value of an object
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Object getFieldValue(Object object, String fieldName) {
        Collection<Field> fields = getFields(object);
        for (Field field : fields) {
            if (fieldName.equals(field.getName()))
                return getFieldValue(object, field);
        }
        return null;
    }

    /**
     * @param obj
     * @return all object fields, including inherited
     */
    public static Collection<Field> getFields(Object obj) {
        Class<?> clazz = obj.getClass();
        Collection<Field> fields = new ArrayList<Field>();
        fields.addAll(sortByName(Arrays.asList(clazz.getDeclaredFields())));
        while (clazz.getSuperclass() != null) {
            clazz = clazz.getSuperclass();
            fields.addAll(sortByName(Arrays.asList(clazz.getDeclaredFields())));
        }
        Iterator<Field> it = fields.iterator();
        while (it.hasNext()) {
            Field f = it.next();
            if (f.isSynthetic() || Modifier.isStatic(f.getModifiers()) || Modifier.isTransient(f.getModifiers())) {
                it.remove();
            }
        }
        return fields;
    }

    /**
     * @param obj
     * @return all object fields, including inherited
     */
    public static Collection<Field> getFieldsStatic(Object obj) {
        Class<?> clazz = obj.getClass();
        Collection<Field> fields = new ArrayList<Field>();
        fields.addAll(sortByName(Arrays.asList(clazz.getDeclaredFields())));
        while (clazz.getSuperclass() != null) {
            clazz = clazz.getSuperclass();
            fields.addAll(sortByName(Arrays.asList(clazz.getDeclaredFields())));
        }
        Iterator<Field> it = fields.iterator();
        while (it.hasNext()) {
            Field f = it.next();
            if (f.isSynthetic() || !Modifier.isStatic(f.getModifiers()) || Modifier.isTransient(f.getModifiers())) {
                it.remove();
            }
        }
        return fields;
    }

    private static List<Field> sortByName(List<Field> fields) {
        Collections.sort(fields, new Comparator<Field>() {

            @Override
            public int compare(Field o1, Field o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });
        return fields;
    }

    /**
     * @param obj
     * @return all object methods, including inherited
     */
    public static Collection<Method> getMethods(Object obj) {
        Class<?> clazz = obj.getClass();
        Collection<Method> methods = new ArrayList<Method>();
        methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        while (clazz.getSuperclass() != null) {
            clazz = clazz.getSuperclass();
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        }
        Iterator<Method> it = methods.iterator();
        while (it.hasNext()) {
            Method m = it.next();
            if (m.isSynthetic() || Modifier.isStatic(m.getModifiers()) || Modifier.isTransient(m.getModifiers())) {
                it.remove();
            }
        }
        return methods;
    }

    /**
     * @param obj
     * @return all object methods, including inherited
     */
    public static Collection<Method> getMethodsStatic(Object obj) {
        Class<?> clazz = obj.getClass();
        Collection<Method> methods = new ArrayList<Method>();
        methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        while (clazz.getSuperclass() != null) {
            clazz = clazz.getSuperclass();
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        }
        Iterator<Method> it = methods.iterator();
        while (it.hasNext()) {
            Method m = it.next();
            if (m.isSynthetic() || !Modifier.isStatic(m.getModifiers()) || Modifier.isTransient(m.getModifiers())) {
                it.remove();
            }
        }
        return methods;
    }
}
