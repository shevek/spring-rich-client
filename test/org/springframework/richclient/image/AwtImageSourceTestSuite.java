/*
 * The Spring Framework is published under the terms of the Apache Software
 * License.
 */
package org.springframework.richclient.image;

import java.awt.Image;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.richclient.image.AwtImageSource;

/**
 * Tests the image loading and caching library.
 * 
 * @author Keith Donald
 */
public class AwtImageSourceTestSuite extends TestCase {
    private ApplicationContext context;

    public void testValidImageAccess() {
        AwtImageSource source = (AwtImageSource)context.getBean("imageSource");
        Image image = source.getImage("test.image.key");
        assertNotNull(image);
    }

    public void testBrokenImageAccess() {
        AwtImageSource source = (AwtImageSource)context.getBean("imageSourceBroken");
        Image image = source.getImage("bogus.image.key");
        assertNotNull(image);
    }
    

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        context =
            new ClassPathXmlApplicationContext("org/springframework/rcp/image/application-context.xml");
    }
}
