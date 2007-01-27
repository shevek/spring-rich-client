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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Tests the "image:" URL protocol handler.
 * 
 * @author oliverh
 */
public class HandlerTestsIgnored extends TestCase {

    private static boolean imageHasNotBeenInstalledInThisJVM = true;

    /**
     * NOTE: This must be one big test method because of the static dependency
     * introduced by the strange way Java requires custom URL handlers to be
     * registered.
     */
    public void testHandler() throws IOException {
        assertTrue("This test can only be run once in a single JVM", imageHasNotBeenInstalledInThisJVM);

        URL url;

//        // make sure a handler is not installed
//        try {
//            url = new URL("image:test");
//            fail("image protocol is already installed. Do any of the other tests instantiate DefaultImageSource?");
//        }
//        catch (MalformedURLException e) {
//            // expected
//        }

        // test install
        ImageSource urlHandlerImageSource = (ImageSource) new ClassPathXmlApplicationContext(
                "org/springframework/richclient/image/application-context.xml").getBean("imageSource");
        // Handler.installImageUrlHandler(urlHandlerImageSource); is not needed because imageSource calls it itself
        try {
            url = new URL("image:test");
            imageHasNotBeenInstalledInThisJVM = false;
        }
        catch (MalformedURLException e) {
            fail("protocol was not installed");
        }

        // test invalid key
        url = new URL("image:image.that.does.not.exist");
        try {
            url.openConnection();
            fail();
        }
        catch (NoSuchImageResourceException e) {
            // expected
        }

        // test valid key
        url = new URL("image:test.image.key");
        url.openConnection();
    }

}