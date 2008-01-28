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
package org.springframework.richclient.filechooser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

/**
 * Tests for DefaultFileFilter
 * 
 * @author Peter De Bruycker
 */
public class DefaultFileFilterTests extends TestCase {
    public void testSetExtensionListInDescription() {
        DefaultFileFilter filter = new DefaultFileFilter();
        filter.addExtension("jpg");
        filter.addExtension("gif");
        filter.addExtension("bmp");

        filter.setDescription("Image files");
        assertEquals("Image files (*.jpg, *.gif, *.bmp)", filter.getDescription());

        filter.setExtensionListInDescription(false);
        assertEquals("Image files", filter.getDescription());
    }

    public void testGetDescription() {
        DefaultFileFilter filter = new DefaultFileFilter();
        filter.addExtension("jpg");
        filter.addExtension("gif");
        filter.addExtension("bmp");

        assertEquals("(*.jpg, *.gif, *.bmp)", filter.getDescription());

        // no extensions
        filter = new DefaultFileFilter();
        filter.setDescription("test");
        assertEquals("test", filter.getDescription());
    }

    public void testExtensionsAreAddedLowerCase() {
        DefaultFileFilter filter = new DefaultFileFilter();
        filter.addExtension("BMP");
        filter.addExtension("Txt");

        assertEquals(Arrays.asList(new String[] { "bmp", "txt" }), filter.getExtensions());
    }

    public void testAcceptIsDoneLowerCase() {
        DefaultFileFilter filter = new DefaultFileFilter();
        filter.addExtension("BMP");

        File bmpFile = new File("test.bmp");
        assertTrue(filter.accept(bmpFile));
    }

    public void testAddExtension() {
        DefaultFileFilter filter = new DefaultFileFilter();

        filter.addExtension("jpg");
        assertTrue(filter.getExtensions().contains("jpg"));

        filter.addExtension("jpg");
        assertEquals(1, filter.getExtensions().size());

        filter.addExtension("*.gif");
        assertTrue(filter.getExtensions().contains("gif"));
        assertFalse(filter.getExtensions().contains("*.gif"));

        filter.addExtension(".bmp");
        assertTrue(filter.getExtensions().contains("bmp"));
        assertFalse(filter.getExtensions().contains(".bmp"));
    }

    public void testAccept() {
        DefaultFileFilter filter = new DefaultFileFilter();
        filter.addExtension("jpg");
        filter.addExtension("gif");
        filter.addExtension("bmp");

        assertTrue(filter.accept(new File("test.jpg")));
        assertTrue(filter.accept(new File("test.gif")));
        assertTrue(filter.accept(new File("test.bmp")));
        assertFalse(filter.accept(new File("test.txt")));
    }

    public void testAcceptDirectory() throws IOException {
        DefaultFileFilter filter = new DefaultFileFilter();

        assertTrue("directories are always accepted", filter
                .accept(File.createTempFile("test", "test").getParentFile()));
    }
}
