/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.richclient.beans;

import java.awt.Component;
import java.beans.BeanDescriptor;
import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.util.ClassUtils;

/**
 * Responsible for rendering a BeanFeatureNode object in the
 * BeanPropertyInspector tree.
 * 
 * @author  Keith Donald
 */
public class BeanInspectorRenderer
    extends DefaultTreeCellRenderer
    implements TreeCellRenderer {
    private BeanPropertyNameRenderer propertyRenderer =
        new DefaultBeanPropertyNameRenderer();
    private static String PROPERTY_ICON_KEY = "property.icon";
    private static String COMPONENT_ICON_KEY = "component.icon";
    private static String BEAN_ICON_KEY = "bean.icon";
    private IconSource iconRegistry =
        ApplicationServices.locator();

    /**
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(
        JTree tree,
        Object value,
        boolean sel,
        boolean expanded,
        boolean leaf,
        int row,
        boolean hasFocus) {
        super.getTreeCellRendererComponent(
            tree,
            value,
            sel,
            expanded,
            leaf,
            row,
            hasFocus);
        BeanInspectorTreeModel.BeanFeatureNode n =
            (BeanInspectorTreeModel.BeanFeatureNode)value;
        FeatureDescriptor f = n.getFeature();
        setText(propertyRenderer.renderShortName(f.getDisplayName()));
        if (f instanceof PropertyDescriptor) {
            PropertyDescriptor p = (PropertyDescriptor)f;
            if (ClassUtils.isSimpleScalar(p.getPropertyType())) {
                setIcon(iconRegistry.getIcon(PROPERTY_ICON_KEY));
            } else {
                setIcon(iconRegistry.getIcon(COMPONENT_ICON_KEY));
            }
        } else if (f instanceof BeanDescriptor) {
            setIcon(iconRegistry.getIcon(BEAN_ICON_KEY));
        }
        return this;
    }

}
