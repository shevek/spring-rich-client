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
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.text.HtmlPane;
import org.springframework.util.FileCopyUtils;

/**
 * An implementation of an about box..
 * 
 * @author Keith Donald
 * @author Oliver Hutchison
 */
public class AboutBox {
    private Resource aboutTextPath;

    public AboutBox() {
    }

    public void setAboutTextPath(Resource path) {
        this.aboutTextPath = path;
    }

    protected String getApplicationName() {
        return Application.instance().getName();
    }

    public void display(Window parent) {
        AboutDialog aboutMainDialog = new AboutDialog();
        aboutMainDialog.setParent(parent);
        aboutMainDialog.showDialog();
    }

    private class AboutDialog extends ApplicationDialog {

        private HtmlScroller scroller;

        public AboutDialog() {
            setTitle("About " + getApplicationName());
            setResizable(false);
            setCloseAction(CloseAction.DISPOSE);
            scroller = new HtmlScroller(false, 2000, 15, 10);
        }

        protected void addDialogComponents() {
            JComponent dialogContentPane = createDialogContentPane();
            getDialogContentPane().add(dialogContentPane);
            getDialogContentPane().add(createButtonBar(), BorderLayout.SOUTH);
        }

        protected JComponent createDialogContentPane() {
            JPanel dialogPanel = new JPanel(new BorderLayout());

            try {
                String text = FileCopyUtils.copyToString(new BufferedReader(new InputStreamReader(aboutTextPath
                        .getInputStream())));
                scroller.setHtml(text);
            }
            catch (IOException e) {
                final IllegalStateException exp =
                        new IllegalStateException("About text not accessible: "+e.getMessage());
                exp.setStackTrace(e.getStackTrace());
                throw exp;
            }
            dialogPanel.add(scroller);
            dialogPanel.setPreferredSize(new Dimension(scroller.getPreferredSize().width, 200));
            dialogPanel.add(new JSeparator(), BorderLayout.SOUTH);
            return dialogPanel;
        }

        protected void onAboutToShow() {
            try {
                String text = FileCopyUtils.copyToString(new BufferedReader(new InputStreamReader(aboutTextPath
                        .getInputStream())));
                scroller.setHtml(text);
            }
            catch (IOException e) {
                final IllegalStateException exp =
                        new IllegalStateException("About text not accessible: "+e.getMessage());
                exp.setStackTrace(e.getStackTrace());
                throw exp;
            }
            scroller.reset();
            scroller.startScrolling();
        }

        protected boolean onFinish() {
            scroller.pauseScrolling();
            return true;
        }

        protected Object[] getCommandGroupMembers() {
            return new AbstractCommand[] { getFinishCommand() };
        }
    }

    /**
     * A panel that scrolls the content of a HTML document.
     * 
     * @author Oliver Hutchison
     */
    private static class HtmlScroller extends JViewport {

        private HtmlPane htmlPane;

        private Timer timer;

        private int initalDelay;

        private double incY = 0;

        private double currentY = 0;

        private double currentX = 0;

        /**
         * Created a new HtmlScroller.
         * 
         * @param antiAlias
         *            antialias the rendered HTML
         * @param initalDelay
         *            inital delay after which scrolling begins
         * @param speedPixSec
         *            scoll speed in pixels per second
         * @param fps
         *            number of updates per second
         */
        public HtmlScroller(boolean antiAlias, int initalDelay, int speedPixSec, int fps) {
            this.initalDelay = initalDelay;

            incY = (double)speedPixSec / (double)fps;

            htmlPane = new HtmlPane();
            htmlPane.setAntiAlias(antiAlias);
            htmlPane.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType().equals(HyperlinkEvent.EventType.ENTERED)) {
                        enteredLink();
                    }
                    else if (e.getEventType().equals(HyperlinkEvent.EventType.EXITED)) {
                        exitedLink();
                    }
                }
            });
            setView(htmlPane);
            timer = new Timer(1000 / fps, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int maxY = htmlPane.getHeight() - getHeight();
                    currentY = Math.max(0, Math.min(currentY + incY, maxY));
                    if (currentY <= 0 || currentY == maxY) {
                        pauseScrolling();
                    }
                    setViewPositionInternal(new Point((int)currentX, (int)currentY));
                }
            });
            reset();
        }

        /**
         * Sets the HTML that will be rendered by this component.
         */
        public void setHtml(String html) {
            htmlPane.setText(html);
            setPreferredSize(htmlPane.getPreferredSize());
        }

        /**
         * Resets this component to its inital state.
         */
        public final void reset() {
            currentX = 0;
            currentY = 0;
            timer.setInitialDelay(initalDelay);
            setViewPositionInternal(new Point((int)currentX, (int)currentY));
        }

        /**
         * Starts the scoller
         */
        public void startScrolling() {
            timer.start();
        }

        /**
         * Pauses the scoller.
         */
        public void pauseScrolling() {
            timer.stop();
            timer.setInitialDelay(0);
        }

        public void setViewPosition(Point p) {
            // ignore calls that are not internal
        }

        private void setViewPositionInternal(Point p) {
            super.setViewPosition(p);
        }

        private void enteredLink() {
            pauseScrolling();
        }

        private void exitedLink() {
            startScrolling();
        }
    }
}