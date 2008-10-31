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

package org.springframework.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.rules.constraint.Constraint;
import org.springframework.rules.closure.BinaryConstraint;
import org.springframework.rules.closure.StringLength;
import org.springframework.rules.constraint.*;
import org.springframework.rules.constraint.property.CompoundPropertyConstraint;
import org.springframework.rules.constraint.property.ParameterizedPropertyConstraint;
import org.springframework.rules.constraint.property.PropertiesConstraint;
import org.springframework.rules.constraint.property.PropertyConstraint;
import org.springframework.rules.constraint.property.PropertyValueConstraint;
import org.springframework.rules.constraint.property.RequiredIfOthersPresent;
import org.springframework.rules.factory.Constraints;
import org.springframework.util.Assert;

/**
 * @author Keith Donald
 */
public class RulesTests extends TestCase {

	private static final Constraints constraints = Constraints.instance();

	public void testRelationalPredicates() {
		Number n1 = new Integer(25);
		Number n11 = new Integer(25);
		Number n2 = new Integer(30);
		Number n3 = new Integer(-15);
		Number n4 = new Integer(26);
		BinaryConstraint p = GreaterThan.instance();
		Assert.notNull(p);
		assertTrue(p.test(n2, n1));
		assertFalse(p.test(n3, n2));

		p = GreaterThanEqualTo.instance();
		assertTrue(p.test(n2, n2));
		assertFalse(p.test(n1, n4));
		assertTrue(p.test(n4, n1));

		p = LessThan.instance();
		assertTrue(p.test(n1, n2));
		assertFalse(p.test(n2, n3));

		p = LessThanEqualTo.instance();
		assertTrue(p.test(n2, n2));
		assertFalse(p.test(n4, n1));
		assertTrue(p.test(n1, n4));

		p = EqualTo.instance();
		assertTrue(p.test(n1, n11));
		assertTrue(p.test(n2, n2));
		assertFalse(p.test(n1, n2));
	}

	public void testParameterizedBinaryPredicate() {
		Integer number = new Integer(25);
		ParameterizedBinaryConstraint p = new ParameterizedBinaryConstraint(GreaterThan.instance(), number);
		assertTrue(p.test(new Integer(26)));
		assertFalse(p.test(new Integer(24)));
	}

	public void testClosureResultConstraint() {
		String s = "12345";
		ClosureResultConstraint p = new ClosureResultConstraint(StringLength.instance(), constraints.bind(EqualTo
				.instance(), new Integer(s.length())));
		assertTrue(p.test(s));
		assertFalse(p.test("1234567"));
	}

	public void testInGroup() {
		String o1 = "o1";
		String o2 = "o2";
		String o3 = "o3";
		Set group = new HashSet();
		group.add(o1);
		group.add(o2);
		group.add(o3);
		Constraint p = constraints.inGroup(group);
		assertTrue(p.test("o1"));
		assertTrue(p.test(o1));
		assertFalse(p.test("o4"));
		p = constraints.inGroup(new Object[] { o1, o2, o1, o3 });
		assertTrue(p.test("o1"));
		assertTrue(p.test(o1));
		assertFalse(p.test("o4"));
	}

	public void testLike() {
		String keithDonald = "keith donald";
		String keith = "keith";
		String donald = "donald";
		Constraint p = constraints.like(keithDonald);
		assertTrue(p.test("keith donald"));
		assertFalse(p.test("Keith Donald"));

		p = constraints.like("%keith donald%");
		assertTrue(p.test("keith donald"));
		assertFalse(p.test("Keith Donald"));

		p = constraints.like("keith%");
		assertTrue(p.test(keithDonald));
		assertTrue(p.test(keith));
		assertFalse(p.test(donald));

		p = constraints.like("%donald");
		assertTrue(p.test(keithDonald));
		assertTrue(p.test(donald));
		assertFalse(p.test(keith));

	}

	public void testMethodInvokingRule() {
		TestBean b = new TestBean();
		Constraint p = constraints.method(b, "isTooMuch", "max");
		assertTrue(p.test(new Integer(26)));
		assertFalse(p.test(new Integer(25)));
	}

	public void testRegExpConstraint() {
		Constraint p = constraints.regexp("a*b");
		assertTrue(p.test("aaaaab"));
		assertFalse(p.test("bbbbbba"));
	}

	public void testRequired() {
		Required req = Required.instance();
		assertEquals("required", req.getType());
		emptyChecks(req);
	}

	public void testPresent() {
		Required req = Required.present();
		assertEquals("present", req.getType());
		emptyChecks(req);
	}

	private void emptyChecks(Required req) {
		assertFalse(req.test(""));
		assertFalse(req.test(null));
		assertFalse(req.test(new HashMap()));
		assertFalse(req.test(new ArrayList()));
		assertFalse(req.test(new Object[0]));

		assertTrue(req.test(new Integer(25)));
		assertTrue(req.test("25"));
		assertTrue(req.test(new Object[1]));
		Map map = new HashMap();
		map.put("1", "1");
		assertTrue(req.test(map));
		assertTrue(req.test(Arrays.asList(new Object[1])));
	}

	public void testRequiredIfOthersPresent() {
		Rules r = new Rules(Person.class);
		PropertyConstraint c = new RequiredIfOthersPresent("zip", "city,state");
		r.add(c);

		// Ensure that it properly reports all property dependencies
		assertTrue(c.isDependentOn("zip"));
		assertTrue(c.isDependentOn("city"));
		assertTrue(c.isDependentOn("state"));

		Person p = new Person();

		assertTrue(r.test(p)); // No city or state, so not required

		p.setCity("city");
		assertTrue(r.test(p)); // Need both city and state, so not required

		p.setState("state");
		assertFalse(r.test(p));

		p.setZip("zip");
		assertTrue(r.test(p));

		// Now test the OR version
		r = new Rules(Person.class);
		c = new RequiredIfOthersPresent("zip", "city,state", LogicalOperator.OR);
		r.add(c);

		assertTrue(c.isDependentOn("zip"));
		assertTrue(c.isDependentOn("city"));
		assertTrue(c.isDependentOn("state"));

		p = new Person();

		assertTrue(r.test(p)); // No city or state, so not required

		p.setCity("city");
		assertFalse(r.test(p)); // Need either city and state, so required

		p.setState("state");
		assertFalse(r.test(p));

		p.setZip("zip");
		assertTrue(r.test(p));
	}

	public void testMaxLengthConstraint() {
		Constraint p = new StringLengthConstraint(5);
		assertTrue(p.test(null));
		assertTrue(p.test(new Integer(12345)));
		assertFalse(p.test(new Integer(123456)));
		assertTrue(p.test("12345"));
		assertFalse(p.test("123456"));
	}

	public void testMinLengthConstraint() {
		Constraint p = new StringLengthConstraint(RelationalOperator.GREATER_THAN_EQUAL_TO, 5);
		assertFalse(p.test(null));
		assertTrue(p.test(new Integer(12345)));
		assertFalse(p.test(new Integer(1234)));
		assertTrue(p.test("1234567890"));
		assertFalse(p.test("1234"));
	}

	public void testRangeConstraint() {
		Constraint p = new Range(new Integer(0), new Integer(10));
		assertTrue(p.test(new Integer(0)));
		assertTrue(p.test(new Integer(10)));
		assertFalse(p.test(new Integer(-1)));
		assertFalse(p.test(new Integer(11)));
	}

	public void testAnd() {
		And and = new And();
		and.add(Required.instance());
		and.add(new StringLengthConstraint(5));
		assertTrue(and.test("12345"));
		assertFalse(and.test("123456"));
		assertFalse(and.test(""));
	}

	public void testOr() {
		Or or = new Or();
		or.add(Required.instance());
		or.add(new StringLengthConstraint(5));
		assertTrue(or.test("12345"));
		assertTrue(or.test("123456"));
		assertFalse(or.test("           "));
	}

	public void testXOr() {
		XOr xor = new XOr();
		xor.add(new InGroup(new String[] { "123", "12345" }));
		xor.add(new InGroup(new String[] { "1234", "12345" }));
		assertTrue(xor.test("123"));
		assertTrue(xor.test("1234"));
		assertFalse(xor.test("           "));
		assertFalse(xor.test("12345"));
	}

	public void testNot() {
		Number n = new Integer("25");
		Constraint p = constraints.bind(EqualTo.instance(), n);
		Not not = new Not(p);
		assertTrue(not.test(new Integer(24)));
		assertFalse(not.test(new Integer("25")));
	}

	public void testBeanPropertyValueConstraint() {
		And p = constraints.conjunction();
		p.add(constraints.required());
		p.add(constraints.maxLength(9));
		PropertyConstraint e = new PropertyValueConstraint("test", p);
		assertTrue(e.test(new TestBean()));

		p = constraints.conjunction();
		e = new PropertyValueConstraint("test", p);
		p.add(constraints.required());
		p.add(constraints.maxLength(3));
		assertFalse(e.test(new TestBean()));
	}

	public void testBeanPropertiesExpression() {
		PropertiesConstraint p = new PropertiesConstraint("test", EqualTo.instance(), "confirmTest");
		assertTrue(p.test(new TestBean()));

		p = new PropertiesConstraint("test", EqualTo.instance(), "min");
		assertFalse(p.test(new TestBean()));
	}

	public void testParameterizedBeanPropertyExpression() {
		ParameterizedPropertyConstraint p = new ParameterizedPropertyConstraint("test", EqualTo.instance(), "testValue");
		assertTrue(p.test(new TestBean()));

		p = new ParameterizedPropertyConstraint("test", EqualTo.instance(), "test2Value");
		assertFalse(p.test(new TestBean()));
	}

	public void testNoRules() {
		Rules r = new Rules(TestBean.class);
		assertTrue(r.test(new TestBean()));
	}

	public void testMinMaxRules() {
		Rules r = new Rules(TestBean.class);
		r.add(constraints.inRangeProperties("number", "min", "max"));
		assertTrue(r.test(new TestBean()));
		TestBean b = new TestBean();
		b.number = -1;
		assertFalse(r.test(b));
	}

	public void testBasicCompoundRules() {
		Rules r = new Rules(TestBean.class);
		r.add(constraints.inRangeProperties("number", "min", "max")).add(constraints.eqProperty("test", "confirmTest"));
		assertTrue(r.test(new TestBean()));

		r.add("test2", constraints.maxLength(4));
		assertFalse(r.test(new TestBean()));
	}

	public void testCompoundRules() {
		Rules r = new Rules(TestBean.class);
		// test must be required, and have a length in range 3 to 25
		// or test must just equal confirmTest
		CompoundPropertyConstraint rules = new CompoundPropertyConstraint(constraints.or(constraints.all("test",
				new Constraint[] { constraints.required(), constraints.maxLength(25), constraints.minLength(3) }),
				constraints.eqProperty("test", "confirmTest")));
		r.add(rules);
		assertTrue(r.test(new TestBean()));
		TestBean b = new TestBean();
		b.test = "a";
		b.confirmTest = "a";
		assertTrue(r.test(b));

		b.test = null;
		b.confirmTest = null;
		assertTrue(r.test(b));

		b.test = "hi";
		assertFalse(r.test(b));
	}

	public void testDefaultRulesSource() {
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext(
				"org/springframework/rules/rules-context.xml");
		RulesSource rulesSource = (RulesSource) ac.getBean("rulesSource");
		Rules rules = rulesSource.getRules(Person.class);
		assertTrue(rules != null);
		Person p = new Person();
		assertFalse(rules.test(p));
		p.setFirstName("Keith");
		p.setLastName("Donald");
		assertTrue(rules.test(p));
		p.setLastName("Keith");
		assertFalse(rules.test(p));
	}

	public class TestBean {

		private String test = "testValue";

		private String confirmTest = "testValue";

		private String test2 = "test2Value";

		private int number = 15;

		private int min = 10;

		private int max = 25;

		public String getTest() {
			return test;
		}

		public String getTest2() {
			return test2;
		}

		public String getConfirmTest() {
			return confirmTest;
		}

		public int getNumber() {
			return number;
		}

		public int getMax() {
			return max;
		}

		public int getMin() {
			return min;
		}

		public boolean isTooMuch(int number) {
			return number > max;
		}
	}

}
