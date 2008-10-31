/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.binding.form.support;

import java.util.Arrays;

import junit.framework.TestCase;

import org.springframework.util.ObjectUtils;

/**
 * @author Mathias Broekelmann
 * 
 */
public class DefaultMessageCodeStrategyTests extends TestCase {

    private DefaultMessageCodeStrategy strategy;

    protected void setUp() throws Exception {
        strategy = new DefaultMessageCodeStrategy();
    }

    protected void tearDown() throws Exception {
        strategy = null;
    }

    public final void testGetMessageCodesEmptyArgs() {
        String[] emptyValues = new String[0];
        assertEquals(emptyValues, strategy.getMessageCodes(null, null, null));
        assertEquals(emptyValues, strategy.getMessageCodes(null, "", null));
        assertEquals(emptyValues, strategy.getMessageCodes("", "", null));
        assertEquals(emptyValues, strategy.getMessageCodes("", "", emptyValues));
        assertEquals(emptyValues, strategy.getMessageCodes("", "", new String[] { "" }));
        assertEquals(emptyValues, strategy.getMessageCodes("", "", new String[] { "", "" }));
    }

    public final void testGetMessageCodesNullContextNullSuffixes() {
        assertEquals(new String[] { "simpleField" }, strategy.getMessageCodes(null, "simpleField", null));
        assertEquals(new String[] { "fieldbase.simpleField", "simpleField" }, strategy.getMessageCodes(null,
                "fieldbase.simpleField", null));
    }

    public final void testGetMessageCodesWithContext() {
        assertEquals(new String[] { "context.fieldbase.simpleField", "context.simpleField", "fieldbase.simpleField",
                "simpleField" }, strategy.getMessageCodes("context", "fieldbase.simpleField", null));
    }

    public final void testGetMessageCodesWithSuffix() {
        assertEquals(new String[] { "simpleField.suffix" }, strategy.getMessageCodes(null, "simpleField",
                new String[] { "suffix" }));
        assertEquals(new String[] { "simpleField.suffix1", "simpleField" }, strategy.getMessageCodes(null,
                "simpleField", new String[] { "suffix1", "" }));
        assertEquals(new String[] { "simpleField.suffix1", "simpleField.suffix2" }, strategy.getMessageCodes(null,
                "simpleField", new String[] { "suffix1", "suffix2" }));
    }

    public final void testGetMessageCodesWithContextAndSuffix() {
        assertEquals(new String[] { "context.fieldbase.simpleField.suffix", "context.simpleField.suffix",
                "fieldbase.simpleField.suffix", "simpleField.suffix" }, strategy.getMessageCodes("context",
                "fieldbase.simpleField", new String[] { "suffix" }));
    }

    protected void assertEquals(Object[] expected, Object[] actual) {
        if (!Arrays.equals(expected, actual)) {
            fail(buildMessage(expected, actual));
        }
    }

    private String buildMessage(Object[] expected, Object[] actual) {
        return "expected " + ObjectUtils.nullSafeToString(expected) + ", got " + ObjectUtils.nullSafeToString(actual);
    }

    protected void assertEquals(String message, Object[] expected, Object[] actual) {
        if (!Arrays.equals(expected, actual)) {
            fail(message);
        }
    }
}
