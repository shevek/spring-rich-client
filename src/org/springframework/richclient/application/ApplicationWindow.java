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
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.progress.StatusBarCommandGroup;
import org.springframework.richclient.util.Memento;
import org.springframework.richclient.util.PersistableElement;

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

    private ApplicationWindowConfigurer windowConfigurer;

    private JFrame control;

    private ApplicationPage activePage;

    private WindowManager windowManager;

    public ApplicationWindow(int number) {
        this.number = number;
        ApplicationAdvisor advisor = getApplicationAdvisor();
        advisor.onPreWindowOpen(getWindowConfigurer());
        this.commandManager = advisor.createWindowCommandManager();
        this.menuBarCommandGroup = advisor.getMenuBarCommandGroup();
        this.toolBarCommandGroup = advisor.getToolBarCommandGroup();
        this.statusBarCommandGroup = advisor.getStatusBarCommandGroup();
        advisor.onCommandsCreated(this);
    }

    public int getNumber() {
        return number;
    }

    protected ApplicationAdvisor getApplicationAdvisor() {
        return getApplication().getApplicationAdvisor();
    }

    protected ApplicationWindowConfigurer getWindowConfigurer() {
        if (windowConfigurer == null) {
            this.windowConfigurer = createWindowConfigurer();
        }
        return windowConfigurer;
    }

    protected ApplicationWindowConfigurer createWindowConfigurer() {
        return new DefaultApplicationWindowConfigurer(this);
    }

    public CommandManager getCommandManager() {
        return commandManager;
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

    protected Application getApplication() {
        return Application.instance();
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public void openPage(ViewDescriptor viewDescriptor) {
        if (this.activePage == null) {
            ApplicationPage page = new ApplicationPage(this);
            final GlobalCommandTargeter commandTargeter = new GlobalCommandTargeter(
                    getCommandManager());
            page.addViewListener(commandTargeter);
            page.showView(viewDescriptor);
            this.activePage = page;
        }
        else {
            Application.instance().openWindow(viewDescriptor);
        }
    }

    public void open() {
        this.control = createWindowControl();
        getApplicationAdvisor().onWindowCreated(this);
        getApplicationAdvisor().showIntroComponentIfNecessary(this);
        this.control.setVisible(true);
        getApplicationAdvisor().onWindowOpened(this);
    }

    public void showViewOnPage(String viewName) {
        activePage.showView(viewName);
    }

    public void showViewOnPage(ViewDescriptor viewDescriptor) {
        activePage.showView(viewDescriptor);
    }

    public View getView() {
        return this.activePage.getView();
    }

    public JFrame getControl() {
        return control;
    }

    public boolean isControlCreated() {
        return control != null;
    }

    protected JFrame createWindowControl() {
        JFrame control = createNewWindowControl();
        ApplicationWindowConfigurer configurer = getWindowConfigurer();
        control.setTitle(configurer.getTitle());
        control.setIconImage(configurer.getImage());
        control.setJMenuBar(menuBarCommandGroup.createMenuBar());
        menuBarCommandGroup.setVisible(configurer.getShowMenuBar());

        control.getContentPane().setLayout(new BorderLayout());
        control.getContentPane().add(createToolBar(), BorderLayout.NORTH);
        control.getContentPane().add(this.activePage.getControl(),
                BorderLayout.CENTER);
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
        return control;
    }

    protected JFrame createNewWindowControl() {
        return new JFrame();
    }

    protected JComponent createToolBar() {
        JToolBar toolBar = toolBarCommandGroup.createToolBar();
        toolBarCommandGroup.setVisible(getWindowConfigurer().getShowToolBar());
        return toolBar;
    }

    protected JComponent createStatusBar() {
        JComponent statusBar = statusBarCommandGroup.getControl();
        statusBarCommandGroup.setVisible(getWindowConfigurer()
                .getShowStatusBar());
        return statusBar;
    }

    public boolean close() {
        boolean canClose = getApplicationAdvisor().onPreWindowClose(this);
        if (canClose) {
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