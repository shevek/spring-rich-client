/*
 * $Header:
 * /cvsroot/spring-rich-c/spring-richclient/src/org/springframework/richclient/forms/BufferedListValueModel.java,v
 * 1.1 2004/08/03 04:53:31 kdonald Exp $ $Revision$ $Date: 2004/08/03
 * 04:53:31 $
 * 
 * Copyright Computer Science Innovations (CSI), 2004. All rights reserved.
 */
package org.springframework.richclient.forms;

import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.springframework.richclient.list.ListListModel;
import org.springframework.rules.values.BufferedValueModel;
import org.springframework.rules.values.ValueModel;

class BufferedListValueModel extends BufferedValueModel {
    private ListListModel items;

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

    protected Object internalGet() {
        if (this.items == null) {
            this.items = new ListListModel();
            this.items.addListDataListener(new ListDataListener() {
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
                this.items.clear();
            }
            finally {
                updating = false;
            }
        }
        List list = (List)getWrappedModel().get();
        if (list != null) {
            try {
                updating = true;
                this.items.addAll(list);
            }
            finally {
                updating = false;
            }
        }
        return this.items;
    }

    protected void onWrappedValueChanged() {
        if (!updating) {
            super.set(internalGet());
        }
    }

    protected void fireValueChanged() {
        if (!updating) {
            super.fireValueChanged();
        }
    }
}