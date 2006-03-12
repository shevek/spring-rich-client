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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

import org.springframework.binding.value.ValueModel;

/**
 * 
 * @author Keith Donald
 */
public abstract class AbstractTreeModel implements TreeModel {

    private final RootHolderChangeHandler rootHolderChangeHandler = new RootHolderChangeHandler();

    private final EventListenerList listenerList = new EventListenerList();

    private Object root;

    private ValueModel rootHolder;

    protected AbstractTreeModel(Object root) {
        this.root = root;
    }

    protected AbstractTreeModel(ValueModel rootHolder) {
        this.rootHolder = rootHolder;
        this.root = rootHolder.getValue();
        this.rootHolder.addValueChangeListener(rootHolderChangeHandler);
    }

    /*
     * @see javax.swing.tree.TreeModel#getRoot()
     */
    public Object getRoot() {
        return root;
    }

    protected ValueModel getRootHolder() {
        return rootHolder;
    }

    /**
     * Adds a listener for the TreeModelEvent posted after the tree changes.
     * 
     * @see #removeTreeModelListener
     * @param l
     *            the listener to add
     */
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    /**
     * Removes a listener previously added with <B>addTreeModelListener() </B>.
     * 
     * @see #addTreeModelListener
     * @param l
     *            the listener to remove
     */
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    /**
     * Returns an array of all the tree model listeners registered on this
     * model.
     * 
     * @return all of this model's <code>TreeModelListener</code> s or an
     *         empty array if no tree model listeners are currently registered
     * 
     * @see #addTreeModelListener
     * @see #removeTreeModelListener
     */
    public TreeModelListener[] getTreeModelListeners() {
        return (TreeModelListener[]) listenerList.getListeners(TreeModelListener.class);
    }

    /**
     * Notifies all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created using the
     * parameters passed into the fire method.
     * 
     * @param source
     *            the node being changed
     * @param path
     *            the path to the root node
     * @param childIndices
     *            the indices of the changed elements
     * @param children
     *            the changed elements
     * @see EventListenerList
     */
    protected void fireTreeNodesChanged(Object[] path, int[] childIndices, Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new TreeModelEvent(this, path, childIndices, children);
                }
                ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created using the
     * parameters passed into the fire method.
     * 
     * @param source
     *            the node where new elements are being inserted
     * @param path
     *            the path to the root node
     * @param childIndices
     *            the indices of the new elements
     * @param children
     *            the new elements
     * @see EventListenerList
     */
    protected void fireTreeNodesInserted(Object[] path, int[] childIndices, Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new TreeModelEvent(this, path, childIndices, children);
                }
                ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created using the
     * parameters passed into the fire method.
     * 
     * @param source
     *            the node where elements are being removed
     * @param path
     *            the path to the root node
     * @param childIndices
     *            the indices of the removed elements
     * @param children
     *            the removed elements
     * @see EventListenerList
     */
    protected void fireTreeNodesRemoved(Object[] path, int[] childIndices, Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new TreeModelEvent(this, path, childIndices, children);
                }
                ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on
     * this event type. The event instance is lazily created using the
     * parameters passed into the fire method.
     * 
     * @param source
     *            the node where the tree model has changed
     * @param path
     *            the path to the root node
     * @param childIndices
     *            the indices of the affected elements
     * @param children
     *            the affected elements
     * @see EventListenerList
     */
    protected void fireTreeStructureChanged(Object[] path, int[] childIndices, Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new TreeModelEvent(this, path, childIndices, children);
                }
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }

    /**
     * The only event raised by this model is TreeStructureChanged with the root
     * as path, i.e. the whole tree has changed.
     */
    protected void fireRootNodeChanged(Object previousRoot) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new TreeModelEvent(this, new Object[] { previousRoot });
                }
                ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
            }
        }
    }

    /**
     * The only event raised by this model is TreeStructureChanged with the root
     * as path, i.e. the whole tree has changed.
     */
    protected void fireRootTreeStructureChanged(Object previousRoot) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new TreeModelEvent(this, new Object[] { previousRoot });
                }
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }

    protected void fireTreeNodeRemoved(Object[] path, int index, Object child) {
        fireTreeNodesRemoved(path, new int[] { index }, new Object[] { child });
    }

    protected void fireTreeNodeInserted(Object[] path, int index, Object child) {
        fireTreeNodesInserted(path, new int[] { index }, new Object[] { child });
    }

    protected void fireTreeStructureChanged(Object[] path, int index, Object child) {
        fireTreeStructureChanged(path, new int[] { index }, new Object[] { child });
    }

    protected void fireTreeNodeChanged(Object[] path, int index, Object child) {
        fireTreeNodesChanged(path, new int[] { index }, new Object[] { child });
    }

    private class RootHolderChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            fireRootTreeStructureChanged(root);
            root = AbstractTreeModel.this.rootHolder.getValue();
        }
    }
}