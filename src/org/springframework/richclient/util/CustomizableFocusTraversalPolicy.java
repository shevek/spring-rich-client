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

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.WindowConstants;

/**
 * A LayoutFocusTraversalPolicy that allows for individual containers to have a
 * custom focus order.
 * 
 * @author oliverh
 */
public class CustomizableFocusTraversalPolicy extends
        LayoutFocusTraversalPolicy {

    private static final String FOCUS_ORDER_PROPERTY_NAME = "customFocusOrder";

    /**
     * Installs an instance of CustomizableFocusTraversalPolicy as the default
     * focus traversal policy.
     */
    public static void installCustomizableFocusTraversalPolicy() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .setDefaultFocusTraversalPolicy(
                        new CustomizableFocusTraversalPolicy());
    }

    /**
     * Sets a custom focus traversal order for the given container. Child
     * components for which there is no order specified will receive focus after
     * components that do have an order specified in the standard "layout"
     * order.
     * 
     * @param container
     *            the container
     * @param componentsInOrder
     *            a list of child components in the order that thay should
     *            receive focus
     */
    public static void customizeFocusTraversalOrder(JComponent container,
            List componentsInOrder) {
        for (Iterator i = componentsInOrder.iterator(); i.hasNext();) {
            Component comp = (Component)i.next();
            if (comp.getParent() != container) { throw new IllegalArgumentException(
                    "Component [" + comp + "] is not a child of [" + container
                            + "]."); }
        }
        container.putClientProperty(FOCUS_ORDER_PROPERTY_NAME,
                componentsInOrder);
    }

    /**
     * Creates a new CustomizableFocusTraversalPolicy
     */
    public CustomizableFocusTraversalPolicy() {
        setComparator(new CustomizableFocusTraversalComparator(getComparator()));
    }

    private static class CustomizableFocusTraversalComparator implements
            Comparator {

        private Comparator layoutComparator;

        private CustomizableFocusTraversalComparator(Comparator layoutComparator) {
            this.layoutComparator = layoutComparator;
        }

        public int compare(Object o1, Object o2) {
            Component comp1 = (Component)o1;
            Component comp2 = (Component)o2;
            if (comp1 == comp2) { return 0; }
            if (comp1.getParent() == comp2.getParent()) {
                List order = getFocusOrder(comp1);
                if (order != null) {
                    int index1 = order.indexOf(comp1);
                    int index2 = order.indexOf(comp2);
                    if (index1 != -1 && index2 != -1) {
                        return index1 - index2;
                    }
                    else if (index1 != -1) {
                        return -1;
                    }
                    else if (index2 != -1) { return 1; }
                }
            }
            return layoutComparator.compare(comp1, comp2);
        }

        private List getFocusOrder(Component comp) {
            Component parent = comp.getParent();
            return (List)((parent instanceof JComponent) ? ((JComponent)parent)
                    .getClientProperty(FOCUS_ORDER_PROPERTY_NAME) : null);
        }
    }

    public static void main(String[] args) {

        installCustomizableFocusTraversalPolicy();

        JFrame frame = new JFrame();
        frame.setFocusTraversalPolicy(new CustomizableFocusTraversalPolicy());
        frame.setFocusCycleRoot(true);
        frame.setTitle("Focus Fun");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JComponent panel = buildPanel();
        frame.getContentPane().add(panel);
        frame.pack();
        frame.show();
    }

    private static JComponent buildPanel() {
        JFormattedTextField t1 = new JFormattedTextField("1st");
        JFormattedTextField t2 = new JFormattedTextField("2nd");
        JFormattedTextField t3 = new JFormattedTextField("3rd");
        JFormattedTextField t4 = new JFormattedTextField("4th");

        JPanel panel = new JPanel();
        panel.add(new JFormattedTextField("noOrder1st"));
        panel.add(t2);
        panel.add(t3);
        panel.add(t1);
        panel.add(new JFormattedTextField("noOrder2nd"));
        panel.add(t4);

        List order = new ArrayList();
        order.add(t1);
        order.add(t2);
        order.add(t3);
        order.add(t4);

        customizeFocusTraversalOrder(panel, order);

        return panel;
    }
}