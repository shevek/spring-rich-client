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
package org.springframework.richclient.bean;

import java.beans.FeatureDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.richclient.treetable.AbstractTreeTableModel;
import org.springframework.richclient.treetable.TreeTableModel;
import org.springframework.richclient.util.ClassUtils;
import org.springframework.util.comparator.PropertyComparator;
import org.springframework.util.comparator.BooleanComparator;
import org.springframework.util.comparator.CompoundComparator;

/**
 * An implementation of a tree table model for inspecting bean properties. Each
 * object in the tree is a bean feature node which corresponds to a bean feature
 * such as a property descriptor. The value for each tree element is determined
 * based on the type of property for that particular tree node. If the property
 * has a simple scalar value, the value is rendered; otherwise null is returned.
 * 
 * @author Keith Donald
 * @see AbstractTreeTableModel
 */
public class BeanInspectorTreeModel extends AbstractTreeTableModel {
    private static Log logger = LogFactory.getLog(BeanInspectorTreeModel.class);

    private static String PROPERTY = "Property";

    private static String VALUE = "Value";

    private static String[] names = { PROPERTY, VALUE };

    private static Class[] clazzes = { TreeTableModel.class, Object.class };

    public BeanInspectorTreeModel(Object bean) {
        super(new BeanFeatureNode(bean));
    }

    /**
     * Internal class representing a node in the tree table. A node corresponds
     * to a feature of a javabean -- either a scalar property or a component
     * (another bean.)
     * 
     * @author Keith Donald
     */
    public static class BeanFeatureNode {
        public static final CompoundComparator TYPE_THEN_NAME_COMPARATOR = new CompoundComparator();
        static {
            TYPE_THEN_NAME_COMPARATOR.addComparator(new PropertyComparator(
                    "simpleScalar", BooleanComparator.instance()));
            TYPE_THEN_NAME_COMPARATOR.addComparator(new PropertyComparator(
                    "name", String.CASE_INSENSITIVE_ORDER));
        }

        private Object bean;

        private FeatureDescriptor feature;

        private Object[] children;

        private BeanWrapper beanWrapper = new BeanWrapperImpl();

        public BeanFeatureNode(Object bean) {
            this.bean = bean;
            this.beanWrapper.setWrappedInstance(bean);
            try {
                this.feature = Introspector.getBeanInfo(bean.getClass())
                        .getBeanDescriptor();
            }
            catch (Exception e) {
                logger
                        .error("Unable to introspect bean: " + bean.getClass(),
                                e);
            }
        }

        private BeanFeatureNode(Object bean, FeatureDescriptor feature) {
            this.bean = bean;
            if (bean != null) {
                this.beanWrapper.setWrappedInstance(bean);
            }
            this.feature = feature;
        }

        public String getName() {
            return feature.getName();
        }

        public Object getBean() {
            return bean;
        }

        public FeatureDescriptor getFeature() {
            return feature;
        }

        public boolean isSimpleScalar() {
            if (bean == null) {
                if (feature instanceof PropertyDescriptor) {
                    PropertyDescriptor p = (PropertyDescriptor)feature;
                    return ClassUtils.isSimpleScalar(p.getPropertyType());
                }
            }
            return ClassUtils.isSimpleScalar(bean.getClass());
        }

        public Object[] getChildren() {
            if (children != null) { return children; }
            if (bean == null || ClassUtils.isSimpleScalar(bean.getClass())) {
                return new Object[0];
            }
            else {
                PropertyDescriptor[] properties = beanWrapper
                        .getPropertyDescriptors();
                List tmpList = new ArrayList(properties.length);
                for (int i = 0; i < properties.length; i++) {
                    PropertyDescriptor desc = properties[i];
                    if (desc.getName().equals("class")) {
                        continue;
                    }
                    Object child = beanWrapper.getPropertyValue(desc.getName());
                    BeanFeatureNode newNode = new BeanFeatureNode(child, desc);
                    tmpList.add(newNode);
                }
                sortByTypeAndName(tmpList);
                this.children = (BeanFeatureNode[])tmpList
                        .toArray(new BeanFeatureNode[tmpList.size()]);
                return children;
            }
        }

        public void sortByTypeAndName(List nodeList) {
            Collections.sort(nodeList, TYPE_THEN_NAME_COMPARATOR);
        }
    }

    /**
     * @see org.springframework.richclient.treetable.TreeTableModel#getColumnCount()
     */
    public int getColumnCount() {
        return names.length;
    }

    /**
     * @see org.springframework.richclient.treetable.TreeTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        return names[column];
    }

    public Class getColumnClass(int column) {
        return clazzes[column];
    }

    /**
     * @see org.springframework.richclient.treetable.TreeTableModel#getValueAt(java.lang.Object,
     *      int)
     */
    public Object getValueAt(Object node, int column) {
        if (column == 0) {
            return node;
        }
        else if (column == 1) {
            BeanFeatureNode n = (BeanFeatureNode)node;
            if (!n.isSimpleScalar()) {
                return null;
            }
            else {
                return n.getBean();
            }
        }
        return null;
    }

    /**
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    public int getChildCount(Object parent) {
        return ((BeanFeatureNode)parent).getChildren().length;
    }

    /**
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    public Object getChild(Object parent, int index) {
        return ((BeanFeatureNode)parent).getChildren()[index];
    }

}