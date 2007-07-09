/*
 * The Spring Framework is published under the terms of the Apache Software
 * License.
 */
package org.springframework.rules;

import java.util.Date;
import java.util.Locale;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.closure.Constraint;
import org.springframework.richclient.application.support.DefaultApplicationServices;
import org.springframework.richclient.test.SpringRichTestCase;
import org.springframework.rules.constraint.CompoundConstraint;
import org.springframework.rules.constraint.property.CompoundPropertyConstraint;
import org.springframework.rules.factory.Constraints;
import org.springframework.rules.reporting.AlternativeMessageTranslator;
import org.springframework.rules.reporting.BeanValidationResults;
import org.springframework.rules.reporting.BeanValidationResultsCollector;
import org.springframework.rules.reporting.DefaultMessageTranslator;
import org.springframework.rules.reporting.MessageTranslator;

/**
 * @author Keith Donald
 */
public class ValidationResultsTests extends SpringRichTestCase {

	static RulesSource rulesSource;
	static Rules rules;

	private static final Constraints constraints = Constraints.instance();
	
	protected void registerAdditionalServices(DefaultApplicationServices applicationServices) {
		applicationServices.setRulesSourceId("rulesSource");
	}
	
	protected void doSetUp() throws Exception {
		rulesSource = (RulesSource) applicationServices.getService(RulesSource.class);
		rules = rulesSource.getRules(Person.class);
	}

	protected ConfigurableApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("org/springframework/rules/rules-context.xml");
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
				r.getResults("firstName").buildMessage(Locale.getDefault());
		System.out.println(message);
		assertEquals(
				"First Name must have text and must be at least 2 characters or must *not* equal Last Name.",
				message);
	}
	
	public void testJan()
	{
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("greaterThan", Locale.getDefault(), "greater than {0}");
		messageSource.addMessage("greaterThanEqualTo", Locale.getDefault(), "at least {0}");
		messageSource.addMessage("lessThan", Locale.getDefault(), "less than {0}");
		messageSource.addMessage("lessThanEqualTo", Locale.getDefault(), "no more than {0}");
		messageSource.addMessage("equalTo", Locale.getDefault(), "equal {0}");
		messageSource.addMessage("required", Locale.getDefault(), "required");
		messageSource.addMessage("present", Locale.getDefault(), "present");
		messageSource.addMessage("typeMismatch", Locale.getDefault(), "{0} has an invalid format");
		messageSource.addMessage("unknown", Locale.getDefault(), "{0} has an invalid format");
		messageSource.addMessage("unique", Locale.getDefault(), "not already exist");
		messageSource.addMessage("range", Locale.getDefault(), "in range {0} to {1}");
		messageSource.addMessage("stringLengthConstraint", Locale.getDefault(), "must be {0} characters");
		messageSource.addMessage("fileChecks.FileExists", Locale.getDefault(), "does not exist");
		messageSource.addMessage("fileChecks.FileIsFile", Locale.getDefault(), "is a directory");
		messageSource.addMessage("fileChecks.FileIsReadable", Locale.getDefault(), "is not readable");
		messageSource.addMessage("and", Locale.getDefault(), "and");
		messageSource.addMessage("or", Locale.getDefault(), "or");
		messageSource.addMessage("not", Locale.getDefault(), "not");
		messageSource.addMessage("verb.default", Locale.getDefault(), "must be");
		messageSource.addMessage("verb.default.negated", Locale.getDefault(), "must not be");
		
		DefaultMessageTranslator defaultTranslator = new DefaultMessageTranslator(messageSource);
		constraintMessages(defaultTranslator);
	}
	
	public void testJan2()
	{
		StaticMessageSource messageSource = new StaticMessageSource();
		messageSource.addMessage("greaterThan", Locale.getDefault(), "{0} greater than {1}");
		messageSource.addMessage("greaterThanEqualTo", Locale.getDefault(), "at least {0}");
		messageSource.addMessage("lessThan", Locale.getDefault(), "{0} less than {1}");
		messageSource.addMessage("lessThanEqualTo", Locale.getDefault(), "{0} no more than {1}");
		messageSource.addMessage("equalTo", Locale.getDefault(), "{0} equals {1}");
		messageSource.addMessage("required", Locale.getDefault(), "{0} required");
		messageSource.addMessage("present", Locale.getDefault(), "{0} present");
		messageSource.addMessage("typeMismatch", Locale.getDefault(), "{0} has an invalid format");
		messageSource.addMessage("unknown", Locale.getDefault(), "{0} has an invalid format");
		messageSource.addMessage("unique", Locale.getDefault(), "{0} not already exist");
		messageSource.addMessage("range", Locale.getDefault(), "{0} in range {1} to {2}");
		messageSource.addMessage("stringLengthConstraint", Locale.getDefault(), "{0} must be {1} characters");
		messageSource.addMessage("fileChecks.FileExists", Locale.getDefault(), "{0} does not exist");
		messageSource.addMessage("fileChecks.FileIsFile", Locale.getDefault(), "{0} is a directory");
		messageSource.addMessage("fileChecks.FileIsReadable", Locale.getDefault(), "{0} is not readable");
		messageSource.addMessage("and", Locale.getDefault(), "and");
		messageSource.addMessage("or", Locale.getDefault(), "or");
		messageSource.addMessage("not", Locale.getDefault(), "not");
		messageSource.addMessage("verb.default", Locale.getDefault(), "must be");
		messageSource.addMessage("verb.default.negated", Locale.getDefault(), "must not be");
		
		messageSource.addMessage("parameterizedBinaryConstraint", Locale.getDefault(), "{0}");
		
		AlternativeMessageTranslator translator = new AlternativeMessageTranslator(messageSource);
		constraintMessages(translator);
	}
	
	private void constraintMessages(MessageTranslator translator)
	{
		Constraints c = Constraints.instance();
		String prefix = translator.getClass().getSimpleName() + ": ";
		System.out.println(prefix + translator.getMessage("lastName", null, c.required()));
		System.out.println(prefix + translator.getMessage(c.present("lastName")));
		System.out.println(prefix + translator.getMessage(c.value("age", c.gt(18))));
		System.out.println(prefix + translator.getMessage(c.gteProperty("age", "workyears")));
		System.out.println(prefix + translator.getMessage("age", null, c.range(18, 60)));
		System.out.println(prefix + translator.getMessage("age", null, c.lt(new Date())));
		
		
		CompoundConstraint constraint =
			constraints.or(
					constraints.all(
							"firstName",
							new Constraint[]{
								constraints.required(),
								constraints.minLength(2)}),
					constraints.not(
							constraints.eqProperty("firstName", "lastName")));
		System.out.println(prefix + translator.getMessage("firstName", null, constraint));		
	}
}
