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

import javax.swing.*;

import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.command.CommandManager;
import org.springframework.richclient.progress.StatusBarCommandGroup;
import org.springframework.richclient.util.PersistableElement;

/**
 * A main application window (or frame) consisting of a menu bar, tool bar,
 * single page (content pane), and status bar.
 */
public interface ApplicationWindow extends PersistableElement {
    int getNumber();

    CommandManager getCommandManager();

    CommandGroup getMenuBar();

    CommandGroup getToolBar();

    StatusBarCommandGroup getStatusBar();

    void setWindowManager(WindowManager windowManager);

    void openPage(ViewDescriptor viewDescriptor);

    void open();

    void showViewOnPage(String viewName);

    void showViewOnPage(ViewDescriptor viewDescriptor);

    View getView();

    JFrame getControl();

    boolean isControlCreated();

    boolean close();

}
