/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.enums.CodedEnum;
import org.springframework.util.ArrayUtils;

/**
 * Misc static utility functions for java classes.
 * 
 * @author Kerth Donald
 */
public class ClassUtils {
    private static final Log logger = LogFactory.getLog(ClassUtils.class);
    private static Set simpleClasses = new HashSet();
    static {
        simpleClasses.add(String.class);
        simpleClasses.add(Integer.class);
        simpleClasses.add(Float.class);
        simpleClasses.add(Double.class);
        simpleClasses.add(Long.class);
        simpleClasses.add(Short.class);
        simpleClasses.add(Byte.class);
        simpleClasses.add(BigInteger.class);
        simpleClasses.add(Date.class);
        simpleClasses.add(Class.class);
        simpleClasses.add(Boolean.class);
        simpleClasses.add(Timestamp.class);
        simpleClasses.add(Calendar.class);
        simpleClasses.add(URL.class);
        simpleClasses.add(InetAddress.class);
    }

    private ClassUtils() {
    }

    /**
     * Intializes the specified class if not initialized already.
     * 
     * This is required for EnumUtils if the enum class has not yet been
     * loaded.
     * 
     * @param enumClass
     */
    public static void initializeClass(Class clazz) {
        try {
            Class.forName(clazz.getName(), true, Thread.currentThread()
                    .getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the qualified class field name with the specified value. For
     * example, with a class defined with a static field "NORMAL" with value =
     * "0", passing in "0" would return: className.NORMAL.
     * 
     * @param clazz
     * @param value
     * @return The qualified field.
     */
    public static String getClassFieldNameWithValue(Class clazz, Object value) {
        Field[] fields = clazz.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            try {
                Object constant = field.get(null);
                if (value.equals(constant)) {
                    return clazz.getName() + "." + field.getName();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Gets the field value for the specified qualified field name.
     * 
     * @param name
     * @return
     */
    public static Object getFieldValue(String qualifiedFieldName) {
        Class clazz;
        try {
            clazz = classForName(ClassUtils.qualifier(qualifiedFieldName));
        } catch (ClassNotFoundException cnfe) {
            return null;
        }
        try {
            return clazz.getField(ClassUtils.unqualify(qualifiedFieldName))
                    .get(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Load the class with the specified name.
     * 
     * @param name
     * @return The loaded class.
     * @throws ClassNotFoundException
     */
    public static Class classForName(String name) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(
                    name);
        } catch (Exception e) {
            return Class.forName(name);
        }
    }

    public static Method findMethod(String methodName, Class clazz,
            Class[] parmTypes) {
        try {
            return clazz.getMethod(methodName, parmTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static String unqualify(String qualifiedName) {
        return ClassUtils.unqualify(qualifiedName, '.');
    }

    /**
     * Returns the unqualified class name of the specified class.
     * 
     * @param string
     *            The class
     * @return The unqualified, short name.
     */
    public static String unqualify(Class clazz) {
        return unqualify(clazz.getName());
    }

    public static String unqualify(String qualifiedName, char separator) {
        return qualifiedName
                .substring(qualifiedName.lastIndexOf(separator) + 1);
    }

    /**
     * Returns the qualifier for a name separated by dots. The qualified part
     * is everything up to the last dot separator.
     * 
     * @param qualifiedName
     *            The qualified name.
     * @return The qualifier portion.
     */
    public static String qualifier(String qualifiedName) {
        int loc = qualifiedName.lastIndexOf('.');
        if (loc < 0) {
            return "";
        } else {
            return qualifiedName.substring(0, loc);
        }
    }

    /**
     * Check if the given class represents a primitive wrapper array.
     */
    public static boolean isPrimitiveWrapperArray(Class clazz) {
        return org.springframework.beans.BeanUtils
                .isPrimitiveWrapperArray(clazz);
    }

    /**
     * Check if the given class represents a primitive array.
     */
    public static boolean isPrimitiveArray(Class clazz) {
        return org.springframework.beans.BeanUtils.isPrimitiveArray(clazz);
    }

    /**
     * Does the provided bean class represent a simple scalar property? A
     * simple scalar property is considered a value property; that is, it is
     * not another bean. Examples include primitives, primitive wrappers,
     * Enums, and Strings.
     * 
     * @param clazz
     * @return true or false
     */
    public static boolean isSimpleScalar(Class clazz) {
        return clazz.isPrimitive() || simpleClasses.contains(clazz)
                || CodedEnum.class.isAssignableFrom(clazz);
    }

    public static Method getStaticMethod(String name, Class locatorClass,
            Class[] args) {
        try {
            logger.debug("Attempting to get method '" + name + "' on class "
                    + locatorClass + " with arguments '"
                    + ArrayUtils.toString(args) + "'");
            Method method = locatorClass.getDeclaredMethod(name, args);
            if ((method.getModifiers() & Modifier.STATIC) != 0) {
                return method;
            } else {
                return null;
            }
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

}
