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
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
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
import org.springframework.richclient.application.config.ApplicationLifecycleAdvisor;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.progress.StatusBarCommandGroup;
import org.springframework.richclient.util.EventListenerListHelper;
import org.springframework.util.Assert;

/**
 * Provides a default implementation of {@link ApplicationWindow}
 */
public class DefaultApplicationWindow implements ApplicationWindow {
    protected Log logger = LogFactory.getLog(getClass());

    private static final String DEFAULT_APPLICATION_PAGE_BEAN_ID = "defaultApplicationPagePrototype";

    private final EventListenerListHelper pageListeners = new EventListenerListHelper(PageListener.class);

    private int number;

    private ApplicationWindowCommandManager commandManager;

    private CommandGroup menuBarCommandGroup;

    private CommandGroup toolBarCommandGroup;

    private StatusBarCommandGroup statusBarCommandGroup;

    private ApplicationWindowConfigurer windowConfigurer;

    private JFrame control;

    private ApplicationPage currentPage;

    private WindowManager windowManager;

    public DefaultApplicationWindow() {
        this(Application.instance().getWindowManager().size());
    }

    public DefaultApplicationWindow(int number) {
        this.number = number;
        getAdvisor().setOpeningWindow(this);
        getAdvisor().onPreWindowOpen(getWindowConfigurer());
        init();
        getAdvisor().onCommandsCreated(this);
    }

    protected void init() {
        this.commandManager = getAdvisor().createWindowCommandManager();
        this.menuBarCommandGroup = getAdvisor().getMenuBarCommandGroup();
        this.toolBarCommandGroup = getAdvisor().getToolBarCommandGroup();
        this.statusBarCommandGroup = getAdvisor().getStatusBarCommandGroup();
    }

    public int getNumber() {
        return number;
    }

    public ApplicationPage getPage() {
        return currentPage;
    }

    protected ApplicationLifecycleAdvisor getAdvisor() {
        return Application.instance().getLifecycleAdvisor();
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

    /**
     * Show the given page in this window.
     *
     * @param pageId the page to show, identified by id
     *
     * @throws IllegalArgumentException if pageId == null
     */
    public void showPage(String pageId) {
        if (pageId == null) throw new IllegalArgumentException("pageId == null");

        if (getPage() == null || !getPage().getId().equals(pageId)) {
            showPage(createPage(this, pageId));
        }
        else {
            // asking for the same page, so ignore
        }
    }

    /**
     * Show the given page in this window.
     *
     * @param page the page to show
     *
     * @throws IllegalArgumentException if page == null
     */
    protected void showPage(ApplicationPage page) {
        if (page == null) throw new IllegalArgumentException("page == null");

        if (this.currentPage == null) {
            this.currentPage = page;
            initWindow();
        }
        else {
            if (!currentPage.getId().equals(page.getId())) {
                final ApplicationPage oldPage = this.currentPage;
                this.currentPage = page;
                updatePageControl(oldPage);
                pageListeners.fire("pageClosed", oldPage);
            }
            else {
                // asking for the same page, so ignore
            }
        }
        pageListeners.fire("pageOpened", this.currentPage);
    }

    protected final ApplicationPage createPage(ApplicationWindow window, String pageDescriptorId) {
        PageDescriptor descriptor = getPageDescriptor(pageDescriptorId);
        return createPage(descriptor);
    }

    /**
     * Factory method for creating the page area managed by this window.
     * Subclasses may override to return a custom page implementation.
     *
     * @param descriptor The page descriptor
     *
     * @return The window's page
     */
    protected ApplicationPage createPage(PageDescriptor descriptor) {
        try {
            ApplicationPage page = (ApplicationPage)getServices().getBean(
                DEFAULT_APPLICATION_PAGE_BEAN_ID, ApplicationPage.class);
            page.setApplicationWindow(this);
            page.setDescriptor(descriptor);
            return page;
        }
        catch (NoSuchBeanDefinitionException e) {
            return new DefaultApplicationPage(this, descriptor);
        }
    }

    protected PageDescriptor getPageDescriptor(String pageDescriptorId) {
        Assert.state(getServices().containsBean(pageDescriptorId),
            "Do not know about page or view descriptor with name '" + pageDescriptorId
                + "' - check your context config");
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
        this.control = createNewWindowControl();
        initWindowControl(this.control);
        getAdvisor().onWindowCreated(this);
        getAdvisor().showIntroComponentIfNecessary(this);
        this.control.setVisible(true);
        getAdvisor().onWindowOpened(this);
    }

    protected void initWindowControl(JFrame windowControl) {
        ApplicationWindowConfigurer configurer = getWindowConfigurer();
        applyStandardLayout(windowControl, configurer);
        applyCustomLayout(windowControl, configurer);
        prepareWindowForView(windowControl, configurer);
    }

    protected void applyStandardLayout(JFrame windowControl, ApplicationWindowConfigurer configurer) {
        windowControl.setTitle(configurer.getTitle());
        windowControl.setIconImage(configurer.getImage());
        windowControl.setJMenuBar(createMenuBarControl());
        windowControl.getContentPane().setLayout(new BorderLayout());
        windowControl.getContentPane().add(createToolBarControl(), BorderLayout.NORTH);
        windowControl.getContentPane().add(createStatusBarControl(), BorderLayout.SOUTH);
    }

    protected void applyCustomLayout(JFrame windowControl, ApplicationWindowConfigurer configurer) {
        windowControl.getContentPane().add(this.currentPage.getControl());
    }

    protected void prepareWindowForView(JFrame windowControl, ApplicationWindowConfigurer configurer) {
        windowControl.pack();
        windowControl.setSize(configurer.getInitialSize());

        // This works around a bug in setLocationRelativeTo(...): it currently
        // does not take multiple monitors into accounts on all operating
        // systems.
        try {
            // Note that if this is running on a JVM prior to 1.4, then an
            // exception will be thrown and we will fall back to
            // setLocationRelativeTo(...).
            final Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();

            final Dimension windowSize = windowControl.getSize();
            final int x = screenBounds.x + ((screenBounds.width - windowSize.width) / 2);
            final int y = screenBounds.y + ((screenBounds.height - windowSize.height) / 2);
            windowControl.setLocation(x, y);
        }
        catch (Throwable t) {
            windowControl.setLocationRelativeTo(null);
        }
    }

    protected JFrame createNewWindowControl() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        WindowAdapter windowCloseHandler = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        };
        frame.addWindowListener(windowCloseHandler);
        return frame;
    }

    protected void updatePageControl(ApplicationPage oldPage) {
        control.getContentPane().remove(oldPage.getControl());
        control.getContentPane().add(this.currentPage.getControl());
        control.validate();
    }

    public JFrame getControl() {
        return control;
    }

    public boolean isControlCreated() {
        return control != null;
    }

    protected JMenuBar createMenuBarControl() {
        JMenuBar menuBar = menuBarCommandGroup.createMenuBar();
        menuBarCommandGroup.setVisible(getWindowConfigurer().getShowMenuBar());
        return menuBar;
    }

    protected JComponent createToolBarControl() {
        JComponent toolBar = toolBarCommandGroup.createToolBar();
        toolBarCommandGroup.setVisible(getWindowConfigurer().getShowToolBar());
        return toolBar;
    }

    protected JComponent createStatusBarControl() {
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
            if (currentPage != null) {
                currentPage.close();
            }

            if (control != null) {
                control.dispose();
                control = null;
            }

            if (windowManager != null) {
                windowManager.remove(this);
            }
            windowManager = null;
        }
        return canClose;
    }
}