/*
 * $Header:
 * /cvsroot/spring-rich-c/spring-richclient/src/org/springframework/richclient/forms/BufferedListValueModel.java,v
 * 1.1 2004/08/03 04:53:31 kdonald Exp $ $Revision$ $Date: 2004/08/03
 * 04:53:31 $
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.forms;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.springframework.richclient.list.ListListModel;
import org.springframework.rules.values.BufferedValueModel;
import org.springframework.rules.values.ValueModel;

class BufferedListValueModel extends BufferedValueModel {
    private ListListModel itemsBuffer;

    private boolean updating;

    public BufferedListValueModel(ValueModel wrappedModel) {
        super(wrappedModel);
    }

    public Object get() {
        if (!isChangeBuffered()) {
            super.set(internalGet());
        }
        return super.get();
    }

    /**
     * Gets the list value associated with this value model, creating a list
     * model buffer containing its contents, suitable for manipulation.
     * 
     * @return The list model buffer
     */
    protected Object internalGet() {
        List itemsList = (List)getWrappedModel().get();
        if (this.itemsBuffer == null) {
            this.itemsBuffer = new ListListModel(itemsList);
            this.itemsBuffer.addListDataListener(new ListDataListener() {
                public void contentsChanged(ListDataEvent e) {
                    fireValueChanged();
                }

                public void intervalAdded(ListDataEvent e) {
                    fireValueChanged();
                }

                public void intervalRemoved(ListDataEvent e) {
                    fireValueChanged();
                }
            });
        }
        else {
            try {
                updating = true;
                this.itemsBuffer.clear();
                if (itemsList != null) {
                    this.itemsBuffer.addAll(itemsList);
                }
            }
            finally {
                updating = false;
            }
        }
        return this.itemsBuffer;
    }

    /**
     * Overriden to make a defensive copy of the list model contents before
     * setting. This is not needed if the target object makes the copy; but this
     * method assumes users are lazy and often forget to do that.
     */
    protected void doBufferedValueCommit(Object bufferedValue) {
        List list = (List)bufferedValue;
        getWrappedModel().set(new ArrayList(list));
    }

    protected void fireValueChanged() {
        if (!updating) {
            super.fireValueChanged();
        }
    }

    protected void onWrappedValueChanged() {
        if (!updating) {
            super.set(internalGet());
        }
    }

}