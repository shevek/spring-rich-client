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
package org.springframework.richclient.text;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;

/**
 * An extension of JTextPane for displaying HTML with the system LaF. 
 * 
 * @author Oliver Hutchison
 */
public class HtmlPane extends JTextPane {
    private boolean antiAlias;

    /**
     * Creates a new HtmlPane. A default hyperlink activation handler will be
     * installed.
     */
    public HtmlPane() {
        this(true);
    }

    /**
     * Creates a new HtmlPane.
     * 
     * @param installHyperlinkActivationHandler
     *            whether to install a default hyperlink activation handler.
     */
    public HtmlPane(boolean installHyperlinkActivationHandler) {
        setEditorKit(new SynchronousHTMLEditorKit());
        setEditable(false);
        HyperlinkEnterExitBugFixer bugFixer = new HyperlinkEnterExitBugFixer();
        addMouseListener(bugFixer);
        addHyperlinkListener(bugFixer);
        if (installHyperlinkActivationHandler) {
            addHyperlinkListener(new DefaultHyperlinkActivationHandler());
        }
    }

    /**
     * Is the HTML rendered with antialiasing.
     */
    public boolean getAntiAlias() {
        return antiAlias;
    }

    /**
     * Set whether the pane should render the HTML using antialiasing.
     */
    public void setAntiAlias(boolean antiAlias) {
        if (this.antiAlias == antiAlias) { return; }
        this.antiAlias = antiAlias;
        firePropertyChange("antiAlias", !antiAlias, antiAlias);
        repaint();
    }

    /**
     * Applies the current LaF font setting to the document.
     */
    protected void installLaFStyleSheet() {
        Font defaultFont = UIManager.getFont("Button.font");
        String stylesheet = "body {  font-family: " + defaultFont.getName() + "; font-size: " + defaultFont.getSize()
                + "pt;  }" + "a, p, li { font-family: " + defaultFont.getName() + "; font-size: "
                + defaultFont.getSize() + "pt;  }";
        try {
            ((HTMLDocument)getDocument()).getStyleSheet().loadRules(new StringReader(stylesheet), null);
        }
        catch (IOException e) {
        }
    }

    public void paintComponent(Graphics g) {
        if (antiAlias) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }
        super.paintComponent(g);
    }

    /*
     * Fixes a bug in the handling of HyperlinkEvents in JEditorPane. If a
     * hyperlink is active when the mouse exits the pane no Hyperlink EXITED
     * event is fired.
     */
    private class HyperlinkEnterExitBugFixer extends MouseAdapter implements HyperlinkListener {
        private boolean hyperlinkActive;

        public void mouseExited(MouseEvent e) {
            if (hyperlinkActive) {
                fireHyperlinkUpdate(new HyperlinkEvent(HtmlPane.this, HyperlinkEvent.EventType.EXITED, null));
                hyperlinkActive = true;
            }
        }

        public void mouseEntered(MouseEvent e) {
            if (hyperlinkActive) {
                fireHyperlinkUpdate(new HyperlinkEvent(HtmlPane.this, HyperlinkEvent.EventType.ENTERED, null));
            }
        }

        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ENTERED)) {
                hyperlinkActive = true;
            }
            else if (e.getEventType().equals(HyperlinkEvent.EventType.EXITED)) {
                hyperlinkActive = false;
            }
        }
    }
}