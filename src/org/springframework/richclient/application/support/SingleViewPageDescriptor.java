/*
 * $Header: /usr/local/cvs/module/src/java/File.java,v 1.7 2004/01/16 22:23:11
 * keith Exp $ $Revision$ $Date$
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.application.support;

import java.awt.Image;

import javax.swing.Icon;

import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageLayoutBuilder;
import org.springframework.richclient.application.ViewDescriptor;

public class SingleViewPageDescriptor implements PageDescriptor {

	private ViewDescriptor viewDescriptor;

	public SingleViewPageDescriptor(ViewDescriptor viewDescriptor) {
		super();
		this.viewDescriptor = viewDescriptor;
	}

	public String getId() {
		return viewDescriptor.getId();
	}

	public String getDisplayName() {
		return viewDescriptor.getDisplayName();
	}

	public String getCaption() {
		return viewDescriptor.getCaption();
	}

	public String getDescription() {
		return viewDescriptor.getDescription();
	}

	public Icon getIcon() {
		return viewDescriptor.getIcon();
	}

	public Image getImage() {
		return viewDescriptor.getImage();
	}

	public void buildInitialLayout(PageLayoutBuilder layout) {
		layout.addView(viewDescriptor.getId());
	}

}