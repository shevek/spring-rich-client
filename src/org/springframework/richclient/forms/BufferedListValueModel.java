/*
 * $Header$
 * $Revision$
 * $Date$
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
            this.items.addAll(list);
        }
        return this.items;
    }

    protected void onWrappedValueChanged() {
        super.set(internalGet());
    }

    protected void fireValueChanged() {
        if (!updating) {
            super.fireValueChanged();
        }
    }
}