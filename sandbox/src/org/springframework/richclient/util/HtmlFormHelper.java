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
package org.springframework.richclient.util;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

/**
 * @author Oliver Hutchison
 */
public class HtmlFormHelper extends DefaultCaret
        implements
            HyperlinkListener,
            MouseListener {

    private boolean inLink;

    private HtmlFormHelper() {
    }

    public static void installFormPresets(JTextPane formPane) {
        formPane.setEditorKit(new SynchronousHTMLEditorKit());
        formPane.setEditable(false);
        HtmlFormHelper helper = new HtmlFormHelper();
        formPane.setCaret(helper);
        formPane.addMouseListener(helper);
        formPane.addHyperlinkListener(helper);
        installLaFStyleSheet((HTMLDocument)formPane.getDocument());
    }

    public static void installLaFStyleSheet(HTMLDocument doc) {
        Font defaultFont = UIManager.getFont("Button.font");
        String stylesheet = "body {  font-family: " + defaultFont.getName()
                + "; font-size: " + defaultFont.getSize() + "pt;  }"
                + "a, p, li { font-family: " + defaultFont.getName()
                + "; font-size: " + defaultFont.getSize() + "pt;  }";
        try {
            doc.getStyleSheet().loadRules(new StringReader(stylesheet), null);
        }
        catch (IOException e) {
        }
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
        if (inLink) {
            exitedLink(((JTextPane)e.getSource()));
        }
    }

    public void mouseEntered(MouseEvent e) {
        if (inLink) {
            enteredLink(((JTextPane)e.getSource()));
        }
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType().equals(HyperlinkEvent.EventType.ENTERED)) {
            inLink = true;
            enteredLink(((JTextPane)e.getSource()));
        }
        else if (e.getEventType().equals(HyperlinkEvent.EventType.EXITED)) {
            inLink = false;
            exitedLink(((JTextPane)e.getSource()));
        }
        else if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
            if (e.getDescription().startsWith("#")) {
                ((JTextPane)e.getSource()).scrollToReference(e.getDescription()
                        .substring(1));
            }
        }
    }

    private void enteredLink(JTextPane formPane) {
        formPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void exitedLink(JTextPane formPane) {
        formPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public static class SynchronousHTMLEditorKit extends HTMLEditorKit {

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
}