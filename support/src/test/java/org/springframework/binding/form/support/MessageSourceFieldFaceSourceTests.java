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
package org.springframework.binding.form.support;

import java.awt.Color;
import java.util.Locale;

import javax.swing.Icon;

import org.easymock.EasyMock;
import org.springframework.binding.form.FieldFace;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.test.SpringRichTestCase;
import org.springframework.richclient.test.TestIcon;

/**
 * Testcase for MessageSourceFieldFaceSource
 * 
 * @author Peter De Bruycker
 */
public class MessageSourceFieldFaceSourceTests extends SpringRichTestCase {

	public void testLoadFieldFace() {
		Icon testIcon = new TestIcon(Color.RED);

		MessageSourceFieldFaceSource fieldFaceSource = new MessageSourceFieldFaceSource();

		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("context.field.caption", Locale.getDefault(), "the caption");
		messageSource.addMessage("context.field.description", Locale.getDefault(), "the description");
		messageSource.addMessage("context.field.label", Locale.getDefault(), "the label");
		messageSource.addMessage("context.field.icon", Locale.getDefault(), "iconName");
		fieldFaceSource.setMessageSourceAccessor(new MessageSourceAccessor(messageSource));

		IconSource mockIconSource = (IconSource) EasyMock.createMock(IconSource.class);
		EasyMock.expect(mockIconSource.getIcon("iconName")).andReturn(testIcon);
		EasyMock.replay(mockIconSource);

		fieldFaceSource.setIconSource(mockIconSource);

		FieldFace face = fieldFaceSource.loadFieldFace("field", "context");

		assertEquals("the caption", face.getCaption());
		assertEquals("the label", face.getDisplayName());
		assertEquals("the description", face.getDescription());

		assertEquals(testIcon, face.getIcon());

		EasyMock.verify(mockIconSource);
	}

}
