package org.springframework.binding.value.support;

import java.util.Arrays;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import junit.framework.TestCase;

public class ListListModelTests extends TestCase {

    private TestListDataListener listener;

    protected void setUp() throws Exception {
        listener = new TestListDataListener();
    }

    public void testAddAllCollection() {
        ListListModel model = new ListListModel(Arrays.asList(new Object[] { "1", "2", "3" }));
        model.addListDataListener(listener);
        model.addAll(Arrays.asList(new Object[] { "4", "5", "6" }));
        assertNotNull(listener.intervalAddedEvent);
        assertEquals(3, listener.intervalAddedEvent.getIndex0());
        assertEquals(5, listener.intervalAddedEvent.getIndex1());
    }

    private static class TestListDataListener implements ListDataListener {

        ListDataEvent contentsChangedEvent;

        ListDataEvent intervalAddedEvent;

        ListDataEvent intervalRemovedEvent;

        public void contentsChanged(ListDataEvent e) {
            contentsChangedEvent = e;
        }

        public void intervalAdded(ListDataEvent e) {
            intervalAddedEvent = e;
        }

        public void intervalRemoved(ListDataEvent e) {
            intervalRemovedEvent = e;
        }

    }
}
