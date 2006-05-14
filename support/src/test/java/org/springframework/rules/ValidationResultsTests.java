/*
 * The Spring Framework is published under the terms of the Apache Software
 * License.
 */
package org.springframework.rules;

import java.util.Locale;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.closure.Constraint;
import org.springframework.rules.constraint.CompoundConstraint;
import org.springframework.rules.constraint.property.CompoundPropertyConstraint;
import org.springframework.rules.factory.Constraints;
import org.springframework.rules.reporting.BeanValidationResults;
import org.springframework.rules.reporting.BeanValidationResultsCollector;

/**
 * @author Keith Donald
 */
public class ValidationResultsTests extends TestCase {

	static ClassPathXmlApplicationContext ac;
	static RulesSource rulesSource;
	static Rules rules;

	private static final Constraints constraints = Constraints.instance();

	public void setUp() {
		ac = new ClassPathXmlApplicationContext("org/springframework/rules/rules-context.xml");
		rulesSource = (RulesSource) ac.getBean("rulesSource");
		rules = rulesSource.getRules(Person.class);
	}

	public void testValidationResultsCollector() {
		Person p = new Person();
		BeanValidationResultsCollector c = new BeanValidationResultsCollector(p);
		BeanValidationResults r =
				c.collectResults(rulesSource.getRules(Person.class));
		assertEquals(2, r.getViolatedCount());
	}

	public void testValidationResultsCollectorCollectAllErrors() {
		Person p = new Person();
		BeanValidationResultsCollector c = new BeanValidationResultsCollector(p);
		c.setCollectAllErrors(true);
		BeanValidationResults r =
				c.collectResults(rulesSource.getRules(Person.class));
		assertEquals(2, r.getViolatedCount());
	}

	public void testNestedValidationResultsPropertyConstraint() {
		Person p = new Person();

		Rules rules = new Rules(Person.class);
		CompoundConstraint constraint =
				constraints.or(
						constraints.all(
								"firstName",
								new Constraint[]{
									constraints.required(),
									constraints.minLength(2)}),
						constraints.not(
								constraints.eqProperty("firstName", "lastName")));
		rules.add(new CompoundPropertyConstraint(constraint));
		BeanValidationResultsCollector c = new BeanValidationResultsCollector(p);
		c.setCollectAllErrors(true);
		BeanValidationResults r = c.collectResults(rules);
		assertEquals(3, r.getViolatedCount());
		String message =
				r.getResults("firstName").buildMessage(ac, Locale.getDefault());
		System.out.println(message);
		assertEquals(
				"First Name must have text and must be at least 2 characters or must *not* equal Last Name.",
				message);
	}
	
}
