/*
 * Copyright 2004-2005 the original author or authors.
 */
package org.springframework.core.closure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.core.closure.support.Block;
import org.springframework.core.closure.support.IteratorTemplate;
import org.springframework.richclient.test.SpringRichTestCase;

/**
 * @author Keith Donald
 */
public class ClosureTests extends SpringRichTestCase {
	public void testIteratorProcessTemplateRunOnce() {
		List collection = new ArrayList();
		collection.add("Item 1");
		collection.add("Item 2");
		collection.add("Item 3");
		collection.add("Item 4");
		collection.add("Item 5");
		IteratorTemplate template = new IteratorTemplate(collection.iterator());
		assertTrue(template.allTrue(new Constraint() {
			public boolean test(Object o) {
				return ((String)o).startsWith("Item");
			}
		}));
		try {
			assertEquals(false, template.allTrue(new Constraint() {
				public boolean test(Object o) {
					return ((String)o).startsWith("Element");
				}
			}));
			fail("Should have failed");
		}
		catch (UnsupportedOperationException e) {

		}
	}

	public void testIteratorProcessTemplateRunMultiple() {
		List collection = new ArrayList();
		collection.add("Item 1");
		collection.add("Item 2");
		collection.add("Item 3");
		collection.add("Item 4");
		collection.add("Item 5");
		IteratorTemplate template = new IteratorTemplate(collection);
		assertTrue(template.allTrue(new Constraint() {
			public boolean test(Object o) {
				return ((String)o).startsWith("Item");
			}
		}));
		assertEquals(false, template.allTrue(new Constraint() {
			public boolean test(Object o) {
				return ((String)o).startsWith("Element");
			}
		}));
	}

	public void testAnyTrue() {
		List collection = new ArrayList();
		collection.add("Item 1");
		collection.add("Item 2");
		collection.add("Item 3");
		collection.add("Item 4");
		collection.add("Item 5");
		IteratorTemplate template = new IteratorTemplate(collection);
		assertTrue(template.anyTrue(new Constraint() {
			public boolean test(Object o) {
				return ((String)o).startsWith("Item 5");
			}
		}));
		assertEquals(false, template.anyTrue(new Constraint() {
			public boolean test(Object o) {
				return ((String)o).startsWith("Element");
			}
		}));
	}

	public void testFindFirst() {
		List collection = new ArrayList();
		collection.add("Item 1");
		collection.add("Item 2");
		collection.add("Item 3");
		collection.add("Item 4");
		collection.add("Item 5");
		IteratorTemplate template = new IteratorTemplate(collection);
		assertEquals("Item 4", template.findFirst(new Constraint() {
			public boolean test(Object o) {
				return ((String)o).startsWith("Item 4");
			}
		}));
		assertEquals(null, template.findFirst(new Constraint() {
			public boolean test(Object o) {
				return ((String)o).startsWith("Element");
			}
		}));
	}

	public void testFindAll() {
		List collection = new ArrayList();
		collection.add("Item 1");
		collection.add("Item 2");
		collection.add("Item 3");
		collection.add("Item 4");
		collection.add("Item 5");
		IteratorTemplate template = new IteratorTemplate(collection);
		ElementGenerator finder = template.findAll(new Constraint() {
			public boolean test(Object o) {
				return ((String)o).startsWith("Item 4");
			}
		});
		finder.run(new Block() {
			protected void handle(Object o) {
				assertEquals("Item 4", o);
			}
		});
		finder = template.findAll(new Constraint() {
			public boolean test(Object o) {
				return ((String)o).startsWith("Element");
			}
		});
		finder.run(new Block() {
			protected void handle(Object o) {
				fail("Should not be called");
			}
		});
	}

	public void testRunUntil() {
		List collection = new ArrayList();
		collection.add("Item 1");
		collection.add("Item 2");
		collection.add("Item 3");
		collection.add("Item 4");
		collection.add("Item 5");
		IteratorTemplate template = new IteratorTemplate(collection);
		final ValueModel countHolder = new ValueHolder(new Integer(0));
		template.runUntil(new Block() {
			protected void handle(Object o) {
				countHolder.setValue(new Integer(((Integer)countHolder.getValue()).intValue() + 1));
			}
		}, new Constraint() {
			public boolean test(Object o) {
				return o.equals("Item 4");
			}
		});
		assertEquals(new Integer(3), countHolder.getValue());
	}
}