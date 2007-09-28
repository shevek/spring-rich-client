/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.richclient.command.config;

import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.JButton;

import org.springframework.richclient.command.AbstractCommand;

/**
 * Custom <code>CommandButtonConfigurer</code> for buttons on the toolbar.
 * <p>
 * The <code>showText</code> property determines whether text is shown.
 * Default value is <code>false</code>.
 * <p>
 * The <code>textBelowIcon</code> property indicates whether the text is shown
 * below the icon (as is default in most applications). The default value is
 * <code>true</code>.
 * 
 * @author Keith Donald
 * @author Peter De Bruycker
 */
public class ToolBarCommandButtonConfigurer extends DefaultCommandButtonConfigurer {
	private boolean showText = false;

	private boolean textBelowIcon = true;

	public void setTextBelowIcon(boolean textBelowIcon) {
		this.textBelowIcon = textBelowIcon;
	}

	public boolean isTextBelowIcon() {
		return textBelowIcon;
	}

	public void setShowText(boolean showText) {
		this.showText = showText;
	}

	public boolean isShowText() {
		return showText;
	}

	public void configure(AbstractButton button, AbstractCommand command, CommandFaceDescriptor faceDescriptor) {
		super.configure(button, command, faceDescriptor);

		if (textBelowIcon) {
			button.setHorizontalTextPosition(JButton.CENTER);
			button.setVerticalTextPosition(JButton.BOTTOM);
		}

		if (!showText) {
			if (button.getIcon() != null) {
				button.setText("");
			}
		}

		button.setMargin(new Insets(2, 5, 2, 5));
	}
}
