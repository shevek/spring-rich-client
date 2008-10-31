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
package org.springframework.binding.value.support;

import org.springframework.binding.value.ValueModel;
import org.springframework.rules.closure.Closure;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * Tests class {@link TypeConverter}.
 * 
 * @author Oliver Hutchison
 */
public class TypeConverterTests extends SpringRichTestCase {

    private ValueModel vm = new ValueHolder("whatever!");

    private TypeConverter tc = new TypeConverter(vm, new UpperConverter(), new LowerConverter());

    public void testConvertsTo() {
        vm.setValue("test");
        assertEquals("TEST", tc.getValue());
        assertEquals("test", vm.getValue());
    }

    public void testConvertsFrom() {
        tc.setValue("TEST");
        assertEquals("TEST", tc.getValue());
        assertEquals("test", vm.getValue());
    }

    public void testDoesNotSetWrappedValueWhenConvertedValueHasNotChanged() {
        vm.setValue("tEsT");
        tc.setValue("TEST");
        assertEquals("TEST", tc.getValue());
        assertEquals("tEsT", vm.getValue());
    }

    private static class UpperConverter implements Closure {

        public Object call(Object argument) {
            return ((String)argument).toUpperCase();
        }

    }

    private static class LowerConverter implements Closure {

        public Object call(Object argument) {
            return ((String)argument).toLowerCase();
        }

    }
}
