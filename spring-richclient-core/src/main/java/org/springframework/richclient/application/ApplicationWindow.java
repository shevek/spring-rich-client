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

import java.util.Iterator;

import javax.swing.JFrame;

import org.springframework.richclient.application.statusbar.StatusBar;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;

/**
 * A main application window (or frame) consisting of a menu bar, tool bar,
 * single page (content pane), and status bar.
 */
public interface ApplicationWindow {
    public int getNumber();

    public ApplicationPage getPage();

    public CommandManager getCommandManager();

    public Iterator getSharedCommands();

    public CommandGroup getMenuBar();

    public CommandGroup getToolBar();

    public StatusBar getStatusBar();

    public JFrame getControl();

    public void showPage(String pageDescriptorId);

    public void showPage(PageDescriptor pageDescriptor);

    public void showPage(ApplicationPage page);

    public boolean close();

    public void setWindowManager(WindowManager windowManager);

    public void addPageListener(PageListener listener);

    public void removePageListener(PageListener listener);

}