/*
 * Copyright 2002-2006 the original author or authors.
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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.easymock.EasyMock;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Provides a suite of unit tests for the {@link EventListenerListHelper} class.
 * 
 * @author Kevin Stembridge
 * @since 0.3.0
 * 
 */
public class EventListenerListHelperTests extends TestCase {

	/**
	 * Test method for
	 * {@link EventListenerListHelper#EventListenerListHelper(java.lang.Class)}.
	 * Confirms that this constructor throws an IllegalArgumentException if
	 * passed a null argument.
	 */
	public void testEventListenerListHelper() {

		try {
			new EventListenerListHelper(null);
			Assert.fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing, test succeeded
		}

	}

	/**
	 * Test method for {@link EventListenerListHelper#hasListeners()}.
	 */
	public void testHasListeners() {

		EventListenerListHelper listHelper = new EventListenerListHelper(Object.class);
		Assert.assertFalse("Assert list helper has no listeners", listHelper.hasListeners());
		listHelper.add(new Object());
		Assert.assertTrue("Assert list helper has listeners", listHelper.hasListeners());

	}

	/**
	 * Test method for {@link EventListenerListHelper#isEmpty()}.
	 */
	public void testIsEmpty() {

		EventListenerListHelper listHelper = new EventListenerListHelper(Object.class);
		Assert.assertTrue("Assert list helper is empty", listHelper.isEmpty());
		listHelper.add(new Object());
		Assert.assertFalse("Assert list helper is not empty", listHelper.isEmpty());

	}

	/**
	 * Test method for {@link EventListenerListHelper#getListenerCount()}.
	 */
	public void testGetListenerCount() {

		Object listener1 = new Object();
		Object listener2 = new Object();
		Object listener3 = new Object();

		EventListenerListHelper listHelper = new EventListenerListHelper(Object.class);

		Assert.assertEquals(0, listHelper.getListenerCount());
		listHelper.add(listener1);
		Assert.assertEquals(1, listHelper.getListenerCount());
		listHelper.add(listener2);
		Assert.assertEquals(2, listHelper.getListenerCount());
		listHelper.add(listener3);
		Assert.assertEquals(3, listHelper.getListenerCount());
		listHelper.remove(listener1);
		Assert.assertEquals(2, listHelper.getListenerCount());
		listHelper.remove(listener2);
		Assert.assertEquals(1, listHelper.getListenerCount());
		listHelper.remove(listener3);
		Assert.assertEquals(0, listHelper.getListenerCount());

	}

	/**
	 * Test method for {@link EventListenerListHelper#getListeners()}.
	 */
	public void testGetListeners() {

		EventListenerListHelper listHelper = new EventListenerListHelper(Object.class);
		Object listener1 = new Object();
		Object listener2 = new Object();

		listHelper.addAll(new Object[] { listener1, listener2 });

		Object[] listeners = listHelper.getListeners();
		Assert.assertEquals(2, listeners.length);
		Assert.assertEquals(listener1, listeners[0]);
		Assert.assertEquals(listener2, listeners[1]);

	}

	/**
	 * Test method for {@link EventListenerListHelper#iterator()}.
	 */
	public void testIterator() {

		EventListenerListHelper listHelper = new EventListenerListHelper(Object.class);
		Object listener1 = new Object();
		Object listener2 = new Object();
		Iterator itr = listHelper.iterator();

		Assert.assertFalse("Assert iterator.hasNext() returns false", itr.hasNext());
		listHelper.add(listener1);
		listHelper.add(listener2);
		Assert.assertFalse("Assert iterator.hasNext() returns false", itr.hasNext());
		itr = listHelper.iterator();
		Assert.assertTrue("Assert iterator.hasNext() returns true", itr.hasNext());
		Assert.assertEquals(listener1, itr.next());
		Assert.assertTrue("Assert iterator.hasNext() returns true", itr.hasNext());
		Assert.assertEquals(listener2, itr.next());
		Assert.assertFalse("Assert iterator.hasNext() returns false", itr.hasNext());

		try {
			itr.next();
			fail("Should have thrown a NoSuchElementException");
		}
		catch (NoSuchElementException e) {
			// do nothing, test succeeded
		}

	}

	/**
	 * Test method for {@link EventListenerListHelper#fire(java.lang.String)}.
	 */
	public void testFireByMethodName() {

		EventListenerListHelper listHelper = new EventListenerListHelper(DummyEventListener.class);

		DummyEventListener listener1 = (DummyEventListener) EasyMock.createMock(DummyEventListener.class);
		DummyEventListener listener2 = (DummyEventListener) EasyMock.createMock(DummyEventListener.class);
		listener1.onEvent1();
		listener2.onEvent1();
		EasyMock.replay(listener1);
		EasyMock.replay(listener2);

		listHelper.add(listener1);
		// if listener is added to list helper twice, should still only receive
		// one event notification
		listHelper.add(listener1);
		listHelper.add(listener2);

		listHelper.fire("onEvent1");

		try {
			listHelper.fire(null);
			Assert.fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing
		}

		try {
			listHelper.fire("bogusEventName");
			Assert.fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing
		}

		EasyMock.verify(listener1);
		EasyMock.verify(listener2);

	}

	/**
	 * Test method for
	 * {@link EventListenerListHelper#fire(java.lang.String, java.lang.Object)}.
	 */
	public void testFireByMethodNameWithOneArg() {

		EventListenerListHelper listHelper = new EventListenerListHelper(DummyEventListener.class);
		String arg1 = "arg1";

		DummyEventListener listener1 = (DummyEventListener) EasyMock.createMock(DummyEventListener.class);
		DummyEventListener listener2 = (DummyEventListener) EasyMock.createMock(DummyEventListener.class);
		listener1.onEvent2(arg1);
		listener1.onEvent2(null);
		listener2.onEvent2(arg1);
		listener2.onEvent2(null);
		EasyMock.replay(listener1);
		EasyMock.replay(listener2);

		listHelper.add(listener1);
		// if listener is added to list helper twice, should still only receive
		// one event notification
		listHelper.add(listener1);
		listHelper.add(listener2);

		listHelper.fire("onEvent2", arg1);
		listHelper.fire("onEvent2", (Object) null);

		try {
			listHelper.fire("bogusEventName", arg1);
			Assert.fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing
		}

		try {
			listHelper.fire(null, arg1);
			Assert.fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing
		}

		EasyMock.verify(listener1);
		EasyMock.verify(listener2);

	}

	/**
	 * Test method for
	 * {@link EventListenerListHelper#fire(java.lang.String, java.lang.Object, java.lang.Object)}.
	 */
	public void testFireByMethodNameWithTwoArgs() {

		EventListenerListHelper listHelper = new EventListenerListHelper(DummyEventListener.class);
		String arg1 = "arg1";
		String arg2 = "arg2";

		DummyEventListener listener1 = (DummyEventListener) EasyMock.createMock(DummyEventListener.class);
		DummyEventListener listener2 = (DummyEventListener) EasyMock.createMock(DummyEventListener.class);
		listener1.onEvent3(arg1, arg2);
		listener1.onEvent3(null, null);
		listener2.onEvent3(null, null);
		listener2.onEvent3(arg1, arg2);
		EasyMock.replay(listener1);
		EasyMock.replay(listener2);

		listHelper.add(listener1);
		// if listener is added to list helper twice, should still only receive
		// one event notification
		listHelper.add(listener1);
		listHelper.add(listener2);

		listHelper.fire("onEvent3", arg1, arg2);
		listHelper.fire("onEvent3", null, null);

		try {
			listHelper.fire("bogusEventName", arg1, arg2);
			Assert.fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing
		}

		try {
			listHelper.fire(null, arg1, arg2);
			Assert.fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing
		}

		EasyMock.verify(listener1);
		EasyMock.verify(listener2);

	}

	/**
	 * Test method for
	 * {@link org.springframework.richclient.util.EventListenerListHelper#fire(java.lang.String, java.lang.Object[])}.
	 */
	public void testFireByMethodNameWithArrayArg() {

		EventListenerListHelper listHelper = new EventListenerListHelper(DummyEventListener.class);
		String arg1 = "arg1";
		String arg2 = "arg2";
		String arg3 = "arg3";

		Object[] args = new Object[] { arg1, arg2, arg3 };

		DummyEventListener listener1 = (DummyEventListener) EasyMock.createMock(DummyEventListener.class);
		DummyEventListener listener2 = (DummyEventListener) EasyMock.createMock(DummyEventListener.class);
		listener1.onEvent4(args);
		listener1.onEvent4(null);
		listener2.onEvent4(args);
		listener2.onEvent4(null);
		EasyMock.replay(listener1);
		EasyMock.replay(listener2);

		listHelper.add(listener1);
		// if listener is added to list helper twice, should still only receive
		// one event notification
		listHelper.add(listener1);
		listHelper.add(listener2);

		// The cast to Object here, and below, is a workaround for varargs
		// conversion in Java 5
		listHelper.fire("onEvent4", (Object) args);
		listHelper.fire("onEvent4", (Object) null);

		try {
			listHelper.fire("bogusEventName", (Object) args);
			Assert.fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing
		}

		try {
			listHelper.fire(null, (Object) args);
			Assert.fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing
		}

		EasyMock.verify(listener1);
		EasyMock.verify(listener2);

	}

	/**
	 * Test method for {@link EventListenerListHelper#add(java.lang.Object)}.
	 */
	public void testAdd() {

		EventListenerListHelper listHelper = new EventListenerListHelper(String.class);

		Assert.assertFalse("Assert adding a null listener returns false", listHelper.add(null));
		String listener1 = "bogusListener";
		Assert.assertTrue("Assert adding a new listener returns true", listHelper.add(listener1));
		Assert.assertFalse("Assert adding an existing listener returns false", listHelper.add(listener1));

		try {
			listHelper.add(new Object());
			fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing, test succeeded
		}

	}

	/**
	 * Test method for
	 * {@link org.springframework.richclient.util.EventListenerListHelper#addAll(java.lang.Object[])}.
	 */
	public void testAddAll() {

		EventListenerListHelper listHelper = new EventListenerListHelper(String.class);

		Assert.assertFalse("Assert adding a null array of listeners returns false", listHelper.addAll(null));

		String listener1 = "listener1";
		String listener2 = "listener2";
		String[] listenerArray = new String[] { listener1, listener2 };

		Assert.assertTrue("Assert adding an array of new listeners returns true", listHelper.addAll(listenerArray));
		Assert.assertFalse("Assert adding same listeners returns false", listHelper.addAll(listenerArray));

		String[] listenerArray2 = new String[] { "newListener", listener1 };

		Assert.assertTrue("Assert adding array with one new listener returns true", listHelper.addAll(listenerArray2));

		Object[] listenerArray3 = new Object[] { listener1, new Object() };

		try {
			listHelper.addAll(listenerArray3);
			Assert.fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing, test succeeded
		}

	}

	/**
	 * Test method for {@link EventListenerListHelper#remove(java.lang.Object)}.
	 */
	public void testRemove() {

		EventListenerListHelper listHelper = new EventListenerListHelper(String.class);

		String listener1 = "listener1";

		listHelper.add(listener1);

		Assert.assertEquals(1, listHelper.getListenerCount());

		listHelper.remove("bogusListener");

		Assert.assertEquals(1, listHelper.getListenerCount());

		listHelper.remove(listener1);

		Assert.assertEquals(0, listHelper.getListenerCount());

		try {
			listHelper.remove(new Object());
			Assert.fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing, test succeeded
		}

		// TODO why does this method need to throw an IllegalArgEx?
		try {
			listHelper.remove(null);
			Assert.fail("Should have thrown an IllegalArgumentException");
		}
		catch (IllegalArgumentException e) {
			// do nothing, test succeeded
		}

	}

	/**
	 * Test method for
	 * {@link org.springframework.richclient.util.EventListenerListHelper#clear()}.
	 */
	public void testClear() {

		EventListenerListHelper listHelper = new EventListenerListHelper(Object.class);

		listHelper.clear();

		Assert.assertEquals(0, listHelper.getListenerCount());
		listHelper.add(new Object());
		Assert.assertEquals(1, listHelper.getListenerCount());
		listHelper.clear();
		Assert.assertEquals(0, listHelper.getListenerCount());

	}

	/**
	 * Test method for
	 * {@link org.springframework.richclient.util.EventListenerListHelper#toArray()}.
	 */
	public void testToArray() {

		Object listener1 = new Object();
		Object listener2 = new Object();

		Object[] listenerArray = new Object[] { listener1, listener2 };

		EventListenerListHelper listHelper = new EventListenerListHelper(Object.class);

		listHelper.addAll(listenerArray);

		Object[] listenersCopy = (Object[]) listHelper.toArray();

		Assert.assertEquals(listenerArray.length, listenersCopy.length);

		for (int i = 0; i < listenerArray.length; i++) {
			Assert.assertEquals(listenerArray[i], listenersCopy[i]);
		}

	}

	private interface DummyEventListener {

		public void onEvent1();

		public void onEvent2(Object arg1);

		public void onEvent3(Object arg1, Object arg2);

		public void onEvent4(Object[] args);

	}

}
