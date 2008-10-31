/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.richclient.application.support;

import org.easymock.EasyMock;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * Test for <code>DefaultApplicationPageFactory</code>
 * 
 * @author Peter De Bruycker
 */
public class DefaultApplicationPageFactoryTests extends SpringRichTestCase {

	public void testCreate() {
		DefaultApplicationPageFactory factory = new DefaultApplicationPageFactory();
		
		ApplicationWindow window = (ApplicationWindow) EasyMock.createMock(ApplicationWindow.class);
		SingleViewPageDescriptor descriptor= new SingleViewPageDescriptor(new DefaultViewDescriptor());
		
		DefaultApplicationPage page = (DefaultApplicationPage) factory.createApplicationPage(window, descriptor);
		assertNotNull("page cannot be null", page);
		assertEquals(window, page.getWindow());
		assertEquals(descriptor, page.getPageDescriptor());
	}
	
}
