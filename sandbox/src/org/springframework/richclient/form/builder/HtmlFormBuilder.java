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
package org.springframework.richclient.form.builder;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.FormView;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.forms.SwingFormModel;

/**
 * @author oliverh
 */
public class HtmlFormBuilder extends AbstractFormBuilder {

    private JPanel panel;

    private JTextPane htmlPane;

    private Map formViewMap;

    protected boolean inLink;
    
    public HtmlFormBuilder(SwingFormModel formModel, String html) {
        super(formModel);
        formViewMap = new HashMap();
        panel = new JPanel(new BorderLayout());
        htmlPane = new JTextPane();
        panel.add(htmlPane);
        htmlPane.setEditorKit(new InternalHTMLEditorKit());
        htmlPane.setFocusCycleRoot(false);
        htmlPane.setCaret(new DefaultCaret() {
            public void mouseDragged(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {
            }
            
            public void mouseClicked(MouseEvent e) {
                
            }
        });
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

                if (e.getEventType().equals(HyperlinkEvent.EventType.ENTERED)) {
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
        setHtml(html);
    }

    private void enteredLink() {
        Application.instance().getActiveWindow().getControl().setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void exitedLink() {
        Application.instance().getActiveWindow().getControl().setCursor(
                Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void setHtml(String html) {
        htmlPane.setText(html);
        htmlPane.setEditable(false);
        installLaFStyleSheet((HTMLDocument)htmlPane.getDocument());

        for (Iterator i = formViewMap.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry)i.next();
            Element element = (Element)entry.getKey();
            FormView view = (FormView)entry.getValue();

            String propertyName = (String)element.getAttributes().getAttribute(
                    HTML.getAttributeKey("id"));
            if (propertyName != null) {
                JComponent comp = (JComponent) view.getComponent();
                if (getInterceptor() != null) {
                    if (getInterceptor().processComponent(propertyName, comp) != comp) {
                        throw new UnsupportedOperationException("Can't do this...");
                    }
                }
                if (comp instanceof JTextComponent) {
                    getFormModel().bind((JTextComponent)comp, propertyName);
                }
                else if (comp instanceof JComboBox) {
                    getFormModel().bind((JComboBox)comp, propertyName);
                }
                else if (comp instanceof JCheckBox) {
                    ((JCheckBox)comp).setOpaque(false);
                    getFormModel().bind((JCheckBox)comp, propertyName);
                }
                else {
                    System.out.println("Don't know how to bind " + comp);
                }
            }
        }
    }

    public JComponent getForm() {
        return panel;
    }

    public void installLaFStyleSheet(HTMLDocument doc) {
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

    private class InternalHTMLEditorKit extends HTMLEditorKit {

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
                    else if (view instanceof FormView) {
                        formViewMap.put(elem, view);
                    }
                    return view;
                }
            };
        }
    }
}