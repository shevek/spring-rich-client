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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.dialog.CloseAction;
import org.springframework.util.FileCopyUtils;

/**
 * An implementation of an about box..
 * 
 * @author Keith Donald
 */
public class AboutBox {

    private ApplicationInfo applicationInfo;

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
                String text = FileCopyUtils.copyToString(new BufferedReader(
                        new InputStreamReader(aboutTextPath.getInputStream())));
                scroller.setHtml(text);
            }
            catch (IOException e) {
                throw new DataAccessResourceFailureException(
                        "About text not accessible", e);
            }
            dialogPanel.add(scroller);
            dialogPanel.setPreferredSize(new Dimension(scroller
                    .getPreferredSize().width, 200));
            dialogPanel.add(new JSeparator(), BorderLayout.SOUTH);
            return dialogPanel;
        }

        protected void onAboutToShow() {
            try {
                String text = FileCopyUtils.copyToString(new BufferedReader(
                        new InputStreamReader(aboutTextPath.getInputStream())));
                scroller.setHtml(text);
            }
            catch (IOException e) {
                throw new DataAccessResourceFailureException(
                        "About text not accessible", e);
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
     * @author oliverh
     */
    private class HtmlScroller extends JViewport {

        private JTextPane htmlPane;

        private Timer timer;

        private int initalDelay;

        private double incY = 0;

        private double currentY = 0;

        private double currentX = 0;

        private boolean inLink;

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
        public HtmlScroller(boolean antiAlias, int initalDelay,
                int speedPixSec, int fps) {
            this.initalDelay = initalDelay;

            incY = (double)speedPixSec / (double)fps;

            htmlPane = antiAlias ? new AntiAliasedTextPane() : new JTextPane();
            htmlPane.setEnabled(false);
            htmlPane.setEditable(false);
            htmlPane.setEditorKit(new NotLazyHTMLEditorKit());
            htmlPane.addMouseListener(new MouseAdapter() {
                public void mouseExited(MouseEvent e) {
                    if (inLink) {
                        exitedLink();
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    if (inLink) {
                        enteredLink();
                    }
                }
            });
            htmlPane.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent e) {

                    if (e.getEventType().equals(
                            HyperlinkEvent.EventType.ENTERED)) {
                        inLink = true;
                        enteredLink();
                    }
                    else if (e.getEventType().equals(
                            HyperlinkEvent.EventType.EXITED)) {
                        inLink = false;
                        exitedLink();
                    }
                    else if (e.getEventType().equals(
                            HyperlinkEvent.EventType.ACTIVATED)) {
                        System.out.println(e.getURL()); // XXX need to open a
                        // browser
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
                    setViewPositionInternal(new Point((int)currentX,
                            (int)currentY));
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
            installLaFStyleSheet();
        }

        /**
         * Resets this component to its inital state.
         */
        public void reset() {
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

        public void installLaFStyleSheet() {
            Font defaultFont = UIManager.getFont("Button.font");

            String stylesheet = "body {  font-family: " + defaultFont.getName()
                    + "; font-size: " + defaultFont.getSize() + "pt;  }"
                    + "a, p, li { font-family: " + defaultFont.getName()
                    + "; font-size: " + defaultFont.getSize() + "pt;  }";

            HTMLDocument doc = (HTMLDocument)htmlPane.getDocument();
            try {
                doc.getStyleSheet().loadRules(new StringReader(stylesheet),
                        null);
            }
            catch (IOException e) {
            }
        }

        private void enteredLink() {
            pauseScrolling();
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        private void exitedLink() {
            startScrolling();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private static class NotLazyHTMLEditorKit extends HTMLEditorKit {

        public Document createDefaultDocument() {
            HTMLDocument doc = (HTMLDocument)super.createDefaultDocument();
            doc.setAsynchronousLoadPriority(-1);
            return doc;
        }

        public ViewFactory getViewFactory() {
            return new HTMLFactory() {
                public View create(Element elem) {
                    View view = super.create(elem);
                    if (view instanceof ImageView) {
                        ((ImageView)view).setLoadsSynchronously(true);
                    }
                    return view;

                }
            };
        }
    }

    private static class AntiAliasedTextPane extends JTextPane {
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            super.paintComponent(g2);
        }
    }
}