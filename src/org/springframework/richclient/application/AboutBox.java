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
import java.awt.Window;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.util.FileCopyUtils;

/**
 * A simple implementation of an about box..
 * 
 * @author Keith Donald
 */
public class AboutBox {
    private ApplicationInfo applicationInfo;

    private Resource aboutTextPath;

    private ApplicationDialog aboutMainDialog;

    public AboutBox() {

    }

    public void setAboutTextPath(Resource path) {
        this.aboutTextPath = path;
    }

    protected String getApplicationName() {
        return Application.locator().getName();
    }

    public void display(Window parent) {
        if (aboutMainDialog == null) {
            initDialog();
        }
        aboutMainDialog.setParent(parent);
        aboutMainDialog.showDialog();
    }

    private void initDialog() {
        aboutMainDialog = new AboutDialog();
    }

    private class AboutDialog extends ApplicationDialog {

        public AboutDialog() {
            setTitle("About " + getApplicationName());
            setResizable(true);
        }

        protected JComponent createDialogContentPane() {
            JPanel dialogPanel = new JPanel(new BorderLayout());

            JTextArea aboutTextArea = new JTextArea();
            aboutTextArea.setEditable(false);
            try {
                String text = FileCopyUtils.copyToString(new BufferedReader(
                        new InputStreamReader(aboutTextPath.getInputStream())));
                aboutTextArea.setText(text);
            }
            catch (IOException e) {
                throw new DataAccessResourceFailureException(
                        "About text not accessible", e);
            }
            dialogPanel.add(new JScrollPane(aboutTextArea));
            return dialogPanel;
        }

        protected boolean onFinish() {
            return true;
        }

        protected Object[] getCommandGroupMembers() {
            return new AbstractCommand[] { getFinishCommand() };
        }
    }

}