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
package org.springframework.richclient.application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.config.ApplicationAdvisor;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.progress.StatusBar;
import org.springframework.richclient.progress.StatusBarCommandGroup;
import org.springframework.richclient.util.Memento;
import org.springframework.richclient.util.PersistableElement;
import org.springframework.util.Assert;

/**
 * A main application window (or frame) consisting of a menu bar, tool bar,
 * single page (content pane), and status bar.
 */
public class ApplicationWindow implements PersistableElement {
    protected Log logger = LogFactory.getLog(getClass());

    private int number;

    private CommandManager commandManager;

    private CommandGroup menuBarCommandGroup;

    private CommandGroup toolBarCommandGroup;

    private StatusBarCommandGroup statusBarCommandGroup;

    private DefaultApplicationWindowConfigurer windowConfigurer;

    private JFrame control;

    private ApplicationPage activePage;

    private WindowManager windowManager;

    private String pageId;

    public ApplicationWindow(int number) {
        this.number = number;
        getLifecycleAdvisor().onPreWindowOpen(getWindowConfigurer());
        this.commandManager = getLifecycleAdvisor().getCommandManager();
        this.menuBarCommandGroup = getLifecycleAdvisor()
                .getMenuBarCommandGroup();
        this.toolBarCommandGroup = getLifecycleAdvisor()
                .getToolBarCommandGroup();
        this.statusBarCommandGroup = getLifecycleAdvisor()
                .getStatusBarCommandGroup();
    }

    public int getNumber() {
        return number;
    }

    public CommandManager getCommandRegistry() {
        return commandManager;
    }

    public CommandGroup getMenuBar() {
        return menuBarCommandGroup;
    }

    public CommandGroup getToolBar() {
        return menuBarCommandGroup;
    }

    public StatusBarCommandGroup getStatusBar() {
        return statusBarCommandGroup;
    }

    protected Application getApplication() {
        return Application.instance();
    }

    protected ApplicationAdvisor getLifecycleAdvisor() {
        return getApplication().getAdvisor();
    }

    protected DefaultApplicationWindowConfigurer getWindowConfigurer() {
        if (windowConfigurer == null) {
            windowConfigurer = new DefaultApplicationWindowConfigurer(this);
        }
        return windowConfigurer;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public String getActivePageId() {
        return pageId;
    }

    public void openPage(String pageId) {
        Assert.notNull(pageId);
        Perspective perspective = (Perspective)getPageTemplate(pageId);
        ApplicationPage page = new ApplicationPage(this);
        page.addViewListener(new GlobalCommandTargeterViewListener(
                getCommandRegistry()));
        page.showView(perspective.getView());
        if (page == activePage) { return; }
        if (control == null) {
            this.control = createWindowControl(page);
            getLifecycleAdvisor().showIntroIfNecessary(this);
            this.control.setVisible(true);
            getLifecycleAdvisor().onWindowOpened(this);
        }
        this.activePage = page;
        this.pageId = pageId;
    }

    protected Perspective getPageTemplate(String pageId) {
        return (Perspective)Application.services().getApplicationContext()
                .getBean(pageId);
    }

    public void showViewOnPage(String viewName) {
        activePage.showView(viewName);
    }

    public void showViewOnPage(ViewDescriptor viewDescriptor) {
        activePage.showView(viewDescriptor);
    }

    public JFrame getControl() {
        return control;
    }

    public boolean isControlCreated() {
        return control != null;
    }

    protected JFrame createWindowControl(ApplicationPage page) {
        JFrame control = new JFrame();
        control.setTitle(getTitle());
        control.setIconImage(getImage());
        control.setJMenuBar(menuBarCommandGroup.createMenuBar());
        menuBarCommandGroup.setVisible(getShowMenuBar());

        control.getContentPane().setLayout(new BorderLayout());
        control.getContentPane().add(createToolBar(), BorderLayout.NORTH);
        control.getContentPane().add(page.getControl(), BorderLayout.CENTER);
        control.getContentPane().add(createStatusBar(), BorderLayout.SOUTH);
        control.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        control.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        control.pack();
        control.setSize(getInitialSize());
        control.setLocationRelativeTo(null);
        getLifecycleAdvisor().onWindowCreated(this);
        return control;
    }

    protected String getTitle() {
        return getWindowConfigurer().title;
    }

    protected Image getImage() {
        return getWindowConfigurer().image;
    }

    protected Dimension getInitialSize() {
        return getWindowConfigurer().initialSize;
    }

    protected boolean getShowMenuBar() {
        return getWindowConfigurer().showMenuBar;
    }

    protected boolean getShowToolBar() {
        return getWindowConfigurer().showToolBar;
    }

    protected boolean getShowStatusBar() {
        return getWindowConfigurer().showStatusBar;
    }

    protected JComponent createToolBar() {
        JToolBar toolBar = toolBarCommandGroup.createToolBar();
        toolBarCommandGroup.setVisible(getShowToolBar());
        return toolBar;
    }

    protected JComponent createStatusBar() {
        StatusBar statusBar = statusBarCommandGroup.createStatusBar();
        statusBarCommandGroup.setVisible(getShowStatusBar());
        return statusBar;
    }

    public void close() {
        getLifecycleAdvisor().preWindowClose(this);
        control.dispose();
        control = null;
        if (windowManager != null) {
            windowManager.remove(this);
        }
        windowManager = null;
    }

    public void saveState(Memento memento) {
        Rectangle bounds = this.control.getBounds();
        memento.putInteger("x", bounds.x);
        memento.putInteger("y", bounds.y);
        memento.putInteger("width", bounds.width);
        memento.putInteger("height", bounds.height);
    }

}