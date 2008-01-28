package org.springframework.richclient.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Test case for {@link ClassUtils}
 */
public class ClassUtilsTests extends TestCase {

    interface A {

        public void setSomething(String newSomething);
    }

    interface B {

        public String getSomething();
    }

    interface C extends A {
    }

    static class D implements B, C {

        public static final String VALUE1 = "value1";

        public static final Integer VALUE2 = new Integer(5);

        private String something;

        public String getSomething() {
            return something;
        }

        public void setSomething(String something) {
            this.something = something;
        }

        public static void staticMethod() {
        }
    }

    static class E extends D {

        private D deProperty;

        public D getDeProperty() {
            return deProperty;
        }

        public void setDeProperty(D deProperty) {
            this.deProperty = deProperty;
        }
    }

    public void testGetFieldValue() {
        assertNull(ClassUtils.getFieldValue("no.such.class.Exists.someField"));

        assertEquals("value1", ClassUtils.getFieldValue("org.springframework.richclient.util.ClassUtilsTests$D.VALUE1"));
    }

    static class F {

        private E eeProperty;

        public E getEeProperty() {
            return eeProperty;
        }

        public void setEeProperty(E eeProperty) {
            this.eeProperty = eeProperty;
        }
    }

    public void testGetPropertyClass() throws Exception {
        assertEquals(String.class, ClassUtils.getPropertyClass(B.class, "something"));

        assertEquals(D.class, ClassUtils.getPropertyClass(F.class, "eeProperty.deProperty"));
    }

    public void testGetValueFromMapForClass() throws Exception {
        Object val;
        Map map = new HashMap();

        map.put(Number.class, "Number");

        val = ClassUtils.getValueFromMapForClass(Long.class, map);
        assertNotNull(val);
        assertEquals("Number", val);

        map.put(A.class, "A");
        val = ClassUtils.getValueFromMapForClass(B.class, map);
        assertNull(val);

        val = ClassUtils.getValueFromMapForClass(E.class, map);
        assertNotNull(val);
        assertEquals("A", val);

        val = ClassUtils.getValueFromMapForClass(C.class, map);
        assertNotNull(val);
        assertEquals("A", val);
    }

    public void testIsAProperty() throws Exception {
        assertTrue(ClassUtils.isAProperty(B.class, "something"));
        assertTrue(ClassUtils.isAProperty(E.class, "something"));
        assertTrue(ClassUtils.isAProperty(A.class, "something"));
        assertTrue(ClassUtils.isAProperty(F.class, "eeProperty.deProperty"));
    }

    public void testQualifier() {
        assertEquals("org.springframework.richclient.util.ClassUtilsTests", ClassUtils
                .qualifier("org.springframework.richclient.util.ClassUtilsTests.property"));
        assertEquals("java.lang", ClassUtils.qualifier("java.lang.String"));
        assertEquals("", ClassUtils.qualifier(""));
        assertEquals("", ClassUtils.qualifier("test"));
    }

    public void testFindMethod() {
        Method method = ClassUtils.findMethod("getSomething", B.class, null);
        assertNotNull(method);

        assertNull(ClassUtils.findMethod("noSuchMethod", B.class, null));
    }

    public void testGetStaticMethod() {
        assertNull(ClassUtils.getStaticMethod("getSomething", D.class, null));

        assertNotNull(ClassUtils.getStaticMethod("staticMethod", D.class, null));
        assertEquals("staticMethod", ClassUtils.getStaticMethod("staticMethod", D.class, null).getName());
    }

    public void testGetClassFieldNameWithValue() {
        assertEquals("org.springframework.richclient.util.ClassUtilsTests$D.VALUE1", ClassUtils
                .getClassFieldNameWithValue(D.class, "value1"));
        assertEquals("org.springframework.richclient.util.ClassUtilsTests$D.VALUE2", ClassUtils
                .getClassFieldNameWithValue(D.class, new Integer(5)));

        assertNull(ClassUtils.getClassFieldNameWithValue(D.class, "noSuchFieldWithValue"));
    }

    public void testUnqualify() {
        assertEquals("ClassUtilsTests", ClassUtils.unqualify("org.springframework.richclient.util.ClassUtilsTests"));
        assertEquals("ClassUtilsTests", ClassUtils.unqualify(ClassUtilsTests.class));
        assertEquals("ClassUtilsTests", ClassUtils
                .unqualify("org/springframework/richclient/util/ClassUtilsTests", '/'));
    }

}