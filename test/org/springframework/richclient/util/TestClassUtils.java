package org.springframework.richclient.util;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Test case for {@link ClassUtils}
 */
public class TestClassUtils extends TestCase {

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
    }


    public void testGetPropertyClass() throws Exception {
        assertEquals(String.class,
            ClassUtils.getPropertyClass(B.class, "something"));
    }


    interface A {
        public void setSomething(String newSomething);
    }

    interface B {
        public String getSomething();
    }

    interface C extends A {
    }

    static class D implements B, C {
        private String something;


        public String getSomething() {
            return something;
        }


        public void setSomething(String something) {
            this.something = something;
        }
    }

    static class E extends D {
    }

}
