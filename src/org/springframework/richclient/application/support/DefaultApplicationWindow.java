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
package org.springframework.richclient.application.support;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.PageListener;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.WindowManager;
import org.springframework.richclient.application.config.ApplicationAdvisor;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.progress.StatusBarCommandGroup;
import org.springframework.richclient.util.ListenerListHelper;
import org.springframework.richclient.util.Memento;

/**
 * Provides a default implementation of {@link ApplicationWindow}
 */
public class DefaultApplicationWindow implements ApplicationWindow {
	protected Log logger = LogFactory.getLog(getClass());

	private static final String DEFAULT_APPLICATION_PAGE_BEAN_ID = "defaultApplicationPagePrototype";

	private int number;

	private ApplicationWindowCommandManager commandManager;

	private CommandGroup menuBarCommandGroup;

	private CommandGroup toolBarCommandGroup;

	private StatusBarCommandGroup statusBarCommandGroup;

	private ApplicationWindowConfigurer windowConfigurer;

	private JFrame control;

	private ApplicationPage currentPage;

	private WindowManager windowManager;

	private ListenerListHelper pageListeners = new ListenerListHelper(PageListener.class);

	public DefaultApplicationWindow() {
		this(Application.instance().getWindowManager().size());
	}

	public DefaultApplicationWindow(int number) {
		this.number = number;
		getAdvisor().onPreWindowOpen(getWindowConfigurer());
		this.commandManager = getAdvisor().createWindowCommandManager();
		this.menuBarCommandGroup = getAdvisor().getMenuBarCommandGroup();
		this.toolBarCommandGroup = getAdvisor().getToolBarCommandGroup();
		this.statusBarCommandGroup = getAdvisor().getStatusBarCommandGroup();
		getAdvisor().onCommandsCreated(this);
	}

	public int getNumber() {
		return number;
	}

	public ApplicationPage getPage() {
		return currentPage;
	}

	protected ApplicationAdvisor getAdvisor() {
		return Application.instance().getAdvisor();
	}

	protected ApplicationServices getServices() {
		return Application.services();
	}

	protected ApplicationWindowConfigurer getWindowConfigurer() {
		if (windowConfigurer == null) {
			this.windowConfigurer = initWindowConfigurer();
		}
		return windowConfigurer;
	}

	protected ApplicationWindowConfigurer initWindowConfigurer() {
		return new DefaultApplicationWindowConfigurer(this);
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public Iterator getSharedCommands() {
		return commandManager.getSharedCommands();
	}

	public CommandGroup getMenuBar() {
		return menuBarCommandGroup;
	}

	public CommandGroup getToolBar() {
		return toolBarCommandGroup;
	}

	public StatusBarCommandGroup getStatusBar() {
		return statusBarCommandGroup;
	}

	public void setWindowManager(WindowManager windowManager) {
		this.windowManager = windowManager;
	}

	public void showPage(String pageId) {
		if (this.currentPage == null) {
			this.currentPage = createPage(this, pageId);
			initWindow();
		}
		else {
			if (!currentPage.getId().equals(pageId)) {
				this.currentPage = createPage(this, pageId);
				updatePageControl();
			}
		}
	}

	protected final ApplicationPage createPage(ApplicationWindow window, String pageDescriptorId) {
		PageDescriptor descriptor = getPageDescriptor(pageDescriptorId);
		return createPage(descriptor);
	}

	/**
	 * Factory method for creating the page area managed by this window.
	 * Subclasses may override to return a custom page implementation.
	 * 
	 * @param descriptor
	 *            The page descriptor
	 * @return The window's page
	 */
	protected ApplicationPage createPage(PageDescriptor descriptor) {
		try {
			DefaultApplicationPage page = (DefaultApplicationPage)getServices().getBean(DEFAULT_APPLICATION_PAGE_BEAN_ID,
					DefaultApplicationPage.class);
			page.setApplicationWindow(this);
			page.setDescriptor(descriptor);
			return page;
		}
		catch (NoSuchBeanDefinitionException e) {
			return new DefaultApplicationPage(this, descriptor);
		}

	}

	protected PageDescriptor getPageDescriptor(String pageDescriptorId) {
		Object desc = getServices().getBean(pageDescriptorId);
		if (desc instanceof PageDescriptor) {
			return (PageDescriptor)desc;
		}
		else if (desc instanceof ViewDescriptor) {
			return new SingleViewPageDescriptor((ViewDescriptor)desc);
		}
		else {
			throw new IllegalArgumentException("Page id '" + pageDescriptorId
					+ "' is not backed by an ApplicationPageDescriptor");
		}
	}

	private void initWindow() {
		initWindowControl();
		getAdvisor().onWindowCreated(this);
		getAdvisor().showIntroComponentIfNecessary(this);
		this.control.setVisible(true);
		getAdvisor().onWindowOpened(this);
	}

	protected void initWindowControl() {
		this.control = createNewWindowControl();
		ApplicationWindowConfigurer configurer = getWindowConfigurer();
		control.setTitle(configurer.getTitle());
		control.setIconImage(configurer.getImage());
		control.setJMenuBar(menuBarCommandGroup.createMenuBar());
		menuBarCommandGroup.setVisible(configurer.getShowMenuBar());

		control.getContentPane().setLayout(new BorderLayout());
		control.getContentPane().add(createToolBar(), BorderLayout.NORTH);
		control.getContentPane().add(this.currentPage.getControl());
		control.getContentPane().add(createStatusBar(), BorderLayout.SOUTH);
		control.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		control.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		control.pack();
		control.setSize(configurer.getInitialSize());
		control.setLocationRelativeTo(null);
	}

	protected JFrame createNewWindowControl() {
		return new JFrame();
	}

	protected void updatePageControl() {
		control.getContentPane().remove(this.currentPage.getControl());
		control.getContentPane().add(this.currentPage.getControl());
		control.validate();
	}

	public JFrame getControl() {
		return control;
	}

	public boolean isControlCreated() {
		return control != null;
	}

	protected JComponent createToolBar() {
		JComponent toolBar = toolBarCommandGroup.createToolBar();
		toolBarCommandGroup.setVisible(getWindowConfigurer().getShowToolBar());
		return toolBar;
	}

	protected JComponent createStatusBar() {
		JComponent statusBar = statusBarCommandGroup.getControl();
		statusBarCommandGroup.setVisible(getWindowConfigurer().getShowStatusBar());
		return statusBar;
	}

	public void addPageListener(PageListener listener) {
		this.pageListeners.add(listener);
	}

	public void removePageListener(PageListener listener) {
		this.pageListeners.remove(PageListener.class);
	}

	public boolean close() {
		boolean canClose = getAdvisor().onPreWindowClose(this);
		if (canClose) {
			currentPage.close();
			control.dispose();
			control = null;
			if (windowManager != null) {
				windowManager.remove(this);
			}
			windowManager = null;
		}
		return canClose;
	}

	public void saveState(Memento memento) {
		Rectangle bounds = this.control.getBounds();
		memento.putInteger("x", bounds.x);
		memento.putInteger("y", bounds.y);
		memento.putInteger("width", bounds.width);
		memento.putInteger("height", bounds.height);
	}

}