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

    interface A {
    }

    interface B {
    }

    interface C extends A {
    }

    static class D implements B, C {
    }

    static class E extends D {
    }

}
