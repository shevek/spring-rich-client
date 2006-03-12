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
package org.springframework.richclient.settings.support;

import junit.framework.TestCase;

/**
 * @author Peter De Bruycker
 */
public class ArrayUtilTests extends TestCase {

    public void testToIntArray() {
        String[] stringArray = { "2", "3", "5", "0" };
        int[] result = ArrayUtil.toIntArray(stringArray);

        assertNotNull(result);
        assertEquals(stringArray.length, result.length);
        int expected[] = { 2, 3, 5, 0 };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result[i]);
        }
    }

    public void testToIntArrayInvalidArguments() {
        String[] stringArray = { "2", "3", "notAnInt", "0" };
        try {
            ArrayUtil.toIntArray(stringArray);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            pass();
        }
    }

    private static void pass() {
        // test passes
    }

}