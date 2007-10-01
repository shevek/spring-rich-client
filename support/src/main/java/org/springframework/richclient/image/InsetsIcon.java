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
package org.springframework.richclient.image;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;

/**
 * Code taken from
 * http://www.jroller.com/santhosh/entry/beautify_swing_applications_toolbar_with
 * 
 * @author Santhosh Kumar
 */
public class InsetsIcon implements Icon {
	private static final Insets DEFAULT_INSETS = new Insets(2, 2, 0, 0);

	private Icon icon;

	private Insets insets;

	public InsetsIcon(Icon icon) {
		this(icon, null);
	}

	public InsetsIcon(Icon icon, Insets insets) {
		this.icon = icon;
		this.insets = insets == null ? DEFAULT_INSETS : insets;
	}

	public int getIconHeight() {
		return icon.getIconHeight() + insets.top + insets.bottom;
	}

	public int getIconWidth() {
		return icon.getIconWidth() + insets.left + insets.right;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		icon.paintIcon(c, g, x + insets.left, y + insets.top);
	}
}
