package org.springframework.richclient.application.event;

import junit.framework.TestCase;

public class LifecycleApplicationEventTests extends TestCase {

	private class Base {
	}

	private class Child extends Base {
	}

	/**
	 * Simple test to check {@link LifecycleApplicationEvent#objectIs(Class)}.
	 */
	public void testEventObjectType() {
		Child child = new Child();
		LifecycleApplicationEvent event = new LifecycleApplicationEvent(LifecycleApplicationEvent.CREATED, child);
		assertTrue("Child extends Base so objectIs() should return true.", event.objectIs(Base.class));
	}

}
