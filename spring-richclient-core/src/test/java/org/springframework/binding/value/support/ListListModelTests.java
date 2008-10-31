/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.binding.value.support;

import java.util.Arrays;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import junit.framework.TestCase;

import org.easymock.EasyMock;

/**
 * Testcase for <code>ListListModel</code>
 * 
 * @author Keith Donald
 * @author Peter De Bruycker
 */
public class ListListModelTests extends TestCase {

	private ListDataListener mockListener;

	protected void setUp() throws Exception {
		mockListener = (ListDataListener) EasyMock.createMock(ListDataListener.class);
	}

	public void testAddAllCollection() {
		ListListModel model = new ListListModel(Arrays.asList(new Object[] { "1", "2", "3" }));
		model.addListDataListener(mockListener);

		mockListener.intervalAdded(eq(new ListDataEvent(model, ListDataEvent.INTERVAL_ADDED, 3, 5)));
		EasyMock.replay(mockListener);

		model.addAll(Arrays.asList(new Object[] { "4", "5", "6" }));

		assertEquals(Arrays.asList(new Object[] { "1", "2", "3", "4", "5", "6" }), model);

		EasyMock.verify(mockListener);
	}

	public void testRetainAll() {
		ListListModel model = new ListListModel(Arrays.asList(new Object[] { "1", "2", "3", "4", "5" }));
		model.addListDataListener(mockListener);

		mockListener.contentsChanged(eq(new ListDataEvent(model, ListDataEvent.CONTENTS_CHANGED, -1, -1)));
		EasyMock.replay(mockListener);

		model.retainAll(Arrays.asList(new Object[] { "2", "5" }));

		assertEquals(Arrays.asList(new Object[] { "2", "5" }), model);

		EasyMock.verify(mockListener);
	}

	public void testRemoveAll() {
		ListListModel model = new ListListModel(Arrays.asList(new Object[] { "1", "2", "3", "4", "5" }));
		model.addListDataListener(mockListener);

		mockListener.contentsChanged(eq(new ListDataEvent(model, ListDataEvent.CONTENTS_CHANGED, -1, -1)));
		EasyMock.replay(mockListener);

		model.removeAll(Arrays.asList(new Object[] { "2", "5" }));

		assertEquals(Arrays.asList(new Object[] { "1", "3", "4" }), model);

		EasyMock.verify(mockListener);
	}

	public void testRemove() {
		ListListModel model = new ListListModel(Arrays.asList(new Object[] { "1", "2", "3" }));
		model.addListDataListener(mockListener);

		mockListener.intervalRemoved(eq(new ListDataEvent(model, ListDataEvent.INTERVAL_REMOVED, 1, 1)));
		EasyMock.replay(mockListener);

		model.remove(1);

		assertEquals(Arrays.asList(new Object[] { "1", "3" }), model);

		EasyMock.verify(mockListener);
	}

	public void testRemoveObject() {
		ListListModel model = new ListListModel(Arrays.asList(new Object[] { "1", "2", "3" }));
		model.addListDataListener(mockListener);

		mockListener.intervalRemoved(eq(new ListDataEvent(model, ListDataEvent.INTERVAL_REMOVED, 1, 1)));
		EasyMock.replay(mockListener);

		model.remove("2");

		assertEquals(Arrays.asList(new Object[] { "1", "3" }), model);

		EasyMock.verify(mockListener);
	}

	public static ListDataEvent eq(ListDataEvent expected) {
		EasyMock.reportMatcher(new ListDataEventArgumentMatcher(expected));

		return null;
	}
}
