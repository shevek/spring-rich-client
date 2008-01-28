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
package org.springframework.richclient.application;

import java.beans.PropertyEditor;

import junit.framework.TestCase;

import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.richclient.application.support.DefaultPropertyEditorRegistry;

/**
 * Test cases for {@link DefaultPropertyEditorRegistry}
 */
public class DefaultPropertyEditorRegistryTests extends TestCase {

    public void testRegisteringClass() throws Exception {
        PropertyEditor pe;

        DefaultPropertyEditorRegistry registry = new DefaultPropertyEditorRegistry();

        registry.setPropertyEditor(D.class, ClassEditor.class);
        pe = registry.getPropertyEditor(E.class);
        assertNotNull(pe);
        assertEquals(ClassEditor.class, pe.getClass());

        registry.setPropertyEditor(A.class, ClassEditor.class);
        pe = registry.getPropertyEditor(B.class);
        assertNull(pe);

        pe = registry.getPropertyEditor(E.class);
        assertNotNull(pe);
        assertEquals(ClassEditor.class, pe.getClass());

        pe = registry.getPropertyEditor(C.class);
        assertNotNull(pe);
        assertEquals(ClassEditor.class, pe.getClass());
    }

    public void testRegisteringProperty() throws Exception {
        PropertyEditor pe;

        DefaultPropertyEditorRegistry registry = new DefaultPropertyEditorRegistry();

        registry.setPropertyEditor(A.class, "something", ClassEditor.class);

        try {
            registry.getPropertyEditor(B.class, "something");
            fail("Should have thrown an IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
            // should have been thrown
        }

        pe = registry.getPropertyEditor(E.class, "something");
        assertNotNull(pe);
        assertEquals(ClassEditor.class, pe.getClass());

        pe = registry.getPropertyEditor(C.class, "something");
        assertNotNull(pe);
        assertEquals(ClassEditor.class, pe.getClass());

        pe = registry.getPropertyEditor(B.class, "bar");
        assertNotNull(pe);

        registry.setPropertyEditor(Long.class, ClassEditor.class);
        pe = registry.getPropertyEditor(B.class, "bar");
        assertNotNull(pe);
        assertEquals(ClassEditor.class, pe.getClass());
    }

    interface A {
        public String getSomething();

        public void setSomething(String newSomething);
    }

    interface B {
        public Long getBar();

        public void setBar(Long newBar);
    }

    interface C extends A {
    }

    static class D implements B, C {
        private String something;

        private Long bar;

        public String getSomething() {
            return something;
        }

        public void setSomething(String something) {
            this.something = something;
        }

        public Long getBar() {
            return bar;
        }

        public void setBar(Long bar) {
            this.bar = bar;
        }
    }

    static class E extends D {

    }
}