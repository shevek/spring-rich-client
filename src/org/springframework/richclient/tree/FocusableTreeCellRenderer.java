/*
 * $Header: /usr/local/cvs/java-tools/eclipse/code-templates.xml,v 1.1
 * 2003/09/12 18:17:04 keith Exp $ $Revision$ $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2003. All rights reserved.
 */
package org.springframework.richclient.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;

import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * This renderer differs from the default Swing list renderer in that it
 * displays the currently selected item differently when the list has the focus.
 * This is more like Windows behavior. I think that this behavior makes it
 * easier for the user to track the focus when the focus changes.
 * 
 * This class should be subclasses and the setText and setIcon methods should be
 * called before calling getTreeCellRendererComponent
 *  
 */
public class FocusableTreeCellRenderer extends DefaultTreeCellRenderer {
	protected static LineBorder windowsListBorder = new LineBorder(SystemColor.controlDkShadow);

	public FocusableTreeCellRenderer() {
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		Color colorForeground;
		Color colorBackground;
		if (sel) {
			if (tree.hasFocus()) {
				colorBackground = SystemColor.textHighlight;
				colorForeground = SystemColor.textHighlightText;
			}
			else {
				colorForeground = SystemColor.controlText;
				colorBackground = SystemColor.control;
			}
			setBackgroundSelectionColor(colorBackground);
		}
		else {
			colorForeground = tree.getForeground();
			colorBackground = tree.getBackground();
			setBackgroundNonSelectionColor(colorBackground);
		}
		setForeground(colorForeground);

		return this;
	}
}