/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.image;

import java.awt.Image;
import java.io.IOException;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

/**
 * Tests the image loading and caching library.
 * 
 * @author Keith Donald
 */
public class ImageSourceTests extends TestCase {
    private ApplicationContext context;

    public void testValidImageAccess() throws IOException {
        ImageSource source = (ImageSource)context.getBean("imageSource");
        Resource resource = source.getImageResource("test.image.key");
        assertNotNull(resource);
        String urlExternalForm = resource.getURL().toExternalForm();
        assertTrue(urlExternalForm.endsWith("org/springframework/richclient/image/test.gif"));
        Image image = source.getImage("test.image.key");
        assertNotNull(image);
    }

    public void testBrokenImageAccess() throws IOException {
        ImageSource source = (ImageSource)context.getBean("imageSourceBroken");
        Resource resource = source.getImageResource("bogus.image.key");
        assertNotNull(resource);
        String urlExternalForm = resource.getURL().toExternalForm();
        assertTrue(urlExternalForm.endsWith("org/springframework/richclient/image/broken.gif"));
        Image image = source.getImage("bogus.image.key");
        assertNotNull(image);
    }

    public void testInvalidImageAccess() {
        ImageSource source = (ImageSource)context.getBean("imageSourceBroken");
        try {
            source.getImageResource("invalid.image.key");
        }
        catch (NoSuchImageResourceException e) {
            // expected
        }
        try {
            source.getImage("invalid.image.key");
        }
        catch (NoSuchImageResourceException e) {
            // expected
        }
    }

    protected void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("org/springframework/richclient/image/application-context.xml");
    }
}