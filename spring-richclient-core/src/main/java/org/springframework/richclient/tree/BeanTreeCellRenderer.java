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
package org.springframework.richclient.tree;

import java.awt.Component;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.FatalBeanException;
import org.springframework.richclient.core.DescribedElement;
import org.springframework.richclient.core.VisualizedElement;
import org.springframework.util.Assert;

/**
 * 
 * @author Keith Donald
 */
public class BeanTreeCellRenderer extends FocusableTreeCellRenderer {
    private BeanInfo beanInfo;

    private String propertyName;

    public BeanTreeCellRenderer() {

    }

    public BeanTreeCellRenderer(Class beanClass) {
        this(beanClass, null);
    }

    public BeanTreeCellRenderer(Class beanClass, String propertyName) {
        Assert.notNull(beanClass);
        try {
            this.beanInfo = Introspector.getBeanInfo(beanClass);
        }
        catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        this.propertyName = propertyName;
    }

    /**
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
     *      java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            Object bean = node.getUserObject();
            if (bean != null && !BeanUtils.isSimpleProperty(bean.getClass())) {
                if (bean instanceof DescribedElement) {
                    DescribedElement element = (DescribedElement)bean;
                    setText(element.getDisplayName());
                    setToolTipText(element.getCaption());
                }
                else {
                    BeanWrapper wrapper = new BeanWrapperImpl(bean);
                    try {
                        Object text = propertyName != null ? wrapper.getPropertyValue(propertyName) : wrapper
                                .getPropertyValue("name");
                        setText(String.valueOf(text));
                    }
                    catch (FatalBeanException e) {

                    }
                }

                if (bean instanceof VisualizedElement) {
                    VisualizedElement element = (VisualizedElement) bean;
                    setIcon(element.getIcon());
                }
                else {
                    if (beanInfo != null) {
                        Image icon = beanInfo.getIcon(BeanInfo.ICON_COLOR_16x16);
                        if (icon != null) {
                            setIcon(new ImageIcon(icon));
                        }
                    }
                }
                
            }
        }
        return this;
    }
}