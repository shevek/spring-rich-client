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
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.FormView;
import javax.swing.text.html.HTML;

import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.text.HtmlPane;
import org.springframework.richclient.text.SynchronousHTMLEditorKit;

/**
 * @author Oliver Hutchison
 */
public class HtmlFormBuilder extends AbstractFormBuilder {

    private JPanel panel;

    private JTextPane htmlPane;

    private Map formViewMap;

    protected boolean inLink;

    public HtmlFormBuilder(BindingFactory bindingFactory, String html) {
        super(bindingFactory);
        formViewMap = new HashMap();
        panel = new JPanel(new BorderLayout());
        htmlPane = new HtmlPane();
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
        setHtml(html);
    }

    private void setHtml(String html) {
        htmlPane.setText(html);
        htmlPane.setEditable(false);

        for (Iterator i = formViewMap.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry)i.next();
            Element element = (Element)entry.getKey();
            FormView view = (FormView)entry.getValue();

            String propertyName = (String)element.getAttributes().getAttribute(HTML.getAttributeKey("id"));
            if (propertyName != null) {
                JComponent comp = (JComponent)view.getComponent();
                getBindingFactory().bindControl(comp, propertyName);
                if (comp instanceof JCheckBox)
                    ((JCheckBox)comp).setOpaque(false);
            }
        }
    }

    public JComponent getForm() {
        return panel;
    }

    private class InternalHTMLEditorKit extends SynchronousHTMLEditorKit {

        public ViewFactory getViewFactory() {
            return new HTMLFactory() {
                public View create(Element elem) {
                    View view = super.create(elem);
                    if (view instanceof FormView) {
                        formViewMap.put(elem, view);
                    }
                    return view;
                }
            };
        }
    }
}